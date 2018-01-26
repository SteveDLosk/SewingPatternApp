package com.weebly.stevelosk.sewingpatternapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class AddPatternActivity extends AppCompatActivity {

    static final int REQUEST_FRONT_IMAGE_CAPTURE = 1;
    static final int REQUEST_BACK_IMAGE_CAPTURE = 2;

    private ImageView frontPic;

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
        if (requestCode == REQUEST_FRONT_IMAGE_CAPTURE) {
            target = (ImageView) findViewById(R.id.frontImage);
        }
        else if (requestCode == REQUEST_BACK_IMAGE_CAPTURE) {
            target = (ImageView) findViewById(R.id.backImage);
        }
        // get the picture taken
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // This could throw a null object exception, but really only if an incorrect
            // request code is set.
            assert (requestCode == 1 || requestCode == 2);
            target.setImageBitmap(imageBitmap);
        }
    }
}
