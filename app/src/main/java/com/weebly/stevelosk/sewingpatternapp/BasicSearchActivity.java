package com.weebly.stevelosk.sewingpatternapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BasicSearchActivity extends AppCompatActivity {

    private String searchString = "";
    private EditText searchEditText;
    private Button searchButon;
    private TextView resultsTextView;
    private List<Pattern> patterns = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_search);

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        resultsTextView = (TextView) findViewById(R.id.searchActivity_resultsTextView);
        searchButon = (Button) findViewById(R.id.searchActivitySearchButton);
        searchButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    PatternDBAdapter db = new PatternDBAdapter(getApplicationContext());
                    db.open();

                    String[] predicate = new String[1];
                    predicate[0] = searchEditText.getText().toString();
                    Cursor cursor = db.getPatternByID(predicate);

                    while (cursor.moveToNext()) {
                        Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
                        patterns.add(p);
                    }
                    StringBuilder sb = new StringBuilder();
                    for (Pattern p : patterns) {
                        sb.append(p.getPatternId());
                        sb.append(" ");
                        sb.append(p.getPatternNumber());
                        sb.append(" ");
                        sb.append(p.getBrand());
                        sb.append(" ");
                        sb.append(p.getContent());
                        sb.append("\n");
                    }
                    resultsTextView.setText(sb.toString());
                    db.close();
                }

            catch(Exception e) {
                resultsTextView.setText(e.getMessage());
            }
        }

        });
    }
}