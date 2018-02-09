package com.weebly.stevelosk.sewingpatternapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;


public class AddPatternActivity extends AppCompatActivity {

    static final int REQUEST_FRONT_IMAGE_CAPTURE = 1;
    static final int REQUEST_BACK_IMAGE_CAPTURE = 2;
    static boolean hasFrontImage = false;
    static boolean hasBackImage = false;

    private ImageView frontPic;
    private ImageView backPic;
    private EditText patternNumberET;
    private EditText brandET;
    private EditText sizesET;
    private EditText contentsET;
    private EditText notesET;

    private android.graphics.Bitmap frontImgBitmap = null;
    private android.graphics.Bitmap backImgBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pattern);

        ImageView frontPic = (ImageView) findViewById(R.id.frontImage);
        frontPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(REQUEST_FRONT_IMAGE_CAPTURE);

            }
        });

        ImageView backPic = (ImageView) findViewById(R.id.backImage);
        backPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(REQUEST_BACK_IMAGE_CAPTURE);
            }
        });


        // register text areas for data collection
        patternNumberET = (EditText) findViewById(R.id.patternNumberEditText);
        brandET = (EditText) findViewById(R.id.brandEditText);
        sizesET = (EditText) findViewById(R.id.sizesEditText);
        contentsET = (EditText) findViewById(R.id.contentsEditText);
        notesET = (EditText) findViewById(R.id.notesEditText);

        Button saveButton = (Button) findViewById(R.id.confirmAddButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addPatternToDatabase();
                    onSuccessfulAdd();
                }
                catch (SQLException e) {
                    toastException(e);
                }
            }
        });

    }

    private void addPatternToDatabase () throws SQLException {

            // Open Database connection
            PatternDBAdapter dbAdapter = new PatternDBAdapter(this);
            dbAdapter.open();

            // Collect user input from fields
            String brand = brandET.getText().toString();
            String patternNumber = patternNumberET.getText().toString();
            String sizes = sizesET.getText().toString();
            String contents = contentsET.getText().toString();
            String notes = notesET.getText().toString();

            // Put into Content Values
            ContentValues values = new ContentValues();
            values.put(PatternDBAdapter.BRAND, brand);
            values.put(PatternDBAdapter.PATTERN_NUMBER, patternNumber);
            values.put(PatternDBAdapter.SIZES, sizes);
            values.put(PatternDBAdapter.CONTENT, contents);
            values.put(PatternDBAdapter.NOTES, notes);

            // Get max and min size
            Pattern p = new Pattern();
            Log.w("tag", sizes);
            boolean sizable = p.parseNumericSizes(sizes);
            int min, max;
            if (sizable) {
                values.put(PatternDBAdapter.MAX_SIZE, p.getMaxNumericSize());
                values.put(PatternDBAdapter.MIN_SIZE, p.getMinNumericSize());
            }

            // BLOBS
            if (frontImgBitmap != null) {
                values.put(PatternDBAdapter.FRONT_IMAGE,
                        PatternDBAdapter.bitmapToByteArray2(frontImgBitmap));
            }
            if (backImgBitmap != null) {
                values.put(PatternDBAdapter.BACK_IMAGE,
                        PatternDBAdapter.bitmapToByteArray2(backImgBitmap));
            }

            // run the insert DML
            dbAdapter.insertPattern(values);
            dbAdapter.close();
    }

    private void dispatchTakePictureIntent(int id) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, id);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // set the camera picture target
        ImageView target = null;
        // get the image as a Bitmap
        Bundle extras = data.getExtras();

        // select the right ImageView to update and log there is an image taken
        if (resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) extras.get("data");
            if (requestCode == REQUEST_FRONT_IMAGE_CAPTURE) {
                target = (ImageView) findViewById(R.id.frontImage);
                hasFrontImage = true;
                // reference for saving to the database
                frontImgBitmap = image;
            }
            else if (requestCode == REQUEST_BACK_IMAGE_CAPTURE) {
                target = (ImageView) findViewById(R.id.backImage);
                hasBackImage = true;
                backImgBitmap = image;
            }
            // update UI
            // This could throw a null object exception, but really only if an incorrect
            // request code is set.
            assert (requestCode == 1 || requestCode == 2);
            target.setImageBitmap(image);

        }
    }

    private void toastException (Exception e) {
        Toast.makeText(getApplicationContext(), e.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccessfulAdd () {
        /*
        Provides confirmation to user the data is saved, and clears text fields
        and image holders for a new pattern.
         */
        Toast.makeText(getApplicationContext(), R.string.AddPatternSuccess,
                Toast.LENGTH_SHORT).show();

        // clear all text fields
        patternNumberET.setText("");
        brandET.setText("");
        sizesET.setText("");
        contentsET.setText("");
        notesET.setText("");

        // reset camera images
        frontPic = (ImageView) findViewById(R.id.frontImage);
        backPic = (ImageView) findViewById(R.id.backImage);
        frontPic.setImageResource(R.drawable.photo_icon);
        backPic.setImageResource(R.drawable.photo_icon);
    }
}
