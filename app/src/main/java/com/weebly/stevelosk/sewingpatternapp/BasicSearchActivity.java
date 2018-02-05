package com.weebly.stevelosk.sewingpatternapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BasicSearchActivity extends AppCompatActivity {

    private String searchString = "";
    private EditText searchEditText;
    private TextView resultsTextView;
    private ArrayList<Pattern> patterns = new ArrayList<>();
    private ImageView placeHolderImage;
    private ListView resultsListView;


    // a Timer to wait between text changes before firing a search event
    private Timer textChangedTimer;
    protected int TIMER_DELAY = 100;

    private PatternAdapter pa = null;
    private PatternDBAdapter db = null;
    private AsyncSearchTask searchTask = null;

    private final String TAG = "SDL BasicSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_search);
        // open database onCreate
        db = new PatternDBAdapter(this);
        db.open();

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        resultsTextView = (TextView) findViewById(R.id.searchActivity_resultsTextView);
        placeHolderImage = (ImageView) findViewById(R.id.placeHolderImage);

        // ListView
        resultsListView = (ListView) findViewById(R.id.basicSearch_ListView);
        pa = new PatternAdapter(this, patterns);
        resultsListView.setAdapter(pa);

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Pattern p = patterns.get(i);

                Intent examineIntent = new Intent(getApplicationContext(),
                        ExaminePatternActivity.class);
                examineIntent.putExtra("PASSED_PATTERN_INSTANCE", p);
                startActivity(examineIntent);
            }
        });

        placeHolderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask searchTask = new AsyncSearchTask();
                Object[] params = {getApplicationContext(),
                        searchEditText.getText().toString(), patterns};
                searchTask.execute(params);

            }
        });


        // TODO: use Handler instead?
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (textChangedTimer != null)
                    textChangedTimer.cancel();
            }

            @Override
            public void afterTextChanged(final Editable s) {
                //avoid triggering event when time is too short

                textChangedTimer = new Timer();
                textChangedTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (searchTask != null) {
                            // cancel previously running search, a search might have started
                            // before the user was done entering text
                            // could make a difference for a large database
                            searchTask.cancel(true);
                        }
                        searchTask = new AsyncSearchTask();
                        Object[] params = {getApplicationContext(),
                                searchEditText.getText().toString(), patterns};
                        searchTask.execute(params);

                    }
                }, TIMER_DELAY);

            }
        });
    }


    class AsyncSearchTask extends AsyncTask<Object, Void, Integer> {

        private static final int ASYNC_TASK_CANCELLED = -1;
        private static final int ASYNC_TASK_COMPLETED = 0;

        @Override
        protected Integer doInBackground(Object... objects) {

            // create database connection
            Context ctx = (Context) objects[0];

            // get query content and result set target from packaged objects
            String searchStr = (String) objects[1];
            ArrayList<Pattern> patterns = (ArrayList<Pattern>) objects[2];

            try {

                // clear the old result list
                patterns.clear();

                // query
                String[] predicate = new String[1];
                // surround search string with "%" so partial matches can be found
                predicate[0] = "%" + searchStr + "%";
                Cursor cursor = db.getPatternByLikeID(predicate);
                if (this.isCancelled()) {
                    return ASYNC_TASK_CANCELLED;
                }

                // populate the results from the query result set
                while (cursor.moveToNext()) {
                    Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
                    patterns.add(p);
                    if (this.isCancelled()) {
                        return ASYNC_TASK_CANCELLED;
                    }
                }

                return ASYNC_TASK_COMPLETED;
            } catch (SQLException e) {
                Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();

                return 1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == ASYNC_TASK_COMPLETED) {
                pa.notifyDataSetChanged();
               /* if (patterns.size() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.noPatternsFound,
                            Toast.LENGTH_SHORT).show();
                } */
            } else if (result > ASYNC_TASK_COMPLETED) {
                Toast.makeText(getApplicationContext(), R.string.database_error,
                        Toast.LENGTH_LONG).show();
            }

            // Make sure AsyncTask dies
            try {
                this.finalize();

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
