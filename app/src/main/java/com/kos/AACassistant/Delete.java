package com.kos.AACassistant;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class Delete extends AppCompatActivity {

    private LinearLayout deleteLayout;
    private LinearLayout deleteBox;
    private Button btnExit;
    private Button btnDelete;
    private TextView txtTitle;
    private TextView txtTitleHelper;
    private String passedValue;
    private ArrayList<String> phrasesIdList;
    private ArrayList<Button> btnArrayList;
    private ArrayList<Button> deleteSelection;
    private LinearLayout listLayout;
    private CategoriesDBH cdbh;
    private PhrasesDBH pdbh;
    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private Cursor categoriesData;
    private Cursor phrasesData;
    private Integer affectedRows;
    private String passedCategory="";
    private String colorBlind;

    //CREATION FUNCTION
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        //SETTING UP THE POP UP WINDOW
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int)(w*.9),(int)(h*.8));

        //SET UP DATABASE VARIABLES
        cdbh = new CategoriesDBH(this);
        pdbh = new PhrasesDBH(this);
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();
        categoriesData = cdbh.getAllData();
        phrasesData = pdbh.getAllData();

        //SET UP VARIABLES AND OBJECTS
        deleteLayout = findViewById(R.id.deleteLayout);
        deleteBox = findViewById(R.id.deleteBox);
        btnExit = findViewById(R.id.btnExitDelete);
        btnDelete = findViewById(R.id.btnDelete);
        txtTitleHelper = findViewById(R.id.txtTitleHelperDelete);
        listLayout = findViewById(R.id.deleteListView);
        btnArrayList = new ArrayList<Button>();
        deleteSelection = new ArrayList<Button>();
        passedValue = getIntent().getExtras().getString("Box");
        txtTitle = findViewById(R.id.txttitleD);

        //CHECK PASSED VALUE
        if(passedValue.equals("Category"))
        {
            txtTitle.setText("Delete Category");
            txtTitleHelper.setText("* Select the categories you want to delete and then press the 'Delete' button. ");
            setUpDeleteCategory();
        }
        else
        {
            txtTitle.setText("Delete Phrase");
            txtTitleHelper.setText("* Select the phrases you want to delete and then press the 'Delete' button. ");
            phrasesIdList = getIntent().getStringArrayListExtra("PhrasesIDList");
            passedCategory = getIntent().getExtras().getString("CategorySelected");
            setUpDeletePhrase();
        }

        //SET RESULTED BUTTONS FUNCTIONS
        setButtons();

        //SET ON CLICK FUNCTION TO THE BUTTONS
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDelete();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFunction();
            }
        });

        checkColorBlindStatus();
    }
    //END OF CREATION FUNCTION

    private void checkColorBlindStatus()
    {
        settingsData.moveToFirst();
        colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            deleteLayout.setBackgroundResource(R.drawable.selectedcolorblindbtn);
            deleteBox.setBackgroundResource(R.drawable.colorblindbtn);
            txtTitle.setTextColor(Color.BLACK);
            txtTitleHelper.setBackgroundResource(R.drawable.colorblindbtn);
            listLayout.setBackgroundResource(R.color.light_yellow);
            btnDelete.setBackgroundResource(R.drawable.colorblindbtn);
            btnExit.setBackgroundResource(R.drawable.colorblindbtn);
            for(int index =0;index < btnArrayList.size();index++)
            {
                btnArrayList.get(index).setBackgroundResource(R.drawable.colorblindbtn);
            }
        }
        else
        {
            deleteLayout.setBackgroundResource(R.drawable.pop_up_window_border);
            deleteBox.setBackgroundResource(R.color.white);
            txtTitle.setTextColor(Color.WHITE);
            txtTitleHelper.setBackgroundResource(R.color.light_grey);
            listLayout.setBackgroundResource(R.color.white);
            btnDelete.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnExit.setBackgroundResource(R.drawable.selectedbuttonshape);
            for(int index =0;index < btnArrayList.size();index++)
            {
                btnArrayList.get(index).setBackgroundResource(R.drawable.listnonselectedbtn);
            }
        }
    }

    //SET UP ACTIVITY FOR CATEGORY OR PHRASE (BUTTONS INCLUDED / FUNCTIONALITY)
    private void setUpDeleteCategory()
    {
        listLayout.removeAllViews();
        btnArrayList.clear();
        for(categoriesData.moveToFirst(); !categoriesData.isAfterLast(); categoriesData.moveToNext())
        {
            btnArrayList.add(getButton
                    (Integer.parseInt(categoriesData.getString(0)),
                            categoriesData.getString(1)));
        }
        for(int counter = 0; counter < btnArrayList.size(); counter++)
        {
            listLayout.addView(btnArrayList.get(counter));
        }
    }
    private void setUpDeletePhrase()
    {
        listLayout.removeAllViews();
        btnArrayList.clear();
        for (int counter = 0; counter < phrasesIdList.size(); counter++)
        {
            for(phrasesData.moveToFirst(); !phrasesData.isAfterLast(); phrasesData.moveToNext())
            {
                if(phrasesData.getString(0).equals(phrasesIdList.get(counter)))
                {
                    btnArrayList.add(getButton
                            (Integer.parseInt(phrasesData.getString(0))
                                    ,phrasesData.getString(2)));
                }
            }
        }
        for(int counter = 0; counter < btnArrayList.size(); counter++)
        {
            listLayout.addView(btnArrayList.get(counter));
        }
    }
    private void setButtons()
    {
        for(int index=0; index < btnArrayList.size(); index++)
        {
            final Button btn = btnArrayList.get(index);
            btnArrayList.get(index).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        selectedCheck(btn);
                }
            });
        }
    }
    private void selectedCheck(Button btn)
    {
        if(colorBlind.equals("False"))
        {
            if(btn.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.listnonselectedbtn).getConstantState()))
            {
                btn.setBackgroundResource(R.drawable.listselectedbtn);
                deleteSelection.add(btn);
            }
            else
            {
                btn.setBackgroundResource(R.drawable.listnonselectedbtn);
                deleteSelection.remove(btn);
            }
        }
        else
        {
            if(btn.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.colorblindbtn).getConstantState()))
            {
                btn.setBackgroundResource(R.drawable.selectedcolorblindbtn);
                deleteSelection.add(btn);
            }
            else
            {
                btn.setBackgroundResource(R.drawable.colorblindbtn);
                deleteSelection.remove(btn);
            }
        }
    }

    //BUTTON GENERATOR
    @NotNull
    private Button getButton(int id, String text)
    {
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams .MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(2,10,2,10);
        Button btn = new Button(this);
        btn.setLayoutParams(btnParams);
        btn.setAllCaps(false);
        btn.setTextSize(15);
        btn.setClickable(true);
        btn.setId(id);
        btn.setText(text);
        return btn;
    }

    //SET DELETE AND EXIT BUTTON
    private void exitDelete()
    {
        finish();
    }
    private void deleteFunction()
    {
        affectedRows = 0;
        if(deleteSelection.isEmpty())
        {
            Toast.makeText(this, "Deleted : Nothing", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(passedValue.equals("Category"))
            {
                for (int counter = 0; counter < deleteSelection.size(); counter++)
                {
                    String categoryName = deleteSelection.get(counter).getText().toString();
                    deletePhrasesOfCategory(categoryName);
                    deleteCategory(deleteSelection.get(counter).getId());
                }
                fixSortedCategories(deleteSelection);
            }
            else
            {
                for(int counter = 0; counter < deleteSelection.size(); counter++)
                {
                    deletePhraseIdFromCategory(passedCategory,deleteSelection.get(counter).getId());
                    deletePhrase(deleteSelection.get(counter).getId());
                }
            }
            Toast.makeText(this, "Deleted : " + affectedRows + " Rows", Toast.LENGTH_SHORT).show();
            reOpenDelete();
        }
    }

    //DELETE CATEGORIES FUNCTIONS
    private void deletePhrasesOfCategory(String category)
    {
        categoriesData = cdbh.getAllData();
        ArrayList<Integer> categoryPhrasesList = new ArrayList<Integer>();
        for (categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
        {
            if(categoriesData.getString(1).equals(category))
            {
                if (!categoriesData.getString(4).equals(""))
                {
                    String[] phrasesID = categoriesData.getString(4).split(",");
                    for (int counter = 0; counter < phrasesID.length; counter++)
                    {
                        categoryPhrasesList.add(Integer.parseInt(phrasesID[counter]));
                    }
                }
            }
        }
        if(!categoryPhrasesList.isEmpty())
        {
            for(int index = 0; index < categoryPhrasesList.size(); index++)
            {
                deletePhrase(categoryPhrasesList.get(index));
            }
        }
    }
    private void deleteCategory(Integer id)
    {
        affectedRows = affectedRows + cdbh.deleteData(String.valueOf(id));
    }
    private void fixSortedCategories(ArrayList<Button> deletedIds)
    {
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String sortedCategories = settingsData.getString(5);
        if(!sortedCategories.equals("None"))
        {
            String[] scIDs = sortedCategories.split(",");
            ArrayList<String> scList = new ArrayList<String>(Arrays.asList(scIDs));
            for(int index =0; index < deletedIds.size(); index++)
            {
                scList.remove(String.valueOf(deleteSelection.get(index).getId()));
            }
            String sc = "None";
            if(scList.size() > 0)
            {
                for(int counter = 0;counter < scList.size();counter++)
                {
                    if(counter == 0)
                    {
                        sc = scList.get(counter);
                    }
                    else
                    {
                        sc = sc + "," + scList.get(counter);
                    }
                }
            }
            updateSortedCategories(sc);
        }
    }
    private void updateSortedCategories(String sc)
    {
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String id = settingsData.getString(0);
        float p = Integer.parseInt(settingsData.getString(1));
        float s = Integer.parseInt(settingsData.getString(2));
        String cb = settingsData.getString(3);
        String cm = settingsData.getString(4);
        settingsDBH.updateData(id,p,s,cb,cm,sc);
    }

    //DELETE PHRASES FUNCTION
    private void deletePhraseIdFromCategory(@NotNull String categorySelected, Integer phraseId)
    {
        if(categorySelected.equals("Favourites"))
        {
            MainCategoriesDBH mcdbh = new MainCategoriesDBH(this);
            Cursor mCategoriesData = mcdbh.getAllData();
            mCategoriesData.moveToFirst();
            ArrayList<Integer> pIdList = new ArrayList<Integer>();
            String[] phrasesID = mCategoriesData.getString(2).split(",");
            for (int counter = 0; counter < phrasesID.length; counter++) {
                pIdList.add(Integer.parseInt(phrasesID[counter]));
            }
            pIdList.remove(phraseId);
            String mcategoriesPhrasesList = "";
            if(pIdList.size() == 0)
            {
                mcategoriesPhrasesList = "";
            }
            else
            {
                for (int counter = 0; counter < pIdList.size(); counter++) {
                    if (counter == 0) {
                        mcategoriesPhrasesList = String.valueOf(pIdList.get(counter));
                    } else {
                        mcategoriesPhrasesList = mcategoriesPhrasesList + "," + pIdList.get(counter);
                    }
                }
            }
            mcdbh.updateData("1", categorySelected,mcategoriesPhrasesList);
            phrasesIdList.remove(phraseId);
        }
        else
        {
            CategoriesDBH categoriesDBH = new CategoriesDBH(this);
            categoriesData = categoriesDBH.getAllData();
            for (categoriesData.moveToFirst(); !categoriesData.isAfterLast(); categoriesData.moveToNext())
            {
                if (categoriesData.getString(1).equals(categorySelected))
                {
                    String id = categoriesData.getString(0);
                    Integer parentID = Integer.parseInt(categoriesData.getString(3));
                    String categoriesPhrasesList = categoriesData.getString(4);
                    ArrayList<Integer> pIdList = new ArrayList<Integer>();
                    String[] phrasesID = categoriesPhrasesList.split(",");
                    for (int index = 0; index < phrasesID.length; index++)
                    {
                        pIdList.add(Integer.parseInt(phrasesID[index]));
                    }
                    pIdList.remove(phraseId);
                    if(pIdList.size() == 0)
                    {
                        categoriesPhrasesList = "";
                    }
                    else
                    {
                        categoriesPhrasesList = "";
                        for (int counter = 0; counter < pIdList.size(); counter++)
                        {
                            if (counter == 0) {
                                categoriesPhrasesList = String.valueOf(pIdList.get(counter));
                            } else {
                                categoriesPhrasesList = categoriesPhrasesList + "," +pIdList.get(counter);
                            }
                        }
                    }
                    cdbh.updateData(id, categorySelected, "Empty", parentID, categoriesPhrasesList);
                    phrasesIdList.remove(phraseId);
                    break;
                }
            }
        }
    }
    private void deletePhrase(Integer id)
    {
        affectedRows = affectedRows + pdbh.deleteData(String.valueOf(id));
    }

    private void reOpenDelete()
    {
        Intent intent = new Intent(this,Delete.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(passedValue.equals("Phrase"))
        {
            intent.putStringArrayListExtra("PhrasesIDList",phrasesIdList);
            intent.putExtra("CategorySelected",passedCategory);
        }
        intent.putExtra("Box",passedValue);
        startActivity(intent);
    }

    public void onDestroy()
    {
        Intent intent = new Intent(this,StartActivity.class);
        if(passedValue.equals("Phrase"))
        {
            intent.putExtra("PC",passedCategory);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onDestroy();
    }

}
