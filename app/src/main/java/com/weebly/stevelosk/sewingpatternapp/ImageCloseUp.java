package com.weebly.stevelosk.sewingpatternapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageCloseUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_close_up);


        try {

            ImageView imageView = (ImageView) findViewById(R.id.image);
            Bitmap bitmap = null;

            Intent intent = getIntent();
            bitmap = intent.getParcelableExtra("PASSED_IMAGE");

            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
