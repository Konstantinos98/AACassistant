package com.kos.AACassistant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class SortingPhrases extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<String> phrasesList;
    private Button btnReturnToCategoriesSorting;
    private CategoriesDBH categoriesDBH;
    private PhrasesDBH phrasesDBH;
    private SettingsDBH settingsDBH;
    private Cursor categoriesData,phrasesData,settingsData;
    private String speechText,categorySelected,activityFrom,passedCategory,sortedPhrases;
    private LinearLayout spLayout;
    private TextView spTitle,txtSpInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting_phrases);

        //LAYOUT
        spLayout = findViewById(R.id.spLayout);
        spTitle = findViewById(R.id.spTitle);
        txtSpInfo = findViewById(R.id.txtSpInfo);


        //GET INTENTS
        passedCategory = getIntent().getStringExtra("CategoryName");
        activityFrom = getIntent().getStringExtra("From");
        categorySelected = getIntent().getStringExtra("PC");
        speechText = getIntent().getStringExtra("SpeechText");

        //DATA FROM DB
        settingsDBH = new SettingsDBH(this);
        categoriesDBH = new CategoriesDBH(this);
        categoriesData = categoriesDBH.getAllData();
        phrasesDBH = new PhrasesDBH(this);
        phrasesData = phrasesDBH.getAllData();

        //LIST FROM CATEGORY
        phrasesList = new ArrayList<>();
        for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
        {
            if(categoriesData.getString(1).equals(passedCategory))
            {
                if(categoriesData.getString(4).length() > 0)
                {
                    String[] phrasesIDList = categoriesData.getString(4).split(",");
                    for(int index=0;index<phrasesIDList.length;index++)
                    {
                        for(phrasesData.moveToFirst();!phrasesData.isAfterLast();phrasesData.moveToNext())
                        {
                            if(phrasesData.getString(0).equals(phrasesIDList[index]))
                            {
                                phrasesList.add(phrasesData.getString(2));
                            }
                        }
                    }
                }
            }
        }

        //SET UP AND DECLARE LIST FOR CATEGORIES DRAG AND DROP SORTING (+SPLIT DECORATION)
        recyclerView = findViewById(R.id.rvPhrasesSorting);
        recyclerAdapter = new RecyclerAdapter(phrasesList,Boolean.FALSE,
                speechText,categorySelected,activityFrom,"No");
        recyclerView.setAdapter(recyclerAdapter);

        //DRAG AND DROP TO RECYCLER VIEW
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //RETURN BTN
        btnReturnToCategoriesSorting = findViewById(R.id.btnReturnToCategoriesSorting);
        btnReturnToCategoriesSorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToCategoriesSorting();
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
            spLayout.setBackgroundResource(R.color.yellow);
            btnReturnToCategoriesSorting.setBackgroundResource(R.drawable.colorblindbtn);
            spTitle.setTextColor(Color.BLACK);
            txtSpInfo.setBackgroundResource(R.drawable.colorblindbtn);
            recyclerView.setBackgroundResource(R.drawable.selectedcolorblindbtn);
        }
        else
        {
            spLayout.setBackgroundResource(R.color.gray);
            btnReturnToCategoriesSorting.setBackgroundResource(R.drawable.selectedbuttonshape);
            spTitle.setTextColor(Color.WHITE);
            txtSpInfo.setBackgroundResource(R.color.light_grey);
            recyclerView.setBackgroundResource(R.color.white_smoke);
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            0)
    {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(phrasesList,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);

            sortedPhrases = "";
            for(int index =0;index < phrasesList.size();index++)
            {
                for(phrasesData.moveToFirst();!phrasesData.isAfterLast();phrasesData.moveToNext())
                {
                    if(phrasesData.getString(1).equals(phrasesList.get(index)))
                    {
                        if(sortedPhrases.equals(""))
                        {
                            sortedPhrases = phrasesData.getString(0);
                            break;
                        }
                        else
                        {
                            sortedPhrases = sortedPhrases + "," +phrasesData.getString(0);
                            break;
                        }
                    }
                }
            }
            updateSortedPhrases();
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {}
    };

    private void updateSortedPhrases()
    {
        for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
        {
            if(categoriesData.getString(1).equals(passedCategory))
            {
                String id = categoriesData.getString(0);
                String n = categoriesData.getString(1);
                String s = categoriesData.getString(2);
                Integer pid = Integer.parseInt(categoriesData.getString(3));
                categoriesDBH.updateData(id,n,s,pid,sortedPhrases);
            }
        }
    }

    public void returnToCategoriesSorting()
    {
        Intent intent = new Intent(this,SortingCategories.class);
        intent.putExtra("PC",categorySelected);
        intent.putExtra("SpeechText",speechText);
        intent.putExtra("From",activityFrom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
