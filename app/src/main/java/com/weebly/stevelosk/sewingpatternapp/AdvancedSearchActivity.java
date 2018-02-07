package com.weebly.stevelosk.sewingpatternapp;

import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdvancedSearchActivity extends AppCompatActivity {

    private EditText patternNumberET, brandET, sizesET, contentsET, notesET;
    private ListView resultsListView;
    private Button searchButton;

    private ArrayList<Pattern> results = null;
    private PatternAdapter pa = null;
    private PatternDBAdapter db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        db = new PatternDBAdapter(this);
        db.open();

        patternNumberET = (EditText) findViewById(R.id.patternNumberEditText);
        brandET = (EditText) findViewById(R.id.brandEditText);
        sizesET = (EditText) findViewById(R.id.sizesEditText);
        contentsET = (EditText) findViewById(R.id.contentsEditText);
        notesET = (EditText) findViewById(R.id.notesEditText);

        resultsListView = (ListView) findViewById(R.id.advancedSearch_ListView);
        results = new ArrayList<>();
        pa = new PatternAdapter(this, results);
        resultsListView.setAdapter(pa);

        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Collect string data from EditText fields
                String[] searchFieldData = {patternNumberET.getText().toString(),
                        brandET.getText().toString(), sizesET.getText().toString(),
                        contentsET.getText().toString(), notesET.getText().toString()};

                try {
                    AsyncSearchTask task = new AsyncSearchTask();
                    Object[] params = {searchFieldData, results, db, pa};
                    task.execute(params);
                }
                catch (SQLException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}
