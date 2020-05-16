package com.kos.AACassistant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Guide extends AppCompatActivity {

    private Button btnReturn;
    private TextView aiTitle;
    private ImageView gImgOne,gImgTwo,gImgThree;
    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private String activityFrom,categoryPassed,phrasePassed;
    private LinearLayout aiLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        //DATABASE VARIABLES
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();

        //LAYOUT CONTROLS
        btnReturn = findViewById(R.id.aiReturn);
        gImgOne = findViewById(R.id.gImgOne);
        gImgTwo = findViewById(R.id.gImgTwo);
        gImgThree = findViewById(R.id.gImgThree);
        aiTitle = findViewById(R.id.aiTitle);
        aiLayout = findViewById(R.id.aiLayout);

        //INTENTS
        activityFrom = getIntent().getStringExtra("From");
        categoryPassed = getIntent().getStringExtra("PC");
        phrasePassed = getIntent().getStringExtra("SpeechText");

        checkColorBlindStatus();
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainSettings();
            }
        });
    }

    public void checkColorBlindStatus()
    {
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            btnReturn.setBackgroundResource(R.drawable.colorblindbtn);
            aiTitle.setTextColor(Color.BLACK);
            aiLayout.setBackgroundResource(R.color.yellow);
        }
        else
        {
            btnReturn.setBackgroundResource(R.drawable.selectedbuttonshape);
            aiTitle.setTextColor(Color.WHITE);
            aiLayout.setBackgroundResource(R.color.gray);
        }
    }

    public void returnToMainSettings()
    {
        Intent intent = new Intent(this,MainSettings.class);
        intent.putExtra("PC",categoryPassed);
        intent.putExtra("SpeechText",phrasePassed);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
