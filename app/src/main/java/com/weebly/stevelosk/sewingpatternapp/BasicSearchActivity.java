package com.weebly.stevelosk.sewingpatternapp;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BasicSearchActivity extends AppCompatActivity {

    private String searchString = "";
    private EditText searchEditText;
    private Button searchButon;
    private TextView resultsTextView;
    private ArrayList<Pattern> patterns = new ArrayList<>();
    private ImageView placeHolderImage;
    private ListView resultsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_search);

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        resultsTextView = (TextView) findViewById(R.id.searchActivity_resultsTextView);
        placeHolderImage = (ImageView) findViewById(R.id.placeHolderImage);

        // ListView
        resultsListView = (ListView) findViewById(R.id.basicSearch_ListView);
        PatternAdapter pa = new PatternAdapter(this, patterns);
        resultsListView.setAdapter(pa);

        searchButon = (Button) findViewById(R.id.searchActivitySearchButton);
        searchButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    // remove prior search results
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

        });
    }
}