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

public class AppInfo extends AppCompatActivity {

    private Button btnCIReturnToSettings;
    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private LinearLayout ciLayout;
    private TextView txtciTitle,txtDev,txtDevInfo,txtSup,txtSupInfo,txtNHS,txtNHSInfo;
    private ImageView imgvVR;
    private String speechText,activityFrom,categorySelected;
    private static final int RECOGNIZER_RESULT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        //DATABASE VARIABLES
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();

        //INTENTS
        activityFrom = getIntent().getStringExtra("From");
        categorySelected = getIntent().getStringExtra("PC");
        speechText = getIntent().getStringExtra("SpeechText");

        //LAYOUT
        btnCIReturnToSettings = findViewById(R.id.btnCIReturnToSettings);
        ciLayout = findViewById(R.id.ciLayout);
        txtciTitle = findViewById(R.id.txtciTitle);
        txtDev = findViewById(R.id.txtDev);
        txtDevInfo = findViewById(R.id.txtDevInfo);
        txtSup = findViewById(R.id.txtSup);
        txtSupInfo = findViewById(R.id.txtSupInfo);
        txtNHS = findViewById(R.id.txtNHS);
        txtNHSInfo = findViewById(R.id.txtNHSInfo);

        btnCIReturnToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainSettings();
            }
        });
        checkColorBlindStatus();
    }

    public void checkColorBlindStatus()
    {
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            btnCIReturnToSettings.setBackgroundResource(R.drawable.colorblindbtn);
            txtDevInfo.setBackgroundResource(R.drawable.colorblindbtn);
            txtSupInfo.setBackgroundResource(R.drawable.colorblindbtn);
            txtNHSInfo.setBackgroundResource(R.drawable.colorblindbtn);
            txtDev.setTextColor(Color.BLACK);;
            txtSup.setTextColor(Color.BLACK);;
            txtNHS.setTextColor(Color.BLACK);;
            txtciTitle.setTextColor(Color.BLACK);
            ciLayout.setBackgroundResource(R.color.yellow);
        }
        else
        {
            btnCIReturnToSettings.setBackgroundResource(R.drawable.selectedbuttonshape);
            txtDevInfo.setBackgroundResource(R.drawable.buttonshape);
            txtSupInfo.setBackgroundResource(R.drawable.buttonshape);
            txtNHSInfo.setBackgroundResource(R.drawable.buttonshape);
            txtDev.setTextColor(Color.WHITE);;
            txtSup.setTextColor(Color.WHITE);;
            txtNHS.setTextColor(Color.WHITE);;
            txtciTitle.setTextColor(Color.WHITE);
            ciLayout.setBackgroundResource(R.color.gray);
        }
    }

    private void returnToMainSettings()
    {
        Intent intent = new Intent(this,MainSettings.class);
        intent.putExtra("From",activityFrom);
        intent.putExtra("PC",categorySelected);
        intent.putExtra("SpeechText",speechText);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
