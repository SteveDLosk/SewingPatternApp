package com.weebly.stevelosk.sewingpatternapp;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by steve on 2/4/2018.
 */

class AsyncSearchTask extends AsyncTask<Object, Void, Integer>  {

    // Result flags.  Canceled, completed, or any error conditions (> 0)
    private static final int ASYNC_TASK_CANCELLED = -1;
    private static final int ASYNC_TASK_COMPLETED = 0;

    // The passed in objects, in paramater order:
    private String searchStr = "";
    private String[] advancedSearchStrings = null;
    private ArrayList<Pattern> patterns = null;
    private PatternDBAdapter db = null;
    private PatternAdapter pa = null;

    @Override
    protected Integer doInBackground(Object... objects) {

        boolean simple;

        // get query content and result set target from packaged objects
        if (objects[0] instanceof String) {
            simple = true;
            searchStr = (String) objects[0];
        }
        else {
            simple = false;
            advancedSearchStrings = (String[])  objects[0];
        }

        patterns = (ArrayList<Pattern>) objects[1];
        db = (PatternDBAdapter) objects[2];
        pa = (PatternAdapter) objects[3];

        try {

            // clear the old result list
            patterns.clear();
            Cursor cursor;

            if (simple) {
                // query
                String[] predicate = new String[2];
                // surround search string with "%" so partial matches can be found
                predicate[0] = "%" + searchStr + "%";
                predicate[1] = "%" + searchStr + "%";
                cursor = db.getPatternBy_ID_OR_Content(predicate);
            }
            else {
                cursor = db.getComplexSearchResultSet(advancedSearchStrings);
            }
            if (this.isCancelled()) {
                return ASYNC_TASK_CANCELLED;
            }

            // populate the results from the query result set
            while (cursor.moveToNext()) {
                Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
                int min = p.getMinNumericSize();
                int max = p.getMaxNumericSize();
                Log.w("tag", String.valueOf(min));
                Log.w("tag", String.valueOf(max));
                Log.w("tag", p.toString());
                patterns.add(p);
                if (this.isCancelled()) {
                    return ASYNC_TASK_CANCELLED;
                }
            }

            return ASYNC_TASK_COMPLETED;
        } catch (SQLException e ) {
            throw e;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {

        if (result == ASYNC_TASK_COMPLETED) {
            pa.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


}