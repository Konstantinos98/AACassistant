package com.kos.AACassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainCategoriesDBH extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "appDatabase.db";
    public static final String TABLE_NAME = "mainCategoriesTable";
    public static final String COL_0 = "MainCategoryID";
    public static final String COL_1 = "Name";
    public static final String COL_2 = "PhrasesList";

    public MainCategoriesDBH(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(int id,String n,String pList)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,n);
        cv.put(COL_2,pList);
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
    public boolean updateData(String id,String n,String pList)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,n);
        cv.put(COL_2,pList);
        db.update(TABLE_NAME,cv,"MainCategoryID = ?",new String[] { id });
        return true;
    }
}
