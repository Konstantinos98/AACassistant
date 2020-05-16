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

public class Search extends AppCompatActivity {

    private LinearLayout searchLayout;
    private TextView txtSearchTitle,txtSearchInfo;
    private Button btnSearchCategories,btnSearchPhrases,btnSReturnToSettings;
    private String activityFrom,categorySelected,speechText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //LAYOUT OBJECTS
        searchLayout = findViewById(R.id.searchLayout);
        txtSearchTitle = findViewById(R.id.txtSearchTitle);
        txtSearchInfo = findViewById(R.id.txtSearchInfo);
        btnSearchCategories = findViewById(R.id.btnSearchCategories);
        btnSearchPhrases = findViewById(R.id.btnSearchPhrases);
        btnSReturnToSettings = findViewById(R.id.btnSReturnToSettings);

        //INTENTS
        activityFrom = getIntent().getStringExtra("From");
        categorySelected = getIntent().getStringExtra("PC");
        speechText = getIntent().getStringExtra("SpeechText");

        //BUTTON FUNCTION SET UP
        btnSearchCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoriesSearch();
            }
        });
        btnSearchPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhrasesSearch();
            }
        });
        btnSReturnToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainSettings();
            }
        });

        checkColorBlindStatus();
    }

    private void checkColorBlindStatus()
    {
        SettingsDBH settingsDBH = new SettingsDBH(this);
        Cursor settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            searchLayout.setBackgroundResource(R.color.yellow);
            txtSearchTitle.setTextColor(Color.BLACK);
            txtSearchInfo.setBackgroundResource(R.drawable.colorblindbtn);
            btnSReturnToSettings.setBackgroundResource(R.drawable.colorblindbtn);
            btnSearchCategories.setBackgroundResource(R.drawable.colorblindbtn);
            btnSearchPhrases.setBackgroundResource(R.drawable.colorblindbtn);
        }
        else
        {
            searchLayout.setBackgroundResource(R.color.gray);
            txtSearchTitle.setTextColor(Color.WHITE);
            txtSearchInfo.setBackgroundResource(R.color.light_grey);
            btnSReturnToSettings.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnSearchCategories.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnSearchPhrases.setBackgroundResource(R.drawable.selectedbuttonshape);
        }

    }

    //BUTTONS FUNCTIONS
    private void openCategoriesSearch()
    {
        Intent intent = new Intent(this,SearchFor.class);
        intent.putExtra("For","Categories");
        intent.putExtra("From",activityFrom);
        intent.putExtra("PC",categorySelected);
        intent.putExtra("SpeechText",speechText);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openPhrasesSearch()
    {
        Intent intent = new Intent(this,SearchFor.class);
        intent.putExtra("For","Phrases");
        intent.putExtra("From",activityFrom);
        intent.putExtra("PC",categorySelected);
        intent.putExtra("SpeechText",speechText);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
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
