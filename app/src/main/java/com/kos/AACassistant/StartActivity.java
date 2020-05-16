package com.kos.AACassistant;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.util.Log;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageButton;

import org.jetbrains.annotations.NotNull;

public class StartActivity extends AppCompatActivity {

    //VARIABLES DECLARATIONS//
    //COLOR BLIND VARIABLE
    private String colorBlind;
    private String sortedCategories;
    //SELECTED CATEGORY
    private String categorySelected;
    private String passedTextFromFCV;
    //DATABASE ACCESS
    private SettingsDBH settingsDBH;
    private MainCategoriesDBH mainCategoriesDBH;
    private CategoriesDBH categoriesDBH;
    private PhrasesDBH phrasesDBH;
    private Cursor settingsData,mainCategoriesData,categoriesData,phrasesData;
    //CONTROL BUTTONS
    private Button btnSpeak,btnClear,btnFullScreen,btnSettings,btnTalkSettings;
    private Button btnAddCategory,btnDeleteCategory,btnEditCategory;
    private Button btnAddCurrentPhrase,btnAddPhrase,btnDeletePhrase,btnEditPhrase;
    private Button btnFavourites,btnHistory,btnDeleteHistory;
    private ImageButton btnLeftScroll,btnRightScroll,btnUpScroll,btnDownScroll;
    //LISTS
    private ArrayList<String> phrasesIdList;
    private ArrayList<Button> categoriesListBtn,phrasesListBtn,subCategoriesListBtn;
    //INPUT/OUTPUT - LAYOUTS
    private LinearLayout mainLayout,categoriesBox,categoriesLayout,subCategoriesRow,phrasesBox;
    private HorizontalScrollView hsvCategoriesBox;
    private EditText speechBar;
    private TextToSpeech dTTS;
    private GridView phrasesLayout;
    private HorizontalScrollView subCategoriesLayout;

    //CREATION FUNCTION
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //SETTING UP DATABASE HELPERS
        settingsDBH = new SettingsDBH(this);
        mainCategoriesDBH = new MainCategoriesDBH(this);
        categoriesDBH = new CategoriesDBH(this);
        phrasesDBH = new PhrasesDBH(this);

        // MAIN+CATEGORIES+PHRASES LAYOUT SET UP
        mainLayout = findViewById(R.id.mainLayout);
        categoriesBox = findViewById(R.id.categoriesBox);
        hsvCategoriesBox = findViewById(R.id.hsvCategoriesBox);
        phrasesBox = findViewById(R.id.phrasesBox);
        categoriesLayout = findViewById(R.id.categoriesLayout);
        phrasesLayout = findViewById(R.id.phrasesLayout);
        subCategoriesLayout = findViewById(R.id.subCategoriesLayout);
        subCategoriesRow = findViewById(R.id.subCategoriesRow);
        categoriesListBtn = new ArrayList<Button>();
        phrasesListBtn = new ArrayList<Button>();
        phrasesIdList = new ArrayList<String>();
        subCategoriesListBtn = new ArrayList<Button>();

        //CONTROL BOX SET UP
        btnSpeak = findViewById(R.id.btnSpeak);
        btnClear = findViewById(R.id.btnClear);
        btnFullScreen = findViewById(R.id.btnFullScreen);
        btnSettings = findViewById(R.id.btnMainSettings);
        speechBar = findViewById(R.id.speechBar);
        btnTalkSettings = findViewById(R.id.btnTalkSettings);
        btnAddCurrentPhrase = findViewById(R.id.btnAddCurrentPhrase);
        passedTextFromFCV = getIntent().getStringExtra("SpeechText");
        speechBar.setText(passedTextFromFCV);

