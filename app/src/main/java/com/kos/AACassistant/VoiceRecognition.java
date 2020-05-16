package com.kos.AACassistant;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognition extends AppCompatActivity {


    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private LinearLayout vrLayout;
    private TextView txtVRTitle,txtVRInfo;
    private ImageView imgvVR;
    private Button btnVRReturnToSettings;
    private String speechText,activityFrom,categorySelected;
    private static final int RECOGNIZER_RESULT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        //DATABASE VARIABLES
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();

        //INTENTS
        activityFrom = getIntent().getStringExtra("From");
        categorySelected = getIntent().getStringExtra("PC");
        speechText = getIntent().getStringExtra("SpeechText");

        //LAYOUT OBJECTS
        vrLayout = findViewById(R.id.vrLayout);
        txtVRInfo = findViewById(R.id.txtVRInfo);
        txtVRTitle = findViewById(R.id.txtVRTitle);
        imgvVR = findViewById(R.id.imgvVR);
        btnVRReturnToSettings = findViewById(R.id.btnVRReturnToSettings);

        //FUNCTIONS
        btnVRReturnToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainSettings();
            }
        });
        imgvVR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Voice Recognition is listening...");
                try {
                    startActivityForResult(speechIntent, RECOGNIZER_RESULT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Voice Recognition not supported.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkColorBlindStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && null != data)
        {
            ArrayList<String> words = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            checkForCategory(words.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkForCategory(@NotNull String speachText)
    {
        String[] listOfWords = speachText.split(" ");
        CategoriesDBH categoriesDBH = new CategoriesDBH(this);
        Cursor categoriesData = categoriesDBH.getAllData();
        for(int index = 0;index < listOfWords.length;index++)
        {
            for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
            {
                if((listOfWords[index].toLowerCase().contains(categoriesData.getString(1).toLowerCase()))
                || (categoriesData.getString(1).toLowerCase().contains(listOfWords[index].toLowerCase())))
                {
                    Intent intent = new Intent(this,StartActivity.class);
                    intent.putExtra("SpeechText",speechText);
                    intent.putExtra("PC",categoriesData.getString(1));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        }
    }

    public void checkColorBlindStatus()
    {
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            btnVRReturnToSettings.setBackgroundResource(R.drawable.colorblindbtn);
            txtVRInfo.setBackgroundResource(R.drawable.colorblindbtn);
            txtVRTitle.setTextColor(Color.BLACK);
            imgvVR.setBackgroundResource(R.drawable.colorblindbtn);
            vrLayout.setBackgroundResource(R.color.yellow);
        }
        else
        {
            btnVRReturnToSettings.setBackgroundResource(R.drawable.selectedbuttonshape);
            txtVRInfo.setBackgroundResource(R.color.light_grey);
            imgvVR.setBackgroundResource(R.drawable.selectedbuttonshape);
            txtVRTitle.setTextColor(Color.WHITE);
            vrLayout.setBackgroundResource(R.color.gray);
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
