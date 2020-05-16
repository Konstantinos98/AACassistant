package com.kos.AACassistant;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhrasesDBH extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "appDatabase.db";
    public static final String TABLE_NAME = "phrasesTable";
    public static final String COL_0 = "PhraseID";
    public static final String COL_1 = "Phrase";
    public static final String COL_2 = "DisplayPhrase";
    public static final String COL_3 = "Symbol";

    public PhrasesDBH(Context context) {
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
    public boolean insertData(int id,String p,String dp,String s)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,p);
        cv.put(COL_2,dp);
        cv.put(COL_3,s);
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
        return db.delete(TABLE_NAME,"PhraseID = ?",new String[] {id});
    }
    public boolean updateData(String id,String p,String dp,String s)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_0,id);
        cv.put(COL_1,p);
        cv.put(COL_2,dp);
        cv.put(COL_3,s);
        db.update(TABLE_NAME,cv,"PhraseID = ?",new String[] { id });
        return true;
    }
}
