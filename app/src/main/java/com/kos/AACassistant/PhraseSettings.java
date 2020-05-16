package com.kos.AACassistant;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PhraseSettings extends Activity {

    public SettingsDBH settingsDB;
    private Cursor settingsData;
    private SeekBar seekBarPitch;
    private SeekBar seekBarSpeed;
    private Button btnSave;
    private Button btnClose;
    private Button btnReset;
    private String colorBlindStatus;
    private String copyMode;
    private String sortedCategories;
    private LinearLayout psLayout;
    private TextView psTitle;
    private TextView txtPitch;
    private TextView txtSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsDB = new SettingsDBH(this);

        //Setting up the pop up Window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int)(w*.9),(int)(h*.6));

        //SET UP OF OBJECTS
        seekBarSpeed = findViewById(R.id.seek_bar_speed);
        seekBarPitch = findViewById(R.id.seek_bar_pitch);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
        btnReset = findViewById(R.id.btnReset);
        psTitle = findViewById(R.id.psTitle);
        psLayout = findViewById(R.id.psLayout);
        txtPitch = findViewById(R.id.txtPitch);
        txtSpeed = findViewById(R.id.txtSpeed);

        //SET UP DATA FROM DATABASE
        setDBData();
        settingsData.moveToFirst();
        colorBlindStatus = settingsData.getString(3);
        copyMode = settingsData.getString(4);
        sortedCategories = settingsData.getString(5);

        //Set Functions on the buttons
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSettings();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSettings();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        checkColorBlindStatus();
    }

    private void checkColorBlindStatus()
    {
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            psLayout.setBackgroundResource(R.drawable.selectedcolorblindbtn);
            psTitle.setTextColor(Color.BLACK);
            txtPitch.setTextColor(Color.BLACK);
            txtSpeed.setTextColor(Color.BLACK);
            btnClose.setBackgroundResource(R.drawable.colorblindbtn);
            btnReset.setBackgroundResource(R.drawable.colorblindbtn);
            btnSave.setBackgroundResource(R.drawable.colorblindbtn);
        }
        else
        {
            psLayout.setBackgroundResource(R.drawable.pop_up_window_border);
            psTitle.setTextColor(Color.WHITE);
            txtPitch.setTextColor(Color.WHITE);
            txtSpeed.setTextColor(Color.WHITE);
            btnClose.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnReset.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnSave.setBackgroundResource(R.drawable.selectedbuttonshape);
        }
    }

    //Close PhraseSettings window
    private void closeSettings()
    {
        finish();
    }

    //Resets PhraseSettings
    private void resetSettings()
    {
        seekBarSpeed.setProgress(50);
        seekBarPitch.setProgress(50);
    }

    //Save settings to DB
    private void saveSettings()
    {
        boolean isUpdated = settingsDB.updateData("1",
                seekBarPitch.getProgress(),seekBarSpeed.getProgress(),colorBlindStatus,copyMode,sortedCategories);
        if(isUpdated == true)
            Toast.makeText(PhraseSettings.this,"PhraseSettings Updated",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(PhraseSettings.this,"Data not Updated",Toast.LENGTH_LONG).show();
    }


    //VIEW DB DATA
    private void setDBData()
    {
        settingsData = settingsDB.getAllData();
        if(settingsData.getCount() == 0)
        {
            addInitialDataToSDB();
        }
        while(settingsData.moveToNext())
        {
            seekBarPitch.setProgress(Integer.parseInt(settingsData.getString(1)));
            seekBarSpeed.setProgress(Integer.parseInt(settingsData.getString(2)));
        }
    }

    //ADD DATA TO DB
    private void addInitialDataToSDB()
    {
        settingsDB.insertData(1,50,50,"False","False","None");
        settingsData = settingsDB.getAllData();
    }

}
