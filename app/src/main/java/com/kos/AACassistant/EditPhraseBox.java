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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class EditPhraseBox extends AppCompatActivity {

    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private int phraseID;
    private String passedCategory;
    private String passedValue;
    private ArrayList<String> phrasesIdList;
    private Button btnReturn;
    private Button btnSavePhraseChanges;
    private PhrasesDBH phrasesDBH;
    private Cursor phrasesData;
    private EditText etxtPhraseText;
    private EditText etxtPhraseDisplayText;
    private LinearLayout epbLayout;
    private TextView epbTitle;
    private TextView txtEditInfo;
    private TextView txtPhrase;
    private TextView txtPhraseDisplay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrase_box);

        //SET UP VARIABLES AND INTENTS
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();
        phrasesIdList = getIntent().getStringArrayListExtra("PhrasesIDList");
        passedCategory = getIntent().getStringExtra("PC");
        passedValue = getIntent().getStringExtra("Box");
        phraseID = getIntent().getExtras().getInt("ID");
        btnReturn = findViewById(R.id.btnReturn);
        btnSavePhraseChanges = findViewById(R.id.btnSavePhraseChanges);
        etxtPhraseText = findViewById(R.id.etxtPhraseText);
        etxtPhraseDisplayText = findViewById(R.id.etxtPhraseDisplayText);
        epbLayout = findViewById(R.id.epbLayout);
        epbTitle = findViewById(R.id.epbTitle);
        txtEditInfo = findViewById(R.id.txtEditInfo);
        txtPhrase = findViewById(R.id.txtPhrase);
        txtPhraseDisplay = findViewById(R.id.txtPhraseDisplay);
        phrasesDBH = new PhrasesDBH(this);

        //SET UP PHRASE TEXT BOXES
        setUpCurrentPhrase();

        //SET FUNCTIONS TO BUTTONS
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSavePhraseChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhraseChanges();
            }
        });

        //SETTING UP THE POP UP WINDOW
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int) (w * .9), (int) (h * .8));

        checkColorBlindStatus();

    }

    private void checkColorBlindStatus(){
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            epbLayout.setBackgroundResource(R.drawable.selectedcolorblindbtn);
            epbTitle.setTextColor(Color.BLACK);
            txtEditInfo.setBackgroundResource(R.drawable.colorblindbtn);
            txtPhrase.setTextColor(Color.BLACK);
            txtPhraseDisplay.setTextColor(Color.BLACK);
            btnReturn.setBackgroundResource(R.drawable.colorblindbtn);
            btnSavePhraseChanges.setBackgroundResource(R.drawable.colorblindbtn);
            etxtPhraseText.setBackgroundResource(R.drawable.colorblindbtn);
            etxtPhraseDisplayText.setBackgroundResource(R.drawable.colorblindbtn);
        }
        else
        {
            epbLayout.setBackgroundResource(R.drawable.pop_up_window_border);
            epbTitle.setTextColor(Color.WHITE);
            txtEditInfo.setBackgroundResource(R.color.light_grey);
            txtPhrase.setTextColor(Color.WHITE);
            txtPhraseDisplay.setTextColor(Color.WHITE);
            btnReturn.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnSavePhraseChanges.setBackgroundResource(R.drawable.selectedbuttonshape);
            etxtPhraseText.setBackgroundResource(R.drawable.editbox);
            etxtPhraseDisplayText.setBackgroundResource(R.drawable.editbox);
        }
    }

    private void setUpCurrentPhrase()
    {
        phrasesData = phrasesDBH.getAllData();
        for (phrasesData.moveToFirst();!phrasesData.isAfterLast();phrasesData.moveToNext())
        {
            if(phrasesData.getString(0).equals(String.valueOf(phraseID)))
            {
                etxtPhraseText.setText(phrasesData.getString(1));
                etxtPhraseDisplayText.setText(phrasesData.getString(2));
            }
        }
    }

    private void savePhraseChanges()
    {
        String phraseID_ = String.valueOf(phraseID);
        if((etxtPhraseDisplayText.getText().length() > 0)&&(etxtPhraseText.getText().length()>0))
        {
            String phrase_ = etxtPhraseText.getText().toString();
            String displayPhrase_ = etxtPhraseDisplayText.getText().toString();
            String symbol_ = "Empty";
            phrasesDBH.updateData(phraseID_,phrase_,displayPhrase_,symbol_);
            Toast.makeText(this, "Phrase Updated", Toast.LENGTH_SHORT).show();
        }
        else
        {
            alertDialog("Fill the data fields to save changes!");
        }
    }

    //ALERT BOX
    private void alertDialog(String message)
    {
        android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Input Error!")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }

    public void onDestroy()
    {
        Intent intent = new Intent(this,Edit.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putStringArrayListExtra("PhrasesIDList",phrasesIdList);
        intent.putExtra("PC",passedCategory);
        intent.putExtra("Box",passedValue);
        startActivity(intent);
        super.onDestroy();
    }

}