        //CATEGORIES BOX SET UP
        btnFavourites = findViewById(R.id.btnFavourites);
        btnHistory = findViewById(R.id.btnHistory);
        btnLeftScroll = findViewById(R.id.btnLeftScroll);
        btnRightScroll = findViewById(R.id.btnRightScroll);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory);
        btnEditCategory = findViewById(R.id.btnEditCategory);

        //PHRASES BOX SET UP
        btnAddPhrase = findViewById(R.id.btnAddPhrase);
        btnDeletePhrase = findViewById(R.id.btnDeletePhrase);
        btnEditPhrase = findViewById(R.id.btnEditPhrase);
        btnUpScroll = findViewById(R.id.btnUpScroll);
        btnDownScroll = findViewById(R.id.btnDownScroll);
        btnDeleteHistory = findViewById(R.id.btnDeleteHistory);

        //DECLARATION OF TEXT TO SPEECH VARIABLE (dTTS)
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
                      btnSpeak.setEnabled(true);
                    }
                }else
                {
                    Log.e("TTS","Initialization failed");
                }
            }
        });
        dTTS.setLanguage(Locale.ENGLISH);

        //CONTROL BOX SET UP
        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFullScreen();
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainSettings();
            }
        });
        btnTalkSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTalkSettings();
            }
        });
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    speak();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        btnAddCurrentPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openAddCurrentPhraseToCategory();
            }
        });

        //SET FAVOURITES AND HISTORY
        btnFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFavourites();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistory();
            }
        });
        btnDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    deleteHistory();
            }
        });

        //SET SCROLL BUTTON
        btnRightScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollRight();
            }
        });
        btnLeftScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollLeft();
            }
        });
        btnUpScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUp();
            }
        });
        btnDownScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    scrollDown();
            }
        });

        //SET ADD PHRASE - CATEGORY FUNCTION
        btnAddPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openAdd("Phrase");
            }
        });
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdd("Category");
            }
        });

        //SET DELETE PHRASE - CATEGORY FUNCTION
        btnDeletePhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDelete("Phrase");
            }
        });
        btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDelete("Category");
            }
        });

        //SET EDIT PHRASE/CATEGORY FUNCTION
        btnEditPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        openEdit("Phrase");
            }
        });
        btnEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEdit("Category");
            }
        });

        //CHECK DATABASES DATA
        checkSettingsDb();
        checkMainCategoriesDB();
        checkCategoriesDB();
        checkPhrasesDB();

        //SETTING UP INITIAL OR PASSED CATEGORY AND PHRASES
        checkAndSetCategory();
        //CHECK MAIN SETTINGS STATUS
        checkForColorBlindStatus();
    }

    //CHECK FOR PASSED CATEGORY
    private void checkAndSetCategory()
    {
        if(getIntent().hasExtra("PC"))
        {
            String checkPassedValue = getIntent().getStringExtra("PC");
            categorySelected = checkPassedValue;
            if(categorySelected.equals("Favourites"))
            {
                openFavourites();
            }
            else if (categorySelected.equals("History"))
            {
                openHistory();
            }
            else
            {
                checkCategorySelected();
            }
        }
        else
        {
            openFavourites();
        }
        setCategoriesBtnFunction();
    }

    //MAIN SETTINGS STATUS
    private void checkForColorBlindStatus()
    {
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            btnSpeak.setBackgroundResource(R.drawable.colorblindbtn);
            btnClear.setBackgroundResource(R.drawable.colorblindbtn);
            btnFullScreen.setBackgroundResource(R.drawable.colorblindbtn);
            btnSettings.setBackgroundResource(R.drawable.colorblindbtn);
            speechBar.setBackgroundResource(R.drawable.colorblindbtn);
            btnTalkSettings.setBackgroundResource(R.drawable.colorblindbtn);
            btnAddCurrentPhrase.setBackgroundResource(R.drawable.colorblindbtn);
            mainLayout.setBackgroundResource(R.color.yellow);
            categoriesBox.setBackgroundResource(R.color.yellow);
            btnFavourites.setBackgroundResource(R.drawable.colorblindbtn);
            btnHistory.setBackgroundResource(R.drawable.colorblindbtn);
            btnAddCategory.setBackgroundResource(R.drawable.colorblindbtn);
            btnDeleteCategory.setBackgroundResource(R.drawable.colorblindbtn);
            btnEditCategory.setBackgroundResource(R.drawable.colorblindbtn);
            btnLeftScroll.setBackgroundResource(R.drawable.colorblindbtn);
            btnRightScroll.setBackgroundResource(R.drawable.colorblindbtn);
            hsvCategoriesBox.setBackgroundResource(R.drawable.colorblindbtn);
            subCategoriesLayout.setBackgroundResource(R.drawable.colorblindbtn);
            btnAddPhrase.setBackgroundResource(R.drawable.colorblindbtn);
            btnDeletePhrase.setBackgroundResource(R.drawable.colorblindbtn);
            btnEditPhrase.setBackgroundResource(R.drawable.colorblindbtn);
            btnUpScroll.setBackgroundResource(R.drawable.colorblindbtn);
            btnDownScroll.setBackgroundResource(R.drawable.colorblindbtn);
            btnDeleteHistory.setBackgroundResource(R.drawable.colorblindbtn);
            phrasesBox.setBackgroundResource(R.color.yellow);
            phrasesLayout.setBackgroundResource(R.drawable.colorblindbtn);
            if(categorySelected.equals("Favourites"))
            {
                btnFavourites.setBackgroundResource(R.drawable.selectedcolorblindbtn);
                btnHistory.setBackgroundResource(R.drawable.colorblindbtn);
            }
            if(categorySelected.equals("History"))
            {
                btnHistory.setBackgroundResource(R.drawable.selectedcolorblindbtn);
                btnFavourites.setBackgroundResource(R.drawable.colorblindbtn);
            }
            for(int index = 0;index <categoriesListBtn.size();index++)
            {
                if(categoriesListBtn.get(index).getText().equals(categorySelected))
                {
                    categoriesListBtn.get(index).setBackgroundResource(R.drawable.selectedcolorblindbtn);
                    btnHistory.setBackgroundResource(R.drawable.colorblindbtn);
                    btnFavourites.setBackgroundResource(R.drawable.colorblindbtn);
                }else
                {
                    categoriesListBtn.get(index).setBackgroundResource(R.drawable.colorblindbtn);
                }
            }
            for(int index = 0;index <phrasesListBtn.size();index++)
            {
                phrasesListBtn.get(index).setBackgroundResource(R.drawable.phrasescolorblindbtn);
            }
            for(int index =0;index < subCategoriesListBtn.size();index++)
            {
                subCategoriesListBtn.get(index).setBackgroundResource(R.drawable.colorblindbtn);
            }
        }
        else
        {
            btnSpeak.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnClear.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnFullScreen.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnSettings.setBackgroundResource(R.drawable.selectedbuttonshape);
            speechBar.setBackgroundResource(R.drawable.phrasesbox);
            btnTalkSettings.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnAddCurrentPhrase.setBackgroundResource(R.drawable.selectedbuttonshape);
            mainLayout.setBackgroundResource(R.color.white);
            categoriesBox.setBackgroundResource(R.color.light_grey);
            btnFavourites.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnHistory.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnAddCategory.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnDeleteCategory.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnEditCategory.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnLeftScroll.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnRightScroll.setBackgroundResource(R.drawable.selectedbuttonshape);
            hsvCategoriesBox.setBackgroundResource(R.drawable.buttonshape);
            subCategoriesLayout.setBackgroundResource(R.drawable.buttonshape);
            btnAddPhrase.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnDeletePhrase.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnEditPhrase.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnUpScroll.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnDownScroll.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnDeleteHistory.setBackgroundResource(R.drawable.selectedbuttonshape);
            phrasesBox.setBackgroundResource(R.color.light_grey);
            phrasesLayout.setBackgroundResource(R.drawable.phrasesgridbox);
            if(categorySelected.equals("Favourites"))
            {
                btnFavourites.setBackgroundResource(R.drawable.selectedbuttonshape);
                btnHistory.setBackgroundResource(R.drawable.buttonshape);
            }
            if(categorySelected.equals("History"))
            {
                btnHistory.setBackgroundResource(R.drawable.selectedbuttonshape);
                btnFavourites.setBackgroundResource(R.drawable.buttonshape);
            }
            for(int index = 0;index <categoriesListBtn.size();index++)
            {
                if(categoriesListBtn.get(index).getText().equals(categorySelected))
                {
                    categoriesListBtn.get(index).setBackgroundResource(R.drawable.selectedbuttonshape);
                    btnFavourites.setBackgroundResource(R.drawable.buttonshape);
                    btnHistory.setBackgroundResource(R.drawable.buttonshape);
                }else
                {
                    categoriesListBtn.get(index).setBackgroundResource(R.drawable.buttonshape);
                }
            }
            for(int index = 0;index <phrasesListBtn.size();index++)
            {
                phrasesListBtn.get(index).setBackgroundResource(R.drawable.selectedbuttonshape);
            }
            for(int index =0;index < subCategoriesListBtn.size();index++)
            {
                subCategoriesListBtn.get(index).setBackgroundResource(R.drawable.buttonshape);
            }
        }
    }
    private boolean checkForCustomSorting()
    {
        categoriesData = categoriesDBH.getAllData();
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        sortedCategories = settingsData.getString(5);
        if(sortedCategories.equals("None"))
        {
            return false;
        }
        else
        {
            ArrayList<String> counterID = new ArrayList<>();
            for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
            {
                counterID.add(categoriesData.getString(0));
            }
            String[] counterSC = sortedCategories.split(",");
            if(counterID.size() == counterSC.length)
            {
                return true;
            }
            else
            {
                if(counterID.size() > counterSC.length)
                {
                    for(int index =0; index < counterID.size(); index++)
                    {
                        if(index >= counterSC.length)
                        {
                            sortedCategories = sortedCategories + "," + counterID.get(index);
                        }
                    }
                    updateSortedCategories();
                    return true;
                }
                if(counterID.size() < counterSC.length)
                {
                    for(int index = 0;index < counterSC.length; index++)
                    {
                        Boolean found = false;
                        for(int count = 0;count < counterID.size();count++)
                        {
                            if(counterSC[index].equals(counterID.get(count)))
                            {
                                found = true;
                            }
                        }
                        if(found == false)
                        {
                            if(index == 0)
                            {
                                sortedCategories.replaceFirst(counterSC[index] + ",","");
                            }
                            else
                            {
                                sortedCategories.replaceFirst("," + counterSC[index],"");
                            }
                        }
                    }
                    updateSortedCategories();
                    return true;
                }
            }
        }
        return false;
    }
    private void updateSortedCategories()
    {
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        String id = settingsData.getString(0);
        float p = Integer.parseInt(settingsData.getString(1));
        float s = Integer.parseInt(settingsData.getString(2));
        String cb = settingsData.getString(3);
        String cm = settingsData.getString(4);
        settingsDBH.updateData(id,p,s,cb,cm,sortedCategories);
    }

    //CONTROL BOX FUNCTIONS
    private void speak()
    {
        //SET UP SETTING DATA FOR THE VOICE
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        float pitch = (float)Integer.parseInt(settingsData.getString(1)) / 50;
        if(pitch < 0.1) pitch = 0.1f;
        float speed = (float)Integer.parseInt(settingsData.getString(2)) / 50;
        if(speed < 0.1) speed = 0.1f;
        dTTS.setPitch(pitch);
        dTTS.setSpeechRate(speed);
        String textOfSb = speechBar.getText().toString();
        //SPEAK FUNCTION
        dTTS.speak(textOfSb, TextToSpeech.QUEUE_FLUSH, null);
    }
    private void clear()
    {
        //EMPTY THE SPEECH BAR TEXT
        speechBar.setText("");
    }
    private void setFullScreen()
    {
        Intent intent = new Intent(this,FullScreenView.class);
        intent.putExtra("PC",categorySelected);
        intent.putExtra("SpeechText",speechBar.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openMainSettings()
    {
        Intent intent = new Intent(this,MainSettings.class);
        intent.putExtra("From","NormalScreen");
        intent.putExtra("PC",categorySelected);
        intent.putExtra("SpeechText",speechBar.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void openTalkSettings()
    {
        Intent intent = new Intent(this, PhraseSettings.class);
        startActivity(intent);
    }
    private void openAddCurrentPhraseToCategory()
    {
        String currentPhrase = speechBar.getText().toString();
        if(!currentPhrase.isEmpty())
        {
            Intent intent = new Intent(this,AddCurrentPhraseToCategory.class);
            intent.putExtra("CurrentPhrase",currentPhrase);
            intent.putExtra("From","NormalScreen");
            startActivity(intent);
        }
        else 
        {
            Toast.makeText(this, "Empty speechbar text.", Toast.LENGTH_SHORT).show();
        }
    }

    //DATABASE INITIALISATION FUNCTIONS
    private void checkSettingsDb()
    {
        settingsData = settingsDBH.getAllData();
        if(settingsData.getCount() == 0)
        {
            addInitialDataToSDB();
        }
    }
    private void addInitialDataToSDB()
    {
        settingsDBH.insertData(1,50,50,"False","False","None");
        settingsData = settingsDBH.getAllData();
    }
    private void checkMainCategoriesDB()
    {
        mainCategoriesData = mainCategoriesDBH.getAllData();
        if(mainCategoriesData.getCount() == 0)
        {
            addInitialDataToMCDB();
        }
    }
    private void addInitialDataToMCDB()
    {
        mainCategoriesDBH.insertData(1,"Favourites","");
        mainCategoriesDBH.insertData(2,"History","");
        mainCategoriesData = mainCategoriesDBH.getAllData();
    }
    private void checkCategoriesDB()
    {
        categoriesData = categoriesDBH.getAllData();
        if(categoriesData.getCount() == 0)
        {
            addInitialDataToCDB();
        }
        if(checkForCustomSorting() == true)
        {
            settingsData = settingsDBH.getAllData();
            settingsData.moveToFirst();
            sortedCategories = settingsData.getString(5);
            String[] categoriesID = sortedCategories.split(",");
            for(int index = 0;index < categoriesID.length;index++)
            {
                for(categoriesData.moveToFirst();!categoriesData.isAfterLast();categoriesData.moveToNext())
                {
                    if(categoriesData.getString(0).equals(categoriesID[index]))
                    {
                        Button btn = getCategoryButton(Integer.parseInt(categoriesData.getString(0)),categoriesData.getString(1));
                        categoriesListBtn.add(btn);
                        categoriesLayout.addView(btn);
                    }
                }
            }
        }
        else
        {
            while (categoriesData.moveToNext())
            {
                Button btn = getCategoryButton(Integer.parseInt(categoriesData.getString(0)),categoriesData.getString(1));
                categoriesListBtn.add(btn);
                categoriesLayout.addView(btn);
            }
        }
    }
    private void addInitialDataToCDB()
    {
        categoriesDBH.insertData(1,"Common","Empty",0,"0,1,2,3,4,5,6,7,8,9");
        categoriesDBH.insertData(2,"Objects","Empty",0,"10,11,12,13,14,15,16,17,18,19");
        categoriesDBH.insertData(3,"Sports","Empty",0,"20,21,22,23,24,25,26,27,28,29");
        categoriesDBH.insertData(4,"Family","Empty",0,"30,31,32,33,34,35,36,37,38,39");
        categoriesDBH.insertData(5,"Food","Empty",0,"40,41,42,43,44,45,46,47,48,49");
        categoriesDBH.insertData(6,"Feelings","Empty",0,"50,51,52,53,54,55,56,57,58,59");
        categoriesDBH.insertData(7,"Health","Empty",0,"60,61,62,63,64,65,66,67,68,69");
        categoriesDBH.insertData(8,"Apps","Empty",0,"70,71,72,73,74,75,76,77,78,79,80");
        categoriesData = categoriesDBH.getAllData();
    }
    private void checkPhrasesDB()
    {
        phrasesData = phrasesDBH.getAllData();
        if(phrasesData.getCount() == 0)
        {
            addInitialDataToPDB();
        }
    }
    private void addInitialDataToPDB()
    {
        phrasesDBH.insertData(0,"Hello","Hello","Empty");
        phrasesDBH.insertData(1,"My name is","My name is","Empty");
        phrasesDBH.insertData(2,"What’s up?","What’s up?","Empty");
        phrasesDBH.insertData(3,"I’m fine, thanks.","I’m fine, thanks.","Empty");
        phrasesDBH.insertData(4,"I really appreciate it.","I really appreciate it.","Empty");
        phrasesDBH.insertData(5,"No problem.","No problem.","Empty");
        phrasesDBH.insertData(6,"It was nice ..","It was nice chatting with you.","Empty");
        phrasesDBH.insertData(7,"I have no idea","I have no idea","Empty");
        phrasesDBH.insertData(8,"Exactly.","Exactly.","Empty");
        phrasesDBH.insertData(9,"Not necessarily","Not necessarily","Empty");
        phrasesDBH.insertData(10,"Chair","Chair","Empty");
        phrasesDBH.insertData(11,"Television set","Television set","Empty");
        phrasesDBH.insertData(12,"Desk","Desk","Empty");
        phrasesDBH.insertData(13,"Floor","Floor","Empty");
        phrasesDBH.insertData(14,"Table","Table","Empty");
        phrasesDBH.insertData(15,"Door","Door","Empty");
        phrasesDBH.insertData(16,"Keys","Keys","Empty");
        phrasesDBH.insertData(17,"Flashlight","Flashlight","Empty");
        phrasesDBH.insertData(18,"Remote Control","Remote Control","Empty");
        phrasesDBH.insertData(19,"Fork","Fork","Empty");
        phrasesDBH.insertData(20,"This is a must win","This is a must win","Empty");
        phrasesDBH.insertData(21,"Half time","Half time","Empty");
        phrasesDBH.insertData(22,"Extra time","Extra time","Empty");
        phrasesDBH.insertData(23,"Its a draw","Its a draw","Empty");
        phrasesDBH.insertData(24,"Penalty kick","Penalty kick","Empty");
        phrasesDBH.insertData(25,"Goal!","Goal!","Empty");
        phrasesDBH.insertData(26,"The field","The field","Empty");
        phrasesDBH.insertData(27,"The game","The game","Empty");
        phrasesDBH.insertData(28,"The players","The players","Empty");
        phrasesDBH.insertData(29,"Its a penalty!","Its a penalty!","Empty");
        phrasesDBH.insertData(30,"Mother","Mother","Empty");
        phrasesDBH.insertData(31,"Dad","Dad","Empty");
        phrasesDBH.insertData(32,"Brother","Brother","Empty");
        phrasesDBH.insertData(33,"Sister","Sister","Empty");
        phrasesDBH.insertData(34,"Uncle","Uncle","Empty");
        phrasesDBH.insertData(35,"Nephew","Nephew","Empty");
        phrasesDBH.insertData(36,"Son","Son","Empty");
        phrasesDBH.insertData(37,"Daughter","Daughter","Empty");
        phrasesDBH.insertData(38,"Grandparents","Grandparents","Empty");
        phrasesDBH.insertData(39,"Parents","Parents","Empty");
        phrasesDBH.insertData(40,"Breakfast","Breakfast","Empty");
        phrasesDBH.insertData(41,"Lunch","Lunch","Empty");
        phrasesDBH.insertData(42,"Dinner","Dinner","Empty");
        phrasesDBH.insertData(43,"I am hungry!","I am hungry!","Empty");
        phrasesDBH.insertData(44,"The food was excellent","The food was excellent","Empty");
        phrasesDBH.insertData(45,"What do you fancy for dinner?","What do you fancy for dinner?","Empty");
        phrasesDBH.insertData(46,"I am starving!","I am starving!","Empty");
        phrasesDBH.insertData(47,"Can I have some","Can I have some","Empty");
        phrasesDBH.insertData(48,"Fast Food","Fast Food","Empty");
        phrasesDBH.insertData(49,"Lets go to a restaurant","Lets go to a restaurant","Empty");
        phrasesDBH.insertData(50,"I am happy","I am happy","Empty");
        phrasesDBH.insertData(51,"I am sad","I am sad","Empty");
        phrasesDBH.insertData(52,"I don't feel well","I don't feel well","Empty");
        phrasesDBH.insertData(53,"I am angry","I am angry","Empty");
        phrasesDBH.insertData(54,"I am relaxed","I am relaxed","Empty");
        phrasesDBH.insertData(55,"That's funny","That's funny","Empty");
        phrasesDBH.insertData(56,"I am tired","I am tired","Empty");
        phrasesDBH.insertData(57,"I am thirsty","I am thirsty","Empty");
        phrasesDBH.insertData(58,"I am afraid","I am afraid","Empty");
        phrasesDBH.insertData(59,"I am bored","I am bored","Empty");
        phrasesDBH.insertData(60,"I need my medicine","I need my medicine","Empty");
        phrasesDBH.insertData(61,"I feel pain in","I feel pain in","Empty");
        phrasesDBH.insertData(62,"I am sick","I am sick","Empty");
        phrasesDBH.insertData(63,"Call a doctor","Call a doctor","Empty");
        phrasesDBH.insertData(64,"I need to go to hospital","I need to go to hospital","Empty");
        phrasesDBH.insertData(65,"Can you help me?","Can you help me?","Empty");
        phrasesDBH.insertData(66,"I need to sleep","I need to sleep","Empty");
        phrasesDBH.insertData(67,"I need to toilet","I need to toilet","Empty");
        phrasesDBH.insertData(68,"I am allergic to","I am allergic to","Empty");
        phrasesDBH.insertData(69,"I need a drink","I need a drink","Empty");
        phrasesDBH.insertData(70,"Email","Email","Empty");
        phrasesDBH.insertData(71,"Messenger","Messenger","Empty");
        phrasesDBH.insertData(72,"Add me on","Add me on","Empty");
        phrasesDBH.insertData(73,"Follow me on","Follow me on","Empty");
        phrasesDBH.insertData(74,"Like my","Like my","Empty");
        phrasesDBH.insertData(75,"Download","Download","Empty");
        phrasesDBH.insertData(76,"Instagram","Instagram","Empty");
        phrasesDBH.insertData(78,"Facebook","Facebook","Empty");
        phrasesDBH.insertData(79,"TrainLine","TrainLine","Empty");
        phrasesDBH.insertData(80,"Netflix","Netflix","Empty");
    }

    // INITIAL CATEGORY // CATEGORIES + PHRASES SET UP // CATEGORY CHANGE FUNCTION
    private void checkCategorySelected()
    {
        categoriesData = categoriesDBH.getAllData();
        btnHistory.setBackgroundResource(R.drawable.buttonshape);
        btnFavourites.setBackgroundResource(R.drawable.buttonshape);
        btnAddPhrase.setVisibility(View.VISIBLE);
        btnDeletePhrase.setVisibility(View.VISIBLE);
        btnEditPhrase.setVisibility(View.VISIBLE);
        btnDeleteHistory.setVisibility(View.GONE);
        for(int clearSelectionBackground = 0; clearSelectionBackground < categoriesListBtn.size(); clearSelectionBackground++)
        {
            categoriesListBtn.get(clearSelectionBackground).setBackgroundResource(R.drawable.buttonshape);
        }
        for (int index = 0 ; index < categoriesListBtn.size(); index++)
        {
            if (categoriesListBtn.get(index).getText().equals(categorySelected))
            {
                for (categoriesData.moveToFirst(); !categoriesData.isAfterLast(); categoriesData.moveToNext())
                {
                    if(categoriesData.getString(1).equals(categorySelected))
                    {
                        categoriesListBtn.get(index).setBackgroundResource(R.drawable.selectedbuttonshape);
                        showSubCategories(String.valueOf(categoriesListBtn.get(index).getId()));
                        categorySelected(categoriesData.getString(4));
                    }
                }
            }
        }
        checkForColorBlindStatus();
    }
    private void showSubCategories(String selectedCategoryID)
    {
        subCategoriesRow.removeAllViews();
        subCategoriesListBtn.clear();
        ArrayList<String> subCategoriesIDList = new ArrayList<String>();
        Cursor categories = categoriesDBH.getAllData();
        //COMPARE ID WITH PARENT ID TO FIND SUB CATEGORIES
        for(categories.moveToFirst();!categories.isAfterLast();categories.moveToNext())
        {
            if(categories.getString(3).equals(selectedCategoryID))
            {
                subCategoriesIDList.add(categories.getString(0));
            }
        }
        //DISPLAY SUB CATEGORIES
        if(subCategoriesIDList.size()>0)
        {
            subCategoriesLayout.setVisibility(View.VISIBLE);
            //ADD THEM INTO THE SUB CATEGORY ROW AND SET UP FUNCTIONS TO THEM
            for(int index =0;index < subCategoriesIDList.size();index++)
            {
                for(categories.moveToFirst();!categories.isAfterLast();categories.moveToNext())
                {
                    if(categories.getString(0).equals(subCategoriesIDList.get(index)))
                    {
                        Button btn = getCategoryButton(Integer.parseInt(categories.getString(0)),categories.getString(1));
                        btn.setBackgroundResource(R.drawable.buttonshape);
                        final String categoryName = categories.getString(1);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setSubCategoryButtonFunction(categoryName);
                            }
                        });
                        subCategoriesListBtn.add(btn);
                        subCategoriesRow.addView(btn);
                    }
                }
            }
        }
        else
        {
            subCategoriesLayout.setVisibility(View.GONE);
        }
        checkForColorBlindStatus();
    }
    private void setSubCategoryButtonFunction(String passedCategoryName)
    {
        changeCategory(passedCategoryName);
    }
    private void categorySelected(@NotNull String phrasesListStr)
    {
        if(phrasesListStr.isEmpty())
        {
            phrasesListBtn.clear();
            phrasesIdList.clear();
            GridViewAdapter gridViewAdapter = new GridViewAdapter(phrasesListBtn);
            phrasesLayout.setAdapter(gridViewAdapter);
            Toast.makeText(this, "This category does not have any phrases.", Toast.LENGTH_LONG).show();
        }else
        {
            ArrayList<Integer> phrasesIdList = new ArrayList<Integer>();
            String[] phrasesID = phrasesListStr.split(",");
            for(int counter=0; counter < phrasesID.length; counter++)
            {
                phrasesIdList.add(Integer.parseInt(phrasesID[counter]));
            }
            setPhrases(phrasesIdList);
        }
    }
    private void setPhrases(@NotNull List<Integer> pl)
    {
        //VARIABLES
        phrasesListBtn.clear();
        phrasesIdList.clear();
        int index = 0;
        while(index < pl.size())
        {
            String check = String.valueOf(pl.get(index));
            for (phrasesData.moveToFirst(); !phrasesData.isAfterLast(); phrasesData.moveToNext())
            {
                if (check.equals(phrasesData.getString(0)))
                {
                    phrasesIdList.add(phrasesData.getString(0));
                    Button btn = getPhraseButton(Integer.parseInt(phrasesData.getString(0)),phrasesData.getString(2));
                    phrasesListBtn.add(btn);
                }
            }
            index = index + 1;
        }
        GridViewAdapter gridViewAdapter = new GridViewAdapter(phrasesListBtn);
        phrasesLayout.setAdapter(gridViewAdapter);
        setPhrasesBtnFunction();
    }
    private void setPhrasesBtnFunction()
    {
        //SET UP PHRASES BUTTON FUNCTION
        String phrase = "";
        for(int phCounter = 0; phCounter < phrasesListBtn.size(); phCounter++)
        {
            int currentPhraseId = phrasesListBtn.get(phCounter).getId();
            for(phrasesData.moveToFirst(); !phrasesData.isAfterLast(); phrasesData.moveToNext())
            {
                if(currentPhraseId == Integer.parseInt(phrasesData.getString(0)))
                {
                    phrase = phrasesData.getString(1);
                    break;
                }
            }
            final String phraseID = phrasesData.getString(0);
            final String passedPhrase = phrase;
            phrasesListBtn.get(phCounter).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    setTextToSpeechBar(passedPhrase,phraseID);
                }
            });
        }
    }
    private void setCategoriesBtnFunction()
    {
        for(int cgCounter = 0; cgCounter < categoriesListBtn.size(); cgCounter++)
        {
            final String ctgName = categoriesListBtn.get(cgCounter).getText().toString();
            categoriesListBtn.get(cgCounter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    changeCategory(ctgName);
                }
            });
        }
    }
    private void changeCategory(String ctgName)
    {
        btnEditCategory.setVisibility(View.VISIBLE);
        categorySelected = ctgName;
        checkCategorySelected();
    }
    private void setTextToSpeechBar(String passedPhrase,String id)
    {
        settingsData = settingsDBH.getAllData();
        settingsData.moveToFirst();
        if(settingsData.getString(4).equals("False"))
        {
            speechBar.setText(speechBar.getText() +  " " + passedPhrase);
            addedToHistory(id);
        }
        else
        {
            ClipboardManager copy = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("CopyText",passedPhrase);
            copy.setPrimaryClip(clip);
            Toast.makeText(this, "Phrase Copied", Toast.LENGTH_SHORT).show();
        }
    }

    //FAVOURITES AND HISTORY
    private void openFavourites()
    {
        subCategoriesLayout.setVisibility(View.GONE);
        for(int clearSelectionBackground = 0; clearSelectionBackground < categoriesListBtn.size(); clearSelectionBackground++)
        {
            categoriesListBtn.get(clearSelectionBackground).setBackgroundResource(R.drawable.buttonshape);
        }
        btnHistory.setBackgroundResource(R.drawable.buttonshape);
        btnFavourites.setBackgroundResource(R.drawable.selectedbuttonshape);
        btnEditCategory.setVisibility(View.INVISIBLE);
        btnDeleteHistory.setVisibility(View.GONE);
        btnAddPhrase.setVisibility(View.VISIBLE);
        btnDeletePhrase.setVisibility(View.VISIBLE);
        btnEditPhrase.setVisibility(View.VISIBLE);
        phrasesListBtn.clear();
        phrasesIdList.clear();
        categorySelected = "Favourites";
        mainCategoriesData.moveToFirst();
        String mainCategoriesPhrasesList = mainCategoriesData.getString(2);
        if(!mainCategoriesPhrasesList.isEmpty())
        {
            ArrayList<Integer> pIdList = new ArrayList<Integer>();
            String[] phrasesID = mainCategoriesPhrasesList.split(",");
            for(int counter=0; counter < phrasesID.length; counter++)
            {
                pIdList.add(Integer.parseInt(phrasesID[counter]));
            }
            setPhrases(pIdList);
        }
        else
        {
            GridViewAdapter gridViewAdapter = new GridViewAdapter(phrasesListBtn);
            phrasesLayout.setAdapter(gridViewAdapter);
            Toast.makeText(this, "Empty Favourites phrases list", Toast.LENGTH_SHORT).show();
        }
        checkForColorBlindStatus();
    }
    private void openHistory()
    {
        subCategoriesLayout.setVisibility(View.GONE);
        for(int clearSelectionBackground = 0; clearSelectionBackground < categoriesListBtn.size(); clearSelectionBackground++)
        {
            categoriesListBtn.get(clearSelectionBackground).setBackgroundResource(R.drawable.buttonshape);
        }
        btnFavourites.setBackgroundResource(R.drawable.buttonshape);
        btnHistory.setBackgroundResource(R.drawable.selectedbuttonshape);
        btnAddPhrase.setVisibility(View.GONE);
        btnDeletePhrase.setVisibility(View.GONE);
        btnEditPhrase.setVisibility(View.GONE);
        btnEditCategory.setVisibility(View.INVISIBLE);
        btnDeleteHistory.setVisibility(View.VISIBLE);
        phrasesListBtn.clear();
        phrasesIdList.clear();
        categorySelected = "History";
        mainCategoriesData = mainCategoriesDBH.getAllData();
        mainCategoriesData.moveToLast();
        String mainCategoriesPhrasesList = mainCategoriesData.getString(2);
        if(!mainCategoriesPhrasesList.isEmpty())
        {
            ArrayList<Integer> pIdList = new ArrayList<Integer>();
            String[] phrasesID = mainCategoriesPhrasesList.split(",");
            for(int counter=0; counter < phrasesID.length; counter++)
            {
                pIdList.add(Integer.parseInt(phrasesID[counter]));
            }
            setPhrases(pIdList);
        }
        else
        {
            GridViewAdapter gridViewAdapter = new GridViewAdapter(phrasesListBtn);
            phrasesLayout.setAdapter(gridViewAdapter);
            Toast.makeText(this, "Empty History phrases list", Toast.LENGTH_SHORT).show();
        }
        checkForColorBlindStatus();
    }
    private void addedToHistory(String phrase)
    {
        mainCategoriesData = mainCategoriesDBH.getAllData();
        mainCategoriesData.moveToLast();
        String mCategoryList = mainCategoriesData.getString(2);
        if(mCategoryList.equals(""))
        {
            mCategoryList = phrase;
        }
        else
        {
            String temp = mCategoryList;
            mCategoryList = phrase + "," + temp;
        }
        mainCategoriesDBH.updateData("2","History",mCategoryList);
    }
    private void deleteHistory()
    {
        mainCategoriesDBH.updateData("2","History","");
        openHistory();
    }

    //SCROLL BUTTONS
    private void scrollLeft()
    {
        categoriesLayout.scrollBy(-70,0);
    }
    private void scrollRight()
    {
        categoriesLayout.scrollBy(70,0);
    }
    private void scrollUp()
    {
        phrasesLayout.scrollBy(0,-70);
    }
    private void scrollDown()
    {
        phrasesLayout.scrollBy(0,70);
    }

    //CATEGORY AND PHRASES BUTTONS FUNCTIONS
    private void openAdd(String box)
    {
        Intent intent = new Intent(this,Add.class);
        intent.putExtra("Box",box);
        intent.putExtra("CategoryName",categorySelected);
        startActivity(intent);
    }
    private void openDelete(@NotNull String box)
    {
        Intent intent = new Intent(this,Delete.class);
        if(box.equals("Phrase"))
        {
            intent.putStringArrayListExtra("PhrasesIDList",phrasesIdList);
            intent.putExtra("CategorySelected",categorySelected);
        }
        intent.putExtra("Box",box);
        startActivity(intent);
    }
    private void openEdit(@NotNull String box)
    {
        Intent intent = new Intent(this,Edit.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(box.equals("Phrase"))
        {
            intent.putStringArrayListExtra("PhrasesIDList",phrasesIdList);
        }
        intent.putExtra("PC",categorySelected);
        intent.putExtra("Box",box);
        startActivity(intent);
    }

    //RETURN BUTTON FUNCTION
    @NotNull
    private Button getPhraseButton(int id, String text)
    {
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnParams.setMargins(5,5,5,5);
        Button btn = new Button(this);
        btn.setLayoutParams(btnParams);
        btn.setAllCaps(false);
        btn.setTextSize(14);
        btn.setBackgroundResource(R.drawable.selectedbuttonshape);
        btn.setId(id);
        btn.setText(text);
        return btn;
    }
    @org.jetbrains.annotations.NotNull
    private Button getCategoryButton(int id, String text)
    {
        Button btn = new Button(this);
        btn.setId(id);
        btn.setAllCaps(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(4,2,4,2);
        btn.setLayoutParams(params);
        btn.setText(text);
        btn.setTextSize(15);
        return btn;
    }

    //END OF APP
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
