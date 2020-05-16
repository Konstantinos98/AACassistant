package com.kos.AACassistant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchFor extends AppCompatActivity {

    private String activityFrom,categorySelected,speechText,passedValue;
    private Button btnReturnToSearch;
    private LinearLayout sfLayout;
    private TextView sfTitle;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<String> dataList;
    private EditText etxtSearchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for);

        //INTENTS AND VARIABLES
        activityFrom = getIntent().getStringExtra("From");
        categorySelected = getIntent().getStringExtra("PC");
        speechText = getIntent().getStringExtra("SpeechText");
        passedValue = getIntent().getStringExtra("For");
        dataList = new ArrayList<>();

        //LAYOUT SET UP
        sfLayout = findViewById(R.id.sfLayout);
        sfTitle = findViewById(R.id.sfTitle);
        btnReturnToSearch = findViewById(R.id.btnReturnToSearch);
        recyclerView = findViewById(R.id.rvSearch);
        etxtSearchBox = findViewById(R.id.etxtSearchBox);
        etxtSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        //BUTTONS FUNCTIONS
        btnReturnToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    returnToSearch();
            }
        });

        checkPassedValue();
        setUpRecyclerView();
        checkColorBlindStatus();
    }

    private void filter(String text)
    {
        ArrayList<String> filteredList = new ArrayList<>();
        for(String item : dataList)
        {
            if(item.toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
        }
        recyclerAdapter.filterList(filteredList);
    }

    private void checkPassedValue()
    {
        if(passedValue.equals("Categories"))
        {
            sfTitle.setText("Search Categories");
            CategoriesDBH categoriesDBH = new CategoriesDBH(this);
            Cursor categoriesData = categoriesDBH.getAllData();
            for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
            {
                dataList.add(categoriesData.getString(1));
            }
        }
        else
        {
            sfTitle.setText("Search Phrases");
            PhrasesDBH phrasesDBH = new PhrasesDBH(this);
            Cursor phrasesData = phrasesDBH.getAllData();
            for(phrasesData.moveToFirst();!phrasesData.isAfterLast();phrasesData.moveToNext())
            {
                dataList.add(phrasesData.getString(1));
            }
        }
    }

    private void setUpRecyclerView()
    {
        recyclerAdapter = new RecyclerAdapter(dataList,Boolean.FALSE,
                speechText,categorySelected,activityFrom,passedValue);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void checkColorBlindStatus()
    {
        SettingsDBH settingsDBH = new SettingsDBH(this);
        Cursor settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            sfLayout.setBackgroundResource(R.color.yellow);
            sfTitle.setTextColor(Color.BLACK);
            btnReturnToSearch.setBackgroundResource(R.drawable.colorblindbtn);
            recyclerView.setBackgroundResource(R.drawable.colorblindbtn);
            etxtSearchBox.setBackgroundResource(R.drawable.colorblindbtn);
        }
        else
        {
            sfLayout.setBackgroundResource(R.color.gray);
            sfTitle.setTextColor(Color.WHITE);
            btnReturnToSearch.setBackgroundResource(R.drawable.selectedbuttonshape);
            recyclerView.setBackgroundResource(R.color.white_smoke);
            etxtSearchBox.setBackgroundResource(R.drawable.selectedbuttonshape);
        }
    }

    private void returnToSearch()
    {
        Intent intent = new Intent(this,Search.class);
        intent.putExtra("From",activityFrom);
        intent.putExtra("PC",categorySelected);
        intent.putExtra("SpeechText",speechText);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
