package com.weebly.stevelosk.sewingpatternapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BrowseActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList patterns = new ArrayList();
    private PatternAdapter pa = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        getPatterns();

        listView = (ListView) findViewById(R.id.browseActivity_ListView);
        pa = new PatternAdapter(this, patterns);
        listView.setAdapter(pa);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Pattern p = (Pattern) patterns.get(i);

                Intent examineIntent = new Intent(getApplicationContext(),
                        ExaminePatternActivity.class);
                examineIntent.putExtra("PASSED_PATTERN_INSTANCE", p);
                startActivity(examineIntent);
            }
        });


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

    protected void onResume() {
        super.onResume();
        patterns.clear();
        pa.notifyDataSetChanged();
        getPatterns();
    }
}
