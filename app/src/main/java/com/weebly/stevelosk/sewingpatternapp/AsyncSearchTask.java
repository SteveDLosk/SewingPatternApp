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

    // search codes to select appropriate SQL query
    protected static final int EXACT_PATTERN_NUMBER_MATCH = 1;
    protected static final int CLOSE_PATTERN_NUMBER_MATCH = 2;
    protected static final int CLOSE_CONTENT_MATCH = 3;
    protected static final int CLOSE_PATTERN_NUMBER_OR_CONTENT_MATCH = 4;
    protected static final int CLOSE_ANY_TEXT_FIELD_MATCH = 5;
    protected static final int COMPLEX_SEARCH = 6;

    // The passed in objects, in parameter order:
    private String searchStr = "";
    private String[] advancedSearchStrings = null;
    private ArrayList<Pattern> patterns = null;
    private PatternDBAdapter db = null;
    private PatternAdapter pa = null;
    private int searchMode;
    private IAsyncCalling calling = null;

    @Override
    protected Integer doInBackground(Object... objects) {

        /* First, determine if the query is a simple or advanced search.  If it is a simple
           search, the first object will be a String, representing the actual query.  Otherwise,
           the first object will be an array of Strings for an advanced search
         */
        boolean simple;
        if (objects[0] instanceof String) {
            simple = true;
            searchStr = (String) objects[0];
        }
        else {
            simple = false;
            advancedSearchStrings = (String[])  objects[0];
        }

        // get query content and result set target from packaged objects
        patterns = (ArrayList<Pattern>) objects[1];
        db = (PatternDBAdapter) objects[2];
        pa = (PatternAdapter) objects[3];
        searchMode = (int) objects[4];
        if (objects.length >= 6) {
            calling = (IAsyncCalling) objects[5];
        }

        try {

            // clear the old result list
            patterns.clear();
            Cursor cursor = null;

            if (simple) {
                /* Build a predicate based on the original string.  Make four copies, because
                   getPatternByAnyTextField looks in four places.  Each of these copies of the
                   original String becomes a where clause argument to the database SQL call.
                   Any extra copies of the String are unused, but harmless.
                */
                String[] predicate = new String[4];
                // surround search string with "%" so partial matches can be found
                predicate[0] = "%" + searchStr + "%";
                predicate[1] = "%" + searchStr + "%";
                predicate[2] = "%" + searchStr + "%";
                predicate[3] = "%" + searchStr + "%";

                switch (searchMode) {
                    case EXACT_PATTERN_NUMBER_MATCH: cursor = db.getPatternByID(predicate);
                    break;

                    case CLOSE_PATTERN_NUMBER_MATCH: cursor = db.getPatternByLikeID(predicate);
                    break;

                    case CLOSE_CONTENT_MATCH: cursor = db.getPatternByLikeContent(predicate);
                    break;

                    case CLOSE_PATTERN_NUMBER_OR_CONTENT_MATCH: cursor =
                            db.getPatternBy_ID_OR_Content(predicate);
                            break;

                    case CLOSE_ANY_TEXT_FIELD_MATCH: cursor = db.getPatternByAnyTextField(predicate);
                    break;
                }

            }
            else {
                cursor = db.getComplexSearchResultSet(advancedSearchStrings);
            }
            if (this.isCancelled()) {
                return ASYNC_TASK_CANCELLED;
            }

            // populate the results from the query result set
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Pattern p = PatternDBAdapter.getPatternFromCursor(cursor);
                    int min = p.getMinNumericSize();
                    int max = p.getMaxNumericSize();
                    patterns.add(p);
                    if (this.isCancelled()) {
                        return ASYNC_TASK_CANCELLED;
                    }
                }
            }

            return ASYNC_TASK_COMPLETED;
        } catch (SQLException e ) {
            throw e;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {

        // callback to the UI thread that the views can be updated with new data
        if (result == ASYNC_TASK_COMPLETED) {
            pa.notifyDataSetChanged();

            // callback to UI showing query is complete, there just is no data to show
            if (pa.isEmpty()) {
                if (calling != null)
                    calling.reportNoResults();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


}
