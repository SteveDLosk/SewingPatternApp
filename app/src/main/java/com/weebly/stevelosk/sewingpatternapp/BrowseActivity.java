package com.weebly.stevelosk.sewingpatternapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BrowseActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList patterns = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        getPatterns();

        listView = (ListView) findViewById(R.id.browseActivity_ListView);
        PatternAdapter pa = new PatternAdapter(this, patterns);
        listView.setAdapter(pa);



    }

    private void getPatterns() {

        PatternDBAdapter db = new PatternDBAdapter(getApplicationContext());
        try {

            db.open();

            Cursor cursor = db.getAllPatterns();
            while (cursor.moveToNext()) {
                Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
                patterns.add(p);
            }

        }

        catch(Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }
    }
}
