package com.weebly.stevelosk.sewingpatternapp;

import android.content.Intent;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageButton topRightButton;
    private ImageButton topLeftButton;
    private ImageButton bottomLeftButton;
    private ImageButton bottomRightButton;

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

        topLeftButton = (ImageButton) findViewById(R.id.imgButtonTopLeft);
        topLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(getApplicationContext(), BasicSearchActivity.class);
                startActivity(searchIntent);
            }
        });

        bottomLeftButton = (ImageButton) findViewById(R.id.imgButtonBottomLeft);
        bottomLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent testDBIntent = new Intent(getApplicationContext(), BrowseActivity.class);
                startActivity(testDBIntent);
            }
        });

        bottomRightButton = (ImageButton) findViewById(R.id.imgButtonBottomRight);
        bottomRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent advancedSearchIntent = new Intent(getApplicationContext(),
                        AdvancedSearchActivity.class);
                startActivity(advancedSearchIntent);
            }
        });
    }
}
