package com.weebly.stevelosk.sewingpatternapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NULL;

public class ExaminePatternActivity extends AppCompatActivity {

    private Pattern thisPattern;
    private final String TAG = "ExaminePatternActivity";

    private TextView brandTV, patternNumberTV, sizesTV, contentsTV, notesTV;
    private EditText patternNumberET, brandET, sizesET, contentsET, notesET;
    private ArrayList<EditText> editTexts = new ArrayList<>();
    private Button editButton, saveButton, cancelButton;
    private ImageView frontImg;
    private ImageView backImg;

    private final int FRONT_IMAGE_NUMBER = 0;
    private final int BACK_IMAGE_NUMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_pattern);


        /*
        brandTV = (TextView) findViewById(R.id.brandTextView);
        patternNumberTV = (TextView) findViewById(R.id.patternNumberTextView);
        sizesTV = (TextView) findViewById(R.id.sizesTextView);
        contentsTV = (TextView) findViewById(R.id.contentsTextView);
        notesTV = (TextView) findViewById(R.id.notesTextView);
        */

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
                disableEdit();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEdit();
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
            contentsET.setText(thisPattern.getSizes());
            notesET.setText(thisPattern.getNotes());

            frontImg = (ImageView) findViewById(R.id.frontImage);
            backImg = (ImageView) findViewById(R.id.backImage);

            // get images
            try {
                setFrontImageFromByteArray(thisPattern, frontImg);

            } catch (NullPointerException e) {

            }
            try {
                setBackImageFromByteArray(thisPattern, backImg);
            } catch (NullPointerException e) {

            }
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
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        // Allow text editing of pattern fields
        for (EditText et : editTexts) {
            et.setInputType(TYPE_CLASS_TEXT);
        }

        // do something to provide a visual que

    }

    private void disableEdit() {
        /*
        Closes "edit mode", changing button visibility, and disabling text edting
         */
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);

        // prevent accidental changes
        for (EditText et : editTexts) {
                et.setInputType(TYPE_NULL);
        }
    }
}
