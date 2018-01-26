package com.weebly.stevelosk.sewingpatternapp;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageButton topRightButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topRightButton = (ImageButton) findViewById(R.id.imgButtonTopRight);
        topRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getApplicationContext(), AddPatternActivity.class);
                startActivity(addIntent);
            }
        });
    }
}
