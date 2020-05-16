package com.kos.AACassistant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

public class FullScreenView extends AppCompatActivity {

    //LAYOUT OBJECTS
    private Button btnSpeakFSM;
    private Button btnClearFSM;
    private Button btnMinimiseScreen;
    private Button btnSettingsFSM;
    private Button btnMainSettingsFSM;
    private Button btnAddFSM;
    private EditText speechBarFSM;
    private TextToSpeech dTTS;
    private LinearLayout fsvLayout;

    //VARIABLES
    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private String passedText;
    private String passedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //CREATION FUNCTION
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_view);

        //SETTING UP ALL VARIABLES AND BUTTONS
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();
        fsvLayout = findViewById(R.id.fsvLayout);
        btnSpeakFSM = findViewById(R.id.btnSpeakFSM);
        btnClearFSM = findViewById(R.id.btnClearFSM);
        btnMainSettingsFSM = findViewById(R.id.btnMainSettingsFSM);
        btnAddFSM = findViewById(R.id.btnAddFSM);
        btnMinimiseScreen = findViewById(R.id.btnMinimiseScreen);
        btnSettingsFSM = findViewById(R.id.btnSettingsFSM);
        speechBarFSM = findViewById(R.id.speechBarFSM);
        passedText = getIntent().getStringExtra("SpeechText");
        passedCategory = getIntent().getStringExtra("PC");
        speechBarFSM.setText(passedText);


        dTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = dTTS.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS","Language not supported");
                    }else
                    {
                        btnSpeakFSM.setEnabled(true);
                    }
                }else
                {
                    Log.e("TTS","Initialization failed");
                }
            }
        });

        btnSpeakFSM.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                speak();
            }
        });

        btnClearFSM.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clear();
            }
        });

        btnMinimiseScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMinimiseScreen();
            }
        });

        btnSettingsFSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        btnMainSettingsFSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainSettings();
            }
        });

        btnAddFSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentPhrase();
            }
        });

        //CHECK FOR COLOR BLIND STATUS
        checkForColorBlindStatus();

    }

    //COLOR BLIND FUNCTION
    private void checkForColorBlindStatus()
    {
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            btnSpeakFSM.setBackgroundResource(R.drawable.colorblindbtn);
            btnClearFSM.setBackgroundResource(R.drawable.colorblindbtn);
            btnMainSettingsFSM.setBackgroundResource(R.drawable.colorblindbtn);
            btnAddFSM.setBackgroundResource(R.drawable.colorblindbtn);
            btnMinimiseScreen.setBackgroundResource(R.drawable.colorblindbtn);
            btnSettingsFSM.setBackgroundResource(R.drawable.colorblindbtn);
            speechBarFSM.setBackgroundResource(R.drawable.colorblindbtn);
            fsvLayout.setBackgroundColor(Color.YELLOW);
        }
        else
        {
            btnSpeakFSM.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnClearFSM.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnMainSettingsFSM.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnAddFSM.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnMinimiseScreen.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnSettingsFSM.setBackgroundResource(R.drawable.selectedbuttonshape);
            speechBarFSM.setBackgroundResource(R.drawable.phrasesbox);
            fsvLayout.setBackgroundColor(Color.WHITE);
        }
    }

    //CONTROL BOX FUNCTIONS
    private void speak()
    {
        //SET UP SETTING DATA FOR THE VOICE
        //SET UP SETTING DATA FOR THE VOICE
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        float pitch = (float)Integer.parseInt(settingsData.getString(1)) / 50;
        if(pitch < 0.1) pitch = 0.1f;
        float speed = (float)Integer.parseInt(settingsData.getString(2)) / 50;
        if(speed < 0.1) speed = 0.1f;
        dTTS.setPitch(pitch);
        dTTS.setSpeechRate(speed);
        String textOfSb = speechBarFSM.getText().toString();
        //SPEAK FUNCTION
        dTTS.speak(textOfSb, TextToSpeech.QUEUE_FLUSH, null);
    }
    private void clear()
    {
        speechBarFSM.setText("");
    }
    private void setMinimiseScreen()
    {
        Intent intent = new Intent(this,StartActivity.class);
        intent.putExtra("PC",passedCategory);
        intent.putExtra("SpeechText",speechBarFSM.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openSettings()
    {
        Intent intent = new Intent(this, PhraseSettings.class);
        startActivity(intent);
    }
    private void openMainSettings()
    {
        Intent intent = new Intent(this,MainSettings.class);
        intent.putExtra("From","FullScreen");
        intent.putExtra("PC",passedCategory);
        intent.putExtra("SpeechText",speechBarFSM.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void addCurrentPhrase()
    {
        String currentPhrase = speechBarFSM.getText().toString();
        if(!currentPhrase.isEmpty())
        {
            Intent intent = new Intent(this,AddCurrentPhraseToCategory.class);
            intent.putExtra("CurrentPhrase",currentPhrase);
            intent.putExtra("From","FullScreen");
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Empty speechbar text.", Toast.LENGTH_SHORT).show();
        }
    }

    //IN CASE THE DATABASE IS NOT SET
    public void addInitialDataToSDB()
    {
        settingsDBH.insertData(1,50,50,"False","False","None");
        settingsData = settingsDBH.getAllData();
    }

    //End of Program
    public void onDestroy()
    {
        if(dTTS !=null)
        {
            dTTS.stop();
            dTTS.shutdown();
        }
        super.onDestroy();
    }
}
