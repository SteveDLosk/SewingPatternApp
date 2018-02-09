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
                        try {
                            searchTask = new AsyncSearchTask();
                            Object[] params = {searchEditText.getText().toString(),
                                    patterns, db, pa};
                            searchTask.execute(params);
                        }
                        catch (SQLException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, TIMER_DELAY);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
