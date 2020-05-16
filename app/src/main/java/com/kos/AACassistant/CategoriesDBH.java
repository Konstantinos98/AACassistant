package com.kos.AACassistant;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

public class CategoriesDBH extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "appDatabase.db";
    public static final String TABLE_NAME = "categoriesTable";
    public static final String COL_0 = "CategoryID";
    public static final String COL_1 = "Name";
    public static final String COL_2 = "Symbol";
    public static final String COL_3 = "ParentID";
    public static final String COL_4 = "PhrasesList";

    public CategoriesDBH(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {}
    @Override
    public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(int id,String n,String s,int pid,String pList)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,n);
        cv.put(COL_2,s);
        cv.put(COL_3,pid);
        cv.put(COL_4,pList);
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
    public boolean updateData(String id,String n,String s,int pid,String pList)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,n);
        cv.put(COL_2,s);
        cv.put(COL_3,pid);
        cv.put(COL_4,pList);
        db.update(TABLE_NAME,cv,"CategoryID = ?",new String[] { id });
        return true;
    }
    public int getNewID()
    {
        int lid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        result.moveToLast();
        lid = Integer.parseInt(result.getString(0));
        return lid + 1;
    }
    public Integer deleteData(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"CategoryID = ?",new String[] {id});
    }
}
