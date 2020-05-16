package com.kos.AACassistant;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class Add extends AppCompatActivity {

    private TextView txtTitle;
    private TextView txtTitleHelper;
    private TextView txtPGPD;
    private TextView txtPCText;
    private String passedValue;
    private String categoryName="";
    private Button btnExit;
    private Button btnAdd;
    private EditText etxtPCT;
    private EditText etxtPhraseDisplay;
    private Spinner parentCategoryList;
    private Spinner categoryList;
    private Cursor categoriesData;
    private LinearLayout addLayout;
    private SettingsDBH settingsDBH;
    private Cursor settingsData;

    //CREATION FUNCTION
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //SETTING UP THE POP UP WINDOW
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int)(w*.9),(int)(h*.8));

        //SET UP VARIABLES AND OBJECTS
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();
        passedValue = getIntent().getExtras().getString("Box");
        categoryName = getIntent().getExtras().getString("CategoryName");
        addLayout = findViewById(R.id.addLayout);
        btnExit = findViewById(R.id.btnExitAdd);
        btnAdd = findViewById(R.id.btnAdd);
        txtTitle = findViewById(R.id.txttitle);
        txtTitleHelper = findViewById(R.id.txtTitleHelperAdd);
        txtPGPD = findViewById(R.id.txtPGPD);
        etxtPCT = findViewById(R.id.etxtPCT);
        etxtPhraseDisplay = findViewById(R.id.etxtPhraseDisplay);
        txtPCText = findViewById(R.id.txtPCText);
        parentCategoryList = findViewById(R.id.parentCategoryList);

        //CHECK PASSED VALUE
        if(passedValue.equals("Category"))
        {
            etxtPhraseDisplay.setVisibility(View.GONE);
            txtTitle.setText("Add Category");
            txtTitleHelper.setText("* Fill the data and then press 'Add' button to add a new category");
            txtPCText.setText("Category Name :");
            txtPGPD.setText("Parent Category :");
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCategoryFunction();
                    }
            });
            setUpCategoryAdd();
        }
        else
        {
            parentCategoryList.setVisibility(View.GONE);
            txtTitle.setText("Add Phrase");
            txtTitleHelper.setText("* Fill the data and then press 'Add' button to add a new phrase");
            txtPCText.setText("Phrase Text :");
            txtPGPD.setText("Phrase Display :");
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhraseFunction();
                }
            });
            setUpPhraseAdd();
        }

        //SET ON CLICK FUNCTION TO THE BUTTONS
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    exitAdd();
            }
        });

        checkColorBlindStatus();
    }
    //END OF CREATION FUNCTION

    public void checkColorBlindStatus()
    {
        SettingsDBH settingsDBH = new SettingsDBH(this);
        Cursor settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            addLayout.setBackgroundResource(R.drawable.selectedcolorblindbtn);
            btnExit.setBackgroundResource(R.drawable.colorblindbtn);
            btnAdd.setBackgroundResource(R.drawable.colorblindbtn);
            txtTitle.setTextColor(Color.BLACK);
            txtTitleHelper.setBackgroundResource(R.drawable.colorblindbtn);
            txtPGPD.setTextColor(Color.BLACK);
            txtPCText.setTextColor(Color.BLACK);
            etxtPCT.setBackgroundResource(R.drawable.colorblindbtn);
            etxtPhraseDisplay.setBackgroundResource(R.drawable.colorblindbtn);
        }
        else
        {
            addLayout.setBackgroundResource(R.drawable.pop_up_window_border);
            btnExit.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnAdd.setBackgroundResource(R.drawable.selectedbuttonshape);
            txtTitle.setTextColor(Color.WHITE);
            txtTitleHelper.setBackgroundResource(R.color.light_grey);
            txtPGPD.setTextColor(Color.WHITE);
            txtPCText.setTextColor(Color.WHITE);
            etxtPCT.setBackgroundResource(R.drawable.editbox);
            etxtPhraseDisplay.setBackgroundResource(R.drawable.editbox);
        }
    }

    //SET UP ACTIVITY FOR CATEGORY OR PHRASE
    private void setUpCategoryAdd()
    {
        //DIFFERENT DISPLAY IN CATEGORY ADD
        categoryList = findViewById(R.id.parentCategoryList);
        CategoriesDBH db = new CategoriesDBH(this);
        categoriesData = db.getAllData();
        List<String> categories = new ArrayList<String>();
        categories.add("None");
        for(categoriesData.moveToFirst(); !categoriesData.isAfterLast(); categoriesData.moveToNext())
        {
            categories.add(categoriesData.getString(1));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        categoryList.setAdapter(dataAdapter);
    }
    private void setUpPhraseAdd()
    {
        //DIFFERENT DISPLAY IN PHRASE ADD
        etxtPhraseDisplay = findViewById(R.id.etxtPhraseDisplay);
    }

    //SET EXIT BUTTON
    private void exitAdd()
    {
        finish();
    }

    //ADD PHRASE FUNCTIONS
    private void addPhraseFunction()
    {
        //DATA VALIDATION
        if((etxtPCT.getText().toString().trim().length() == 0) || (etxtPhraseDisplay.getText().toString().trim().length() == 0))
        {
            alertDialog("Fill all the input fields to proceed.");
        }
        else
        {
            PhrasesDBH pdbh = new PhrasesDBH(this);
            int newID = pdbh.getNewID();
            pdbh.insertData(newID,etxtPCT.getText().toString(),etxtPhraseDisplay.getText().toString(),"Empty");
            Toast.makeText(this, "Added new phrase!", Toast.LENGTH_SHORT).show();
            etxtPhraseDisplay.setText("");
            etxtPCT.setText("");
            addPhraseToCategory(newID,categoryName);
        }
    }
    private void addPhraseToCategory(int phraseId, @NotNull String cname)
    {
        if(cname.equals("Favourites"))
        {
            MainCategoriesDBH mcdbh = new MainCategoriesDBH(this);
            Cursor mCategoriesData = mcdbh.getAllData();
            mCategoriesData.moveToFirst();
            String mCategoryList = mCategoriesData.getString(2);
            if(mCategoryList.equals(""))
            {
                mCategoryList = String.valueOf(phraseId);
            }
            else
            {
                mCategoryList = mCategoryList + "," + phraseId;
            }
            mcdbh.updateData("1","Favourites",mCategoryList);
        }
        else {
            CategoriesDBH cdbh = new CategoriesDBH(this);
            Cursor result = cdbh.getAllData();
            String categoryRecordID;
            Integer parentCategoryID;
            String categoryRecordList;
            for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                if (cname.equals(result.getString(1))) {
                    categoryRecordID = result.getString(0);
                    parentCategoryID = Integer.parseInt(result.getString(3));
                    categoryRecordList = result.getString(4);
                    if (categoryRecordList.equals("")) {
                        categoryRecordList = String.valueOf(phraseId);
                    } else {
                        String phrase = String.valueOf(phraseId);
                        categoryRecordList = categoryRecordList + "," + phrase;
                    }
                    cdbh.updateData(categoryRecordID, cname, "Empty", parentCategoryID, categoryRecordList);
                }
            }
        }
    }

    //ADD CATEGORY FUNCTION
    private void addCategoryFunction()
    {
        //DATA VALIDATION
        if(etxtPCT.getText().toString().trim().length() == 0)
        {
            alertDialog("Fill all the input fields to proceed.");
        }
        else
        {
            Boolean duplicatedName = false;
            CategoriesDBH cdbh = new CategoriesDBH(this);
            Cursor categoriesData = cdbh.getAllData();
            int newID = cdbh.getNewID();
            int parentID = 0;
            for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
            {
                if(categoriesData.getString(1).toLowerCase().equals(etxtPCT.getText().toString().toLowerCase()))
                {
                    duplicatedName = true;
                }
            }
            if(duplicatedName == false)
            {
                if(!categoryList.getSelectedItem().equals("None"))
                {
                    for(categoriesData.moveToFirst(); !categoriesData.isAfterLast();categoriesData.moveToNext())
                    {
                        if(categoryList.getSelectedItem().equals(categoriesData.getString(1)))
                        {
                            parentID = Integer.parseInt(categoriesData.getString(0));
                        }
                    }
                }
                cdbh.insertData(newID,etxtPCT.getText().toString(),"Empty",parentID,"");
                etxtPCT.setText("");
                Toast.makeText(this, "Added new Category!", Toast.LENGTH_SHORT).show();
                //RELOAD LIST OF CATEGORIES
                setUpCategoryAdd();
            }
            else
            {
                alertDialog("Category name already exists!");
            }
        }
    }


    //ALERT BOX
    public void alertDialog(String message)
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
        Intent intent = new Intent(this,StartActivity.class);
        intent.putExtra("PC",categoryName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onDestroy();
    }

}
