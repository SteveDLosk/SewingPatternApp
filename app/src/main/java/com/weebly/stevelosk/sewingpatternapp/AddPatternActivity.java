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


    }



    private void dispatchTakePictureIntent(int id) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == REQUEST_FRONT_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                System.out.println("Got the pciture");
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                frontPic = (ImageView) findViewById(R.id.frontImage);
                frontPic.setImageBitmap(imageBitmap);
            }
    }
}
