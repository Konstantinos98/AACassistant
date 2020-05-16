package com.kos.AACassistant;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainSettings extends AppCompatActivity {

    private LinearLayout msLayout;
    private TextView msTitle;
    private Button btnExit,btnSearch,btnAppInfo,btnVoiceRecognition,btnDatabaseControl,
            btnConvertToCopy,btnColorBlind,btnSortingCategories,btnInfo;
    private String activityFrom,categoryPassed,phrasePassed,colorBlind,copyMode;
    private SettingsDBH settingsDBH;
    private Cursor settingsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        //DATABASE VARIABLES
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();

        //LAYOUT CONTROLS
        btnInfo = findViewById(R.id.btnInfo);
        btnSearch = findViewById(R.id.btnSearch);
        btnExit = findViewById(R.id.btnExitMS);
        btnSortingCategories = findViewById(R.id.btnSortingCategories);
        btnColorBlind = findViewById(R.id.btnColorBlind);
        btnConvertToCopy = findViewById(R.id.btnConvertToCopy);
        btnDatabaseControl = findViewById(R.id.btnDatabaseControl);
        btnVoiceRecognition = findViewById(R.id.btnVoiceRecognition);
        btnAppInfo = findViewById(R.id.btnAppInfo);
        msLayout = findViewById(R.id.msLayout);
        msTitle = findViewById(R.id.msTitle);

        activityFrom = getIntent().getStringExtra("From");
        categoryPassed = getIntent().getStringExtra("PC");
        phrasePassed = getIntent().getStringExtra("SpeechText");

        checkForColorBlindStatus();
        checkForCopyModeStatus();

        //SET UP BUTTON FUNCTIONS
        btnVoiceRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openVoiceRecognition();
            }
        });
        btnAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openAppInfo();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
            }
        });
        btnColorBlind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchColorStatus();
            }
        });
        btnConvertToCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToCopyModeStatus();
            }
        });
        btnSortingCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSortingCategories();
            }
        });
        btnDatabaseControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatabaseControl();
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfo();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitSettings();
            }
        });
    }

    //CHECK STATUS FUNCTIONS
    private void checkForColorBlindStatus()
    {
        settingsData.moveToFirst();
        colorBlind = settingsData.getString(3);
        setColorBlind();
    }
    private void checkForCopyModeStatus()
    {
        settingsData.moveToFirst();
        copyMode = settingsData.getString(4);
        setCopyMode();
    }

    //BUTTONS FUNCTIONS
    private void openAppInfo()
    {
        Intent intent = new Intent(this, Guide.class);
        intent.putExtra("PC",categoryPassed);
        intent.putExtra("SpeechText",phrasePassed);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openVoiceRecognition()
    {
        Intent intent = new Intent(this,VoiceRecognition.class);
        intent.putExtra("PC",categoryPassed);
        intent.putExtra("SpeechText",phrasePassed);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openSearch()
    {
        Intent intent = new Intent(this,Search.class);
        intent.putExtra("PC",categoryPassed);
        intent.putExtra("SpeechText",phrasePassed);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openSortingCategories()
    {
        Intent intent = new Intent(this,SortingCategories.class);
        intent.putExtra("PC",categoryPassed);
        intent.putExtra("SpeechText",phrasePassed);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void switchColorStatus()
    {
        if(colorBlind.equals("True"))
        {
            colorBlind = "False";
        }
        else
        {
            colorBlind = "True";
        }
        setColorBlind();
    }
    private void setColorBlind()
    {
        if(colorBlind.equals("True"))
        {
            btnColorBlind.setText("Color Blind Disable");
            String id = "1";
            float p = Integer.parseInt(settingsData.getString(1));
            float s = Integer.parseInt(settingsData.getString(2));
            String cm = settingsData.getString(4);
            String sc = settingsData.getString(5);
            settingsDBH.updateData(id,p,s,"True",cm,sc);
            msLayout.setBackgroundColor(Color.YELLOW);
            msTitle.setTextColor(Color.BLACK);
            btnSearch.setBackgroundResource(R.drawable.colorblindbtn);
            btnSortingCategories.setBackgroundResource(R.drawable.colorblindbtn);
            btnColorBlind.setBackgroundResource(R.drawable.colorblindbtn);
            btnConvertToCopy.setBackgroundResource(R.drawable.colorblindbtn);
            btnDatabaseControl.setBackgroundResource(R.drawable.colorblindbtn);
            btnVoiceRecognition.setBackgroundResource(R.drawable.colorblindbtn);
            btnAppInfo.setBackgroundResource(R.drawable.colorblindbtn);
            btnExit.setBackgroundResource(R.drawable.colorblindbtn);
            btnInfo.setBackgroundResource(R.drawable.colorblindbtn);
        }
        else
        {
            btnSearch.setBackgroundResource(R.drawable.buttonshape);
            btnColorBlind.setText("Color Blind Enable");
            String id = "1";
            float p = Integer.parseInt(settingsData.getString(1));
            float s = Integer.parseInt(settingsData.getString(2));
            String cm = settingsData.getString(4);
            String sc = settingsData.getString(5);
            settingsDBH.updateData(id,p,s,"False",cm,sc);
            msLayout.setBackgroundColor(Color.GRAY);
            msTitle.setTextColor(Color.WHITE);
            btnSortingCategories.setBackgroundResource(R.drawable.buttonshape);
            btnColorBlind.setBackgroundResource(R.drawable.buttonshape);
            btnConvertToCopy.setBackgroundResource(R.drawable.buttonshape);
            btnDatabaseControl.setBackgroundResource(R.drawable.buttonshape);
            btnVoiceRecognition.setBackgroundResource(R.drawable.buttonshape);
            btnAppInfo.setBackgroundResource(R.drawable.buttonshape);
            btnExit.setBackgroundResource(R.drawable.buttonshape);
            btnInfo.setBackgroundResource(R.drawable.buttonshape);
        }
    }
    private void switchToCopyModeStatus()
    {
        if(copyMode.equals("True"))
        {
            copyMode = "False";
        }
        else
        {
            copyMode = "True";
        }
        setCopyMode();
    }
    private void setCopyMode()
    {
        if(copyMode.equals("True"))
        {
            btnConvertToCopy.setText("Copy Mode Disable");
            String id = "1";
            float p = Integer.parseInt(settingsData.getString(1));
            float s = Integer.parseInt(settingsData.getString(2));
            String cb = settingsData.getString(3);
            String sc = settingsData.getString(5);
            settingsDBH.updateData(id,p,s,cb,"True",sc);
        }
        else
        {
            btnConvertToCopy.setText("Copy Mode Enable");
            String id = "1";
            float p = Integer.parseInt(settingsData.getString(1));
            float s = Integer.parseInt(settingsData.getString(2));
            String cb = settingsData.getString(3);
            String sc = settingsData.getString(5);
            settingsDBH.updateData(id,p,s,cb,"False",sc);
        }
    }
    private void openDatabaseControl()
    {
        Intent intent = new Intent(this,DatabaseControl.class);
        intent.putExtra("PC",categoryPassed);
        intent.putExtra("SpeechText",phrasePassed);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openInfo()
    {
        Intent intent = new Intent(this,AppInfo.class);
        intent.putExtra("PC",categoryPassed);
        intent.putExtra("SpeechText",phrasePassed);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void exitSettings()
    {
        if(activityFrom.equals("FullScreen"))
        {
            Intent intent = new Intent(this,FullScreenView.class);
            intent.putExtra("PC",categoryPassed);
            intent.putExtra("SpeechText",phrasePassed);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this,StartActivity.class);
            intent.putExtra("PC",categoryPassed);
            intent.putExtra("SpeechText",phrasePassed);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }
}
