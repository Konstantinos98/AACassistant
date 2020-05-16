package com.kos.AACassistant;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

public class SettingsDBH extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "appDatabase.db";
    public static final String TABLE_NAME = "settingsTable";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "Pitch";
    public static final String COL_2 = "Speed";
    public static final String COL_3 = "ColorBlind";
    public static final String COL_4 = "CopyMode";
    public static final String COL_5 = "SortedCategories";

    public SettingsDBH(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(@NotNull SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "settingsTable" +" (ID INTEGER," +
                "PITCH FLOAT,SPEED FLOAT,COLORBLIND TEXT,COPYMODE TEXT,SORTEDCATEGORIES TEXT) ");
        db.execSQL("CREATE TABLE " + "mainCategoriesTable" +
                " (MAINCATEGORYID INTEGER,NAME TEXT,PHRASESLIST TEXT) ");
        db.execSQL("CREATE TABLE " + "categoriesTable" +
                " (CATEGORYID INTEGER,NAME TEXT,SYMBOL TEXT,PARENTID INTEGER,PHRASESLIST TEXT) ");
        db.execSQL("CREATE TABLE " + "phrasesTable" +
                " (PHRASEID INTEGER,PHRASE TEXT,DISPLAYPHRASE TEXT,SYMBOL TEXT) ");
    }

    @Override
    public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(int id,float p,float s,String cb,String cm,String sc)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,p);
        cv.put(COL_2,s);
        cv.put(COL_3,cb);
        cv.put(COL_4,cm);
        cv.put(COL_5,sc);

        long result = db.insert(TABLE_NAME,null,cv);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return  result;
    }

    public boolean updateData(String id,float p,float s,String cb,String cm,String sc)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,p);
        cv.put(COL_2,s);
        cv.put(COL_3,cb);
        cv.put(COL_4,cm);
        cv.put(COL_5,sc);
        db.update(TABLE_NAME,cv,"id = ?",new String[] { id });
        return true;
    }



}
