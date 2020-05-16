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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Edit extends AppCompatActivity {

    private LinearLayout editLayout;
    private TextView txtCategoryName;
    private TextView txtParentCategory;
    private TextView txtTitleEdit;
    private String passedValue;
    private ArrayList<String> phrasesIdList;
    private TextView txtEditInfo;
    private String passedCategory;
    private LinearLayout editCategoryLayout;
    private LinearLayout editPhrasesLayout;
    private Button btnExit;
    private Button btnSave;
    private CategoriesDBH categoriesDBH;
    private PhrasesDBH phrasesDBH;
    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private Cursor databaseResult;
    private EditText etxtCategoryName;
    private Spinner parentCategoryList;
    List<String> categoriesList = new ArrayList<String>();
    List<String> subCategoriesIDs = new ArrayList<String>();
    List<Button> phrasesListbtn = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //SETTING UP THE POP UP WINDOW
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int) (w * .9), (int) (h * .8));

        settingsDBH = new SettingsDBH(this);
        passedCategory = getIntent().getStringExtra("PC");
        passedValue = getIntent().getStringExtra("Box");
        editLayout = findViewById(R.id.editLayout);
        txtTitleEdit = findViewById(R.id.txtTitleEdit);
        txtEditInfo = findViewById(R.id.txtEditInfo);
        editPhrasesLayout = findViewById(R.id.editPhrasesLayout);
        editCategoryLayout = findViewById(R.id.editCategoryLayout);
        btnSave = findViewById(R.id.btnEditSave);
        btnExit = findViewById(R.id.btnEditExit);
        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtParentCategory = findViewById(R.id.txtParentCategory);
        etxtCategoryName = findViewById(R.id.editCategoryName);
        parentCategoryList = findViewById(R.id.editParentCategoryList);

        if (passedValue.equals("Category")) {
            //EDIT CATEGORY
            setUpCategoryEdit();
        } else {
            //EDIT PHRASE
            setUpPhrasesEdit();
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkColorBlindStatus();
    }

    private void checkColorBlindStatus()
    {
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            editLayout.setBackgroundResource(R.drawable.selectedcolorblindbtn);
            txtTitleEdit.setTextColor(Color.BLACK);
            txtEditInfo.setBackgroundResource(R.drawable.colorblindbtn);
            editPhrasesLayout.setBackgroundResource(R.drawable.colorblindbtn);
            editCategoryLayout.setBackgroundResource(R.drawable.colorblindbtn);
            btnSave.setBackgroundResource(R.drawable.colorblindbtn);
            btnExit.setBackgroundResource(R.drawable.colorblindbtn);
            txtCategoryName.setTextColor(Color.BLACK);
            txtParentCategory.setTextColor(Color.BLACK);
            etxtCategoryName.setBackgroundResource(R.drawable.colorblindbtn);
            if(!passedValue.equals("Category"))
            {
                for(int index =0;index < phrasesListbtn.size();index++)
                {
                    phrasesListbtn.get(index).setBackgroundResource(R.drawable.selectedcolorblindbtn);
                }
            }
        }
        else
        {
            editLayout.setBackgroundResource(R.drawable.pop_up_window_border);
            txtTitleEdit.setTextColor(Color.WHITE);
            txtEditInfo.setBackgroundResource(R.color.light_grey);
            editPhrasesLayout.setBackgroundResource(R.color.white);
            editCategoryLayout.setBackgroundResource(R.color.gray);
            btnSave.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnExit.setBackgroundResource(R.drawable.selectedbuttonshape);
            txtCategoryName.setTextColor(Color.WHITE);
            txtParentCategory.setTextColor(Color.WHITE);
            etxtCategoryName.setBackgroundResource(R.drawable.editbox);
            if(!passedValue.equals("Category"))
            {
                for(int index =0;index < phrasesListbtn.size();index++)
                {
                    phrasesListbtn.get(index).setBackgroundResource(R.drawable.selectedbuttonshape);
                }
            }
        }
    }

    private void setUpCategoryEdit() {
        String parentCategoryID = "0";
        String currentCategoryID = "0";
        editPhrasesLayout.setVisibility(LinearLayout.GONE);
        txtTitleEdit.setText("Edit Category");
        txtEditInfo.setText("Edit the category and save your changes by clicking the 'Save' button.");
        categoriesDBH = new CategoriesDBH(this);
        databaseResult = categoriesDBH.getAllData();
        for (databaseResult.moveToFirst(); !databaseResult.isAfterLast(); databaseResult.moveToNext()) {
            if (databaseResult.getString(1).equals(passedCategory)) {
                etxtCategoryName.setText(databaseResult.getString(1));
                parentCategoryID = databaseResult.getString(3);
                currentCategoryID = databaseResult.getString(0);
            }
        }
        checkForAnySubCategories(currentCategoryID);
        categoriesList.add("None");
        for (databaseResult.moveToFirst(); !databaseResult.isAfterLast(); databaseResult.moveToNext()) {
            //NOT THE PASSED CATEGORY
            if (!passedCategory.equals(databaseResult.getString(1))) {
                //NOT ANY SUBCATEGORY
                Boolean subCategoryFound = false;
                for (int index = 0; index < subCategoriesIDs.size(); index++) {
                    if (databaseResult.getString(0).equals(subCategoriesIDs.get(index))) {
                        subCategoryFound = true;
                    }
                }
                if (subCategoryFound == false) {
                    categoriesList.add(databaseResult.getString(1));
                }
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesList);
        parentCategoryList.setAdapter(dataAdapter);
        if (parentCategoryID.equals("0")) {
            parentCategoryList.setSelection(0);
        } else {
            String parentCategoryName = "";
            for (databaseResult.moveToFirst(); !databaseResult.isAfterLast(); databaseResult.moveToNext()) {
                if (databaseResult.getString(0).equals(parentCategoryID)) {
                    parentCategoryName = databaseResult.getString(1);
                }
            }
            for (int index = 0; index < parentCategoryList.getCount(); index++) {
                if (parentCategoryList.getItemAtPosition(index).equals(parentCategoryName)) {
                    parentCategoryList.setSelection(index);
                    break;
                }
            }
        }
        //SAVE BUTTON FUNCTIONALITY ONLY IN CATEGORIES EDIT
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit();
            }
        });
    }

    private void checkForAnySubCategories(String currentCID)
    {
        Boolean subCategoryFound = false;
        Cursor categoriesDB = categoriesDBH.getAllData();
        List<String> tempSubcategoriesIDs = new ArrayList<String>();
        for (categoriesDB.moveToFirst(); !categoriesDB.isAfterLast(); categoriesDB.moveToNext())
        {
            if (categoriesDB.getString(3).equals(currentCID))
            {
                subCategoryFound = true;
                tempSubcategoriesIDs.add(categoriesDB.getString(0));
            }
        }
        if (subCategoryFound == true)
        {
            subCategoriesIDs.addAll(tempSubcategoriesIDs);
            for (int index = 0; index < tempSubcategoriesIDs.size(); index++)
            {
                checkForAnySubCategories(tempSubcategoriesIDs.get(index));
            }
        }
    }

    private void saveEdit() {
        //VARIABLES
        String categoryID_ = "0";
        String ParentID_ = "0";
        String PhrasesList_ = "";
        String newParentID = "0";
        String newCategoryName = passedCategory;
        boolean parentCategoryCheck = false;
        boolean nameCategoryCheck = false;
        String cnBox = etxtCategoryName.getText().toString();
        String pcBox = parentCategoryList.getSelectedItem().toString();

        //GET CURRENT CATEGORY RECORD ID
        for (databaseResult.moveToFirst(); !databaseResult.isAfterLast(); databaseResult.moveToNext()) {
            if (databaseResult.getString(1).equals(passedCategory)) {
                categoryID_ = databaseResult.getString(0);
                ParentID_ = databaseResult.getString(3);
                PhrasesList_ = databaseResult.getString(4);
            }
        }

        //NOT EMPTY CATEGORY NAME CHECK
        if (cnBox.equals("")) {
            nameCategoryCheck = false;
        } else {
            nameCategoryCheck = true;
        }

        //UPDATE CURRENT RECORD FOR ANY CHANGES
        if (nameCategoryCheck == true)
        {
            //CHECK FOR DUPLICATE CATEGORY NAMES
            Boolean duplicateName = false;
            Cursor dbCategories = categoriesDBH.getAllData();
            for (dbCategories.moveToFirst(); !dbCategories.isAfterLast(); dbCategories.moveToNext())
            {
                if(!cnBox.equals(passedCategory))
                {
                    if (dbCategories.getString(1).equals(cnBox))
                    {
                        duplicateName = true;
                    }
                }
            }
            if (duplicateName == false)
            {
                newCategoryName = cnBox;
                //CHECK FOR THE NEW PARENT CATEGORY AND NOT NONE
                if (!pcBox.equals("None")) {
                    for (databaseResult.moveToFirst(); !databaseResult.isAfterLast(); databaseResult.moveToNext()) {
                        if (databaseResult.getString(1).equals(pcBox)) {
                            parentCategoryCheck = true;
                            newParentID = databaseResult.getString(0);
                        }
                    }
                }
                if (parentCategoryCheck == true) {
                    ParentID_ = newParentID;
                }
                else
                {
                    ParentID_ = "0";
                }
                categoriesDBH.updateData(categoryID_, newCategoryName, "Empty", Integer.parseInt(ParentID_), PhrasesList_);
                Toast.makeText(this, "Category Updated", Toast.LENGTH_SHORT).show();
                passedCategory = newCategoryName;
            }
            else
            {
                alertDialog("Category name already exists!");
            }
        }
        else
        {
            alertDialog("Fill the data fields to save changes!");
        }
    }

    private void setUpPhrasesEdit()
    {
        phrasesListbtn.clear();
        editCategoryLayout.setVisibility(LinearLayout.GONE);
        btnSave.setVisibility(LinearLayout.GONE);
        LinearLayout phrasesListLayout = findViewById(R.id.phrasesListLayout);
        txtTitleEdit.setText("Edit Phrases");
        phrasesIdList = getIntent().getStringArrayListExtra("PhrasesIDList");
        txtEditInfo.setText("* Click on the phrase you want to edit or exit the window with 'Exit' button.");
        ArrayList<String> phrasesIdList = getIntent().getStringArrayListExtra("PhrasesIDList");
        phrasesDBH = new PhrasesDBH(this);
        databaseResult = phrasesDBH.getAllData();
        for(databaseResult.moveToFirst();!databaseResult.isAfterLast();databaseResult.moveToNext())
        {
            for(int index = 0;index < phrasesIdList.size();index++)
            {
                if(databaseResult.getString(0).equals(phrasesIdList.get(index)))
                {
                    Button btn = getButton(Integer.parseInt(databaseResult.getString(0)),databaseResult.getString(2));
                    phrasesListbtn.add(btn);
                    phrasesListLayout.addView(btn);
                }
            }
        }
    }

    @NotNull
    private Button getButton(int id, String text)
    {
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnParams.setMargins(2,10,2,10);
        Button btn = new Button(this);
        btn.setLayoutParams(btnParams);
        btn.setAllCaps(false);
        btn.setTextSize(15);
        btn.setBackgroundResource(R.drawable.selectedbuttonshape);
        btn.setId(id);
        btn.setText(text);
        final int fId = id;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditBox(fId);
            }
        });
        return btn;
    }

    private void openEditBox(int id)
    {
        Intent intent = new Intent(this,EditPhraseBox.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("ID",id);
        intent.putExtra("PC",passedCategory);
        intent.putExtra("Box",passedValue);
        intent.putExtra("PhrasesIDList",phrasesIdList);
        startActivity(intent);
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
        Intent intent = new Intent(this,StartActivity.class);
        intent.putExtra("PC",passedCategory);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onDestroy();
    }

}
