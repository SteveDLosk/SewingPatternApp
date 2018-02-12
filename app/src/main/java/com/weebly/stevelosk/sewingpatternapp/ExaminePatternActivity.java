package com.weebly.stevelosk.sewingpatternapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NULL;

public class ExaminePatternActivity extends AppCompatActivity {

    private Pattern thisPattern;
    private final String TAG = "ExaminePatternActivity";
    private PatternDBAdapter db = null;
    private boolean editMode = false;

    private EditText patternNumberET, brandET, sizesET, contentsET, notesET;
    private ArrayList<EditText> editTexts = new ArrayList<>();
    private Button editButton, saveButton, cancelButton;
    private ImageView frontImg;
    private ImageView backImg;
    private GridLayout gridLayout;

    private final int FRONT_IMAGE_NUMBER = 1;
    private final int BACK_IMAGE_NUMBER = 2;
    private Bitmap frontImageBitmap;
    private Bitmap backImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_pattern);

        frontImg = (ImageView) findViewById(R.id.frontImage);
        backImg = (ImageView) findViewById(R.id.backImage);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);

        patternNumberET = (EditText) findViewById(R.id.patternNumberEditText);
        brandET = (EditText) findViewById(R.id.brandEditText);
        sizesET = (EditText) findViewById(R.id.sizesEditText);
        contentsET = (EditText) findViewById(R.id.contentsEditText);
        notesET = (EditText) findViewById(R.id.notesEditText);

        editTexts.add(patternNumberET); editTexts.add(brandET); editTexts.add(sizesET);
        editTexts.add(contentsET); editTexts.add(notesET);

        editButton = (Button) findViewById(R.id.examineEditButton);
        saveButton = (Button) findViewById(R.id.examineSaveButton);
        cancelButton = (Button) findViewById(R.id.examineCancelButton);

        disableEdit();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowEdit();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePattern();
                disableEdit();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEdit();
                getPassedPatternInstanceImages();
            }
        });

        frontImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode) {
                    updatePicture((ImageView) view);
                }
                else {
                    displayPicture (frontImageBitmap);
                }
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode) {
                    updatePicture((ImageView) view);
                }
                else {
                    displayPicture (backImageBitmap);
                }
            }
        });

        // Get the saved Pattern object from the Intent
        try {
            Object o = this.getIntent().getExtras().getSerializable("PASSED_PATTERN_INSTANCE");
            if (o != null && o instanceof Pattern) {
                thisPattern = (Pattern) o;
            }

            // Fill text data from Pattern instance
            brandET.setText(thisPattern.getBrand());
            patternNumberET.setText(thisPattern.getPatternNumber());
            sizesET.setText(thisPattern.getSizes());
            contentsET.setText(thisPattern.getContent());
            notesET.setText(thisPattern.getNotes());

            // get images
            getPassedPatternInstanceImages();
        }
        catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void setFrontImageFromByteArray(Pattern p, ImageView imageView) {
        if (p.getFrontImgBytes() != null) {
            Bitmap image = BitmapFactory.decodeByteArray(p.getFrontImgBytes(), 0,
                    p.getFrontImgBytes().length);
            imageView.setImageBitmap(image);
        }
    }
    protected void setBackImageFromByteArray(Pattern p, ImageView imageView) {
        if (p.getBackImgBytes() != null) {
            Bitmap image = BitmapFactory.decodeByteArray(p.getBackImgBytes(), 0,
                    p.getBackImgBytes().length);
            imageView.setImageBitmap(image);
        }
    }

    private void allowEdit() {
        /*
        Switches this activity to "edit mode", allowing fields to be editable, and showing the
         save and cancel buttons
         */

        editMode = true;

        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        // Allow text editing of pattern fields
        for (EditText et : editTexts) {
            et.setInputType(TYPE_CLASS_TEXT);
        }

        // do something to provide a visual que
        gridLayout.setBackgroundResource(R.drawable.edit_mode_border);

    }

    private void disableEdit() {
        /*
        Closes "edit mode", changing button visibility, and disabling text edting
         */

        editMode = false;

        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);

        // prevent accidental changes
        for (EditText et : editTexts) {
                et.setInputType(TYPE_NULL);
        }

        // remove visual hint about edit mode
        gridLayout.setBackgroundResource(R.drawable.view_mode_no_border);
    }

    private void updatePattern() {

        try {
            db = new PatternDBAdapter(getApplicationContext());

            db.open();

            Pattern p = new Pattern();
            p.setPatternNumber(patternNumberET.getText().toString());
            p.setBrand(brandET.getText().toString());
            p.setSizes(sizesET.getText().toString());
            p.setContent(contentsET.getText().toString());
            p.setNotes(notesET.getText().toString());
            p.parseNumericSizes(sizesET.getText().toString());

            ContentValues cv = new ContentValues();
            cv.put(PatternDBAdapter.PATTERN_NUMBER, p.getPatternNumber());
            cv.put(PatternDBAdapter.BRAND, p.getBrand());
            cv.put(PatternDBAdapter.SIZES, p.getSizes());
            cv.put(PatternDBAdapter.MIN_SIZE, p.getMinNumericSize());
            cv.put(PatternDBAdapter.MAX_SIZE, p.getMaxNumericSize());
            cv.put(PatternDBAdapter.CONTENT, p.getContent());
            cv.put(PatternDBAdapter.NOTES, p.getNotes());

            // BLOBS
            if (frontImageBitmap != null) {
                cv.put(PatternDBAdapter.FRONT_IMAGE,
                        PatternDBAdapter.bitmapToByteArray2(frontImageBitmap));
            }
            if (backImageBitmap != null) {
                cv.put(PatternDBAdapter.BACK_IMAGE,
                        PatternDBAdapter.bitmapToByteArray2(backImageBitmap));
            }

            db.updatePattern(thisPattern.getPatternId(), cv);

        } catch (SQLException e) {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
    }

    private void updatePicture (ImageView iv) {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // logical flag for which image needs updated
        int requestCode = 0;
        if (iv == frontImg) {
            requestCode = FRONT_IMAGE_NUMBER;
        }
        else if (iv == backImg) {
            requestCode = BACK_IMAGE_NUMBER;
        }
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, requestCode);
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
            if (requestCode == 1) {
                target = (ImageView) findViewById(R.id.frontImage);
                // reference for saving to the database
                 frontImageBitmap = image;
            }
            else if (requestCode == 2) {
                target = (ImageView) findViewById(R.id.backImage);
                backImageBitmap = image;
            }
            // update UI
            // This could throw a null object exception, but really only if an incorrect
            // request code is set.
            target.setImageBitmap(image);

        }
    }

    private void getPassedPatternInstanceImages () {
        // get images
        try {
            setFrontImageFromByteArray(thisPattern, frontImg);
            if (thisPattern.getFrontImgBytes() != null) {
                frontImageBitmap = BitmapFactory.decodeByteArray(thisPattern.getFrontImgBytes(), 0,
                        thisPattern.getFrontImgBytes().length);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            setBackImageFromByteArray(thisPattern, backImg);
            if (thisPattern.getBackImgBytes() != null) {
                backImageBitmap = BitmapFactory.decodeByteArray(thisPattern.getBackImgBytes(), 0,
                        thisPattern.getBackImgBytes().length);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void displayPicture (Bitmap bitmap) {
        Intent showImageIntent = new Intent(getApplicationContext(), ImageCloseUp.class);
        showImageIntent.putExtra("PASSED_IMAGE", bitmap);

        startActivity(showImageIntent);

    }
}
