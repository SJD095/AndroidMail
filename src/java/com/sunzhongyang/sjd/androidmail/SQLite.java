package com.sunzhongyang.sjd.androidmail;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLite extends SQLiteOpenHelper
{
    //设置数据库的基本信息
    private static final String DB_NAME = "database";
    private static final String TABLE_NAME = "contact";
    private static final int DB_VERSION = 1;

    //默认初始化
    public SQLite(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        //创建数据库
        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME + " (contact_id INTEGER PRIMARY KEY,name TEXT,work TEXT,address TEXT)";
        String CREATE_INBOX_TABLE = "CREATE TABLE if not exists " + "inbox" + " (inbox_id INTEGER PRIMARY KEY,sender TEXT,subject TEXT,content TEXT,time TEXT)";
        String CREATE_OUTBOX_TABLE = "CREATE TABLE if not exists " + "outbox" + " (outbox_id INTEGER PRIMARY KEY,receiver TEXT,subject TEXT,content TEXT,time TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_INBOX_TABLE);
        sqLiteDatabase.execSQL(CREATE_OUTBOX_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il)
    {

    }

    public SQLiteDatabase  get_db()
    {
        //返回一个可写的数据库
        SQLiteDatabase db = getWritableDatabase();
        return db;
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.close();
        super.finalize();
    }
}
