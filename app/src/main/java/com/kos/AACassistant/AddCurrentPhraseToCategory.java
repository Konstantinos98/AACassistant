package com.kos.AACassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddCurrentPhraseToCategory extends AppCompatActivity {

    private LinearLayout addCurrentLayout;
    private TextView acpTitle;
    private TextView txtACPCategory;
    private TextView txtACPPhraseDisplay;
    private String passedValue;
    private String activityFrom;
    private TextView txtCurrentPhraseInfo;
    private Spinner spinnerCategoryList;
    private EditText etxtCurrentPhraseDisplay;
    private CategoriesDBH cdbh;
    private MainCategoriesDBH mcdbh;
    private Cursor categoriesData;
    private Button btnAdd;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_current_phrase_to_category);
        //PASSED VALUES
        passedValue = getIntent().getExtras().getString("CurrentPhrase");
        activityFrom = getIntent().getExtras().getString("From");
        //SETTING UP THE POP UP WINDOW
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int)(w*.9),(int)(h*.8));
        //SET UP LAYOUT MODELS
        txtACPPhraseDisplay = findViewById(R.id.txtACPPhraseDisplay);
        txtACPCategory = findViewById(R.id.txtACPCategory);
        acpTitle = findViewById(R.id.acpTitle);
        addCurrentLayout = findViewById(R.id.addCurrentLayout);
        txtCurrentPhraseInfo = findViewById(R.id.txtCurrentPhraseInfo);
        txtCurrentPhraseInfo.setText("* Choose a category to set the current phrase from the list below and set fill 'Phrase Display'. Then press 'Add' button.");
        spinnerCategoryList = findViewById(R.id.spinnerCategoryList);
        etxtCurrentPhraseDisplay = findViewById(R.id.etxtCurrentPhraseDisplay);
        btnAdd = findViewById(R.id.btnCreateAndAdd);
        btnExit = findViewById(R.id.btnExitAddCurrentPhraseToCategory);
        //SET UP VARIABLES
        mcdbh = new MainCategoriesDBH(this);
        cdbh = new CategoriesDBH(this);
        categoriesData = cdbh.getAllData();
        //FUNCTIONS TO SET UP LAYOUT
        setUpCategoriesList();
        setUpButtons();
        checkColorBlindStatus();
    }

    public void checkColorBlindStatus()
    {
        SettingsDBH settingsDBH = new SettingsDBH(this);
        Cursor settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            addCurrentLayout.setBackgroundResource(R.drawable.selectedcolorblindbtn);
            btnExit.setBackgroundResource(R.drawable.colorblindbtn);
            btnAdd.setBackgroundResource(R.drawable.colorblindbtn);
            acpTitle.setTextColor(Color.BLACK);
            txtCurrentPhraseInfo.setBackgroundResource(R.drawable.colorblindbtn);
            txtACPCategory.setTextColor(Color.BLACK);
            txtACPPhraseDisplay.setTextColor(Color.BLACK);
            etxtCurrentPhraseDisplay.setBackgroundResource(R.drawable.colorblindbtn);
        }
        else
        {
            addCurrentLayout.setBackgroundResource(R.drawable.pop_up_window_border);
            btnExit.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnAdd.setBackgroundResource(R.drawable.selectedbuttonshape);
            acpTitle.setTextColor(Color.WHITE);
            txtCurrentPhraseInfo.setBackgroundResource(R.color.light_grey);
            txtACPCategory.setTextColor(Color.WHITE);
            txtACPPhraseDisplay.setTextColor(Color.WHITE);
            etxtCurrentPhraseDisplay.setBackgroundResource(R.drawable.editbox);
        }
    }

    private void setUpCategoriesList()
    {
        List<String> categories = new ArrayList<String>();
        categories.add("Favourites");
        for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
        {
            categories.add(categoriesData.getString(1));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        spinnerCategoryList.setAdapter(dataAdapter);
    }

    private void setUpButtons()
    {
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndAddPhrase();
            }
        });
    }

    private void createAndAddPhrase()
    {
        //DATA VALIDATION CHECK FIRST
        if(!etxtCurrentPhraseDisplay.getText().toString().equals(""))
        {
            //CREATE AND ADD PHRASE TO PHRASES DB
            PhrasesDBH pdbh = new PhrasesDBH(this);
            int newID = pdbh.getNewID();
            pdbh.insertData(newID, passedValue, etxtCurrentPhraseDisplay.getText().toString(), "Empty");

            //ADD CREATED PHRASE TO THE SELECTED CATEGORY
            if (spinnerCategoryList.getSelectedItem().equals("Favourites"))
            {
                Cursor mCategoriesData = mcdbh.getAllData();
                mCategoriesData.moveToFirst();
                String mCategoryList = mCategoriesData.getString(2);
                if(mCategoryList.equals(""))
                {
                    mCategoryList = String.valueOf(newID);
                }
                else
                {
                    mCategoryList = mCategoryList + "," + newID;
                }
                mcdbh.updateData("1","Favourites",mCategoryList);
            }
            else
            {
                String categoryRecordID;
                String categoryRecordList;
                Integer categoryParentID;
                for (categoriesData.moveToFirst(); !categoriesData.isAfterLast(); categoriesData.moveToNext())
                {
                    if (spinnerCategoryList.getSelectedItem().equals(categoriesData.getString(1)))
                    {
                        categoryRecordID = categoriesData.getString(0);
                        categoryParentID = Integer.parseInt(categoriesData.getString(3));
                        categoryRecordList = categoriesData.getString(4);
                        if (categoryRecordList.equals("")) {
                            categoryRecordList = String.valueOf(newID);
                        } else {
                            categoryRecordList = categoryRecordList + "," + newID;
                        }
                        cdbh.updateData(categoryRecordID, spinnerCategoryList.getSelectedItem().toString(), "Empty", categoryParentID, categoryRecordList);
                    }
                }
            }
            finish();
        }
        else
        {
            alertDialog("Fill all the input fields to proceed.");
        }
    }

    //ALERT BOX
    private void alertDialog(String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Input Error!")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }

    public void onDestroy()
    {
        if(activityFrom.equals("NormalScreen")) {
            Intent intent = new Intent(this, StartActivity.class);
            intent.putExtra("PC", spinnerCategoryList.getSelectedItem().toString());
            intent.putExtra("SpeechText", passedValue);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            super.onDestroy();
        }
        else
        {
            Intent intent = new Intent(this, FullScreenView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("PC",spinnerCategoryList.getSelectedItem().toString());
            intent.putExtra("SpeechText",passedValue);
            startActivity(intent);
            super.onDestroy();
        }
    }

}
