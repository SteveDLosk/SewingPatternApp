package com.weebly.stevelosk.sewingpatternapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExaminePatternActivity extends AppCompatActivity {

    private Pattern thisPattern;

    private TextView brandTextView, patternNumberTextView, sizesTextView, contentsTextView;
    private EditText notesEditText;
    private ImageView frontImg;
    private ImageView backImg;

    private final int FRONT_IMAGE_NUMBER = 0;
    private final int BACK_IMAGE_NUMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_pattern);

        brandTextView = (TextView) findViewById(R.id.brandTextView);
        patternNumberTextView = (TextView) findViewById(R.id.patternNumberTextView);
        sizesTextView = (TextView) findViewById(R.id.sizesTextView);
        contentsTextView = (TextView) findViewById(R.id.contentsTextView);
        notesEditText = (EditText) findViewById(R.id.notesEditText);

        // Get the saved Pattern object from the Intent
        try {
            Object o = this.getIntent().getExtras().getSerializable("PASSED_PATTERN_INSTANCE");
            if (o != null && o instanceof Pattern) {
                thisPattern = (Pattern) o;
            }

            // Fill text data from Pattern instance
            brandTextView.setText(thisPattern.getBrand());
            patternNumberTextView.setText(thisPattern.getPatternNumber());
            sizesTextView.setText(thisPattern.getSizes());
            contentsTextView.setText(thisPattern.getSizes());
            notesEditText.setText(thisPattern.getNotes());

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
}
