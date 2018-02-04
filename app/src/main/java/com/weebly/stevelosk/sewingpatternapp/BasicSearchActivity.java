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
    private int TIMER_DELAY = 50;

    private PatternAdapter pa = null;

    private final String TAG = "SDL BasicSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_search);

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

        /*
        // TODO: use Handler instead?
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if(textChangedTimer != null)
                    textChangedTimer.cancel();
            }
            @Override
            public void afterTextChanged(final Editable s) {
                //avoid triggering event when text is too short

                    textChangedTimer = new Timer();
                    textChangedTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            search();
                        }

                    }, TIMER_DELAY);
                }

        });
    }

    private void search() {
        try {
            // remove prior search results
            Object o0 = getApplicationContext();
            Object o1 = searchEditText.getText().toString();
            Object o2 = patterns;
            patterns.clear();
            PatternDBAdapter db = new PatternDBAdapter(getApplicationContext());
            db.open();

            String[] predicate = new String[1];
            predicate[0] = searchEditText.getText().toString();
            Cursor cursor = db.getPatternByID(predicate);

            while (cursor.moveToNext()) {
                Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
                patterns.add(p);
            }

            db.close();
        }

        catch(Exception e) {
            resultsTextView.setText(e.getMessage());
        }
    }
    */
    }

     class AsyncSearchTask extends AsyncTask<Object, Void, Integer> {
        @Override
        protected Integer doInBackground(Object... objects) {

            // create database connection
            Context ctx = (Context) objects[0];
            PatternDBAdapter db = new PatternDBAdapter(ctx);

            // get query content and result set target from packaged objects
            String searchStr = (String) objects[1];
            ArrayList<Pattern> patterns = (ArrayList<Pattern>) objects[2];

            try {

                // clear the old result list
                patterns.clear();

                // open the database
                db.open();

                // query
                String[] predicate = new String[1];
                predicate[0] = searchStr;
                Cursor cursor = db.getPatternByID(predicate);

                // populate the results from the query result set
                while (cursor.moveToNext()) {
                    Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
                    patterns.add(p);
                }

                return 0;
            } catch (SQLException e) {
                Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();

                return 1;
            } finally {
                db.close();
            }

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 0) {
                pa.notifyDataSetChanged();
                if (patterns.size() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.noPatternsFound,
                            Toast.LENGTH_SHORT).show();
                }
            }
            else {
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
    }
}