package com.kos.AACassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseControl extends AppCompatActivity {


    private SettingsDBH settingsDBH;
    private Cursor settingsData;
    private LinearLayout dcLayout;
    private TextView txtDCTitle,txtDCInfo;
    private Button btnDCReturnToSettings,btnUploadDatabase,btnDownloadDatabase;
    private String speechText,activityFrom,categorySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_control);

        //DATABASE VARIABLES
        settingsDBH = new SettingsDBH(this);
        settingsData = settingsDBH.getAllData();

        //INTENTS
        activityFrom = getIntent().getStringExtra("From");
        categorySelected = getIntent().getStringExtra("PC");
        speechText = getIntent().getStringExtra("SpeechText");

        //LAYOUT OBJECTS
        dcLayout = findViewById(R.id.dcLayout);
        txtDCInfo = findViewById(R.id.txtDCInfo);
        txtDCTitle = findViewById(R.id.txtDCTitle);
        btnDCReturnToSettings = findViewById(R.id.btnDCReturnToSettings);
        btnUploadDatabase = findViewById(R.id.btnUploadDatabase);
        btnDownloadDatabase  = findViewById(R.id.btnDownloadDatabase);

        //FUNCTIONS
        btnDCReturnToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainSettings();
            }
        });
        btnDownloadDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDatabase();
            }
        });
        btnUploadDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDatabase();
            }
        });
        checkColorBlindStatus();
    }

    private void downloadDatabase()
    {
        Intent downloadDBIntent;
        downloadDBIntent = new Intent(Intent.ACTION_GET_CONTENT);
        downloadDBIntent.setType("application/octet-stream");
        startActivityForResult(downloadDBIntent,10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 10 && resultCode == RESULT_OK)
        {
            try{
                File databaseFile = null;
                try {
                    databaseFile = getDBFile();
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }

                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(databaseFile);

                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();

                Toast.makeText(this, "New database imported.", Toast.LENGTH_SHORT).show();
                Intent startPage = new Intent(this, StartActivity.class);
                startPage.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(startPage);

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void copyStream(@NotNull InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @NotNull
    private File getDBFile() throws IOException {

        File data = Environment.getDataDirectory();
        String dbName = "appDatabase.db";
        String currentDBPath = "//data//com.example.finalyearproject//databases//" + dbName;

        File oldDb = new File(data, currentDBPath);
        return oldDb;
    }

    public class FileProvider extends android.support.v4.content.FileProvider {

        public Uri getDatabaseURI(Context c) {

            File data = Environment.getDataDirectory();
            String dbName = "appDatabase.db";
            String currentDBPath = "//data//com.example.finalyearproject//databases//" + dbName;

            File exportFile = new File(data, currentDBPath);

            return getFileUri(c, exportFile);
        }

        public Uri getFileUri(Context c, File f){
            return getUriForFile(c, "com.example.finalyearproject.fileprovider", f);
        }

    }

    private void uploadDatabase()
    {
        try {
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("application/octet-stream");
            Uri uri = new FileProvider().getDatabaseURI(this);
            fileIntent.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(Intent.createChooser(fileIntent,"Upload Database via:"));
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkColorBlindStatus()
    {
        settingsData.moveToFirst();
        String colorBlind = settingsData.getString(3);
        if(colorBlind.equals("True"))
        {
            btnDCReturnToSettings.setBackgroundResource(R.drawable.colorblindbtn);
            txtDCInfo.setBackgroundResource(R.drawable.colorblindbtn);
            txtDCTitle.setTextColor(Color.BLACK);
            btnUploadDatabase.setBackgroundResource(R.drawable.colorblindbtn);
            btnDownloadDatabase.setBackgroundResource(R.drawable.colorblindbtn);
            dcLayout.setBackgroundResource(R.color.yellow);
        }
        else
        {
            btnDCReturnToSettings.setBackgroundResource(R.drawable.selectedbuttonshape);
            txtDCInfo.setBackgroundResource(R.color.light_grey);
            btnUploadDatabase.setBackgroundResource(R.drawable.selectedbuttonshape);
            btnDownloadDatabase.setBackgroundResource(R.drawable.selectedbuttonshape);
            txtDCTitle.setTextColor(Color.WHITE);
            dcLayout.setBackgroundResource(R.color.gray);
        }
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
