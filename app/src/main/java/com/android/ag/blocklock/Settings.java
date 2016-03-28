package com.android.ag.blocklock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by User on 10.03.2016.
 */
public class Settings extends Activity {

    public Button keyOneBtn, keyTwoBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        keyOneBtn = (Button) findViewById(R.id.key_one);
        keyTwoBtn = (Button) findViewById(R.id.key_two);
        backBtn = (Button) findViewById(R.id.backBtn);

        keyOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, KeyOne.class);
                startActivity(intent);
            }
        });

        keyTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, KeyTwo.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
