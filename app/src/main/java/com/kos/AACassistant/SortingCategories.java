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

public class SortingCategories extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<String> categoriesList;
    private CategoriesDBH categoriesDBH;
    private SettingsDBH settingsDBH;
    private Cursor categoriesData,settingsData;
    private Button btnReturnToSettings;
    private String speechText,categorySelected,activityFrom,sortedCategories;
    private LinearLayout scLayout;
    private TextView scTitle,txtScInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting_categories);

        //LAYOUT
        scLayout = findViewById(R.id.scLayout);
        scTitle = findViewById(R.id.scTitle);
        txtScInfo = findViewById(R.id.txtScInfo);

        //GET INTENTS
        activityFrom = getIntent().getStringExtra("From");
        categorySelected = getIntent().getStringExtra("PC");
        speechText = getIntent().getStringExtra("SpeechText");

        //DATA FROM DB
        categoriesDBH = new CategoriesDBH(this);
        categoriesData = categoriesDBH.getAllData();
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();

        //CHECK IF CATEGORIES ARE SORTED OR NOT AND SET THEM UP
        settingsData.moveToFirst();
        categoriesList = new ArrayList<>();
        String sortedList = settingsData.getString(5);
        if(!sortedList.equals("None"))
        {
            String[] categoriesID = sortedList.split(",");
            for(int index = 0;index < categoriesID.length;index++)
            {
                for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
                {
                    if(categoriesData.getString(0).equals(categoriesID[index]))
                    {
                        categoriesList.add(categoriesData.getString(1));
                    }
                }
            }
        }
        else
        {
            for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
            {
                categoriesList.add(categoriesData.getString(1));
            }
        }

        //SET UP AND DECLARE LIST FOR CATEGORIES DRAG AND DROP SORTING (+SPLIT DECORATION)
        recyclerView = findViewById(R.id.rvCategoriesSorting);
        recyclerAdapter = new RecyclerAdapter(categoriesList,Boolean.TRUE,
                speechText,categorySelected,activityFrom,"No");
        recyclerView.setAdapter(recyclerAdapter);

        //DRAG AND DROP TO RECYCLER VIEW
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //RETURN BTN
        btnReturnToSettings = findViewById(R.id.btnReturnToSettings);
        btnReturnToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    returnToMainSettings();
            }
        });

        checkColorBlindStatus();

    }

    private void checkColorBlindStatus()
    {
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            scLayout.setBackgroundResource(R.color.yellow);
            btnReturnToSettings.setBackgroundResource(R.drawable.colorblindbtn);
            scTitle.setTextColor(Color.BLACK);
            txtScInfo.setBackgroundResource(R.drawable.colorblindbtn);
            recyclerView.setBackgroundResource(R.drawable.selectedcolorblindbtn);
        }
        else
        {
            scLayout.setBackgroundResource(R.color.gray);
            btnReturnToSettings.setBackgroundResource(R.drawable.selectedbuttonshape);
            scTitle.setTextColor(Color.WHITE);
            txtScInfo.setBackgroundResource(R.color.light_grey);
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

            Collections.swap(categoriesList,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);

            sortedCategories = "";
            for(int index =0;index < categoriesList.size();index++)
            {
                for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
                {
                    if(categoriesList.get(index).equals(categoriesData.getString(1)))
                    {
                        if (sortedCategories.equals(""))
                        {
                            sortedCategories = categoriesData.getString(0);
                        }
                        else
                        {
                            sortedCategories = sortedCategories + "," + categoriesData.getString(0);
                        }
                    }
                }
            }
            updateSortedCategories(sortedCategories);
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {}
    };

    private void updateSortedCategories(String sc)
    {
        settingsData.moveToFirst();
        String id = settingsData.getString(0);
        float p = Integer.parseInt(settingsData.getString(1));
        float s = Integer.parseInt(settingsData.getString(2));
        String cb = settingsData.getString(3);
        String cm = settingsData.getString(4);
        settingsDBH.updateData(id,p,s,cb,cm,sc);
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
