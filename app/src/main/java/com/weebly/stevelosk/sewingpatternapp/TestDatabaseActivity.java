package com.weebly.stevelosk.sewingpatternapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestDatabaseActivity extends AppCompatActivity {

    private TextView resultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);
        resultsTextView = (TextView) findViewById(R.id.resultsTextView);

        try {
            testPatternAdd();
        }
        catch (SQLException e) {
            resultsTextView.setText(e.getMessage());
        }
    }

    private void testPatternAdd () throws SQLException {
        PatternDBAdapter dbAdapter = new PatternDBAdapter(this);
        dbAdapter.open();
        List<Pattern> patterns = new ArrayList<>();
        Cursor cursor = dbAdapter.getAllPatterns();

        while (cursor.moveToNext()) {
            Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
            patterns.add(p);
        }

        StringBuilder sb = new StringBuilder();
        for (Pattern p : patterns) {
            sb.append(p.getPatternId());
            sb.append(" ");
            sb.append(p.getBrand());
            sb.append("\n");
        }
        resultsTextView.setText(sb.toString());
        dbAdapter.close();
    }

}
