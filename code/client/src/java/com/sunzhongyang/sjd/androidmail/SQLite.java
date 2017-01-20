package com.sunzhongyang.sjd.androidmail;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLite extends SQLiteOpenHelper
{
    //设置数据库的基本信息
    private static final String DB_NAME = "database";
    private static final String TABLE_NAME = "contact";
    private static final int DB_VERSION = 1;

    SharedPreferences loginStatus;

    //默认初始化
    public SQLite(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = context.getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        //创建数据库
        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME + " (id INTEGER PRIMARY KEY,name TEXT,work TEXT,address TEXT,user TEXT)";
        String CREATE_INBOX_TABLE = "CREATE TABLE if not exists " + "inbox" + " (id INTEGER PRIMARY KEY,sender TEXT,receiver TEXT,subject TEXT,content TEXT,time TEXT,user TEXT,status TEXT)";
        String CREATE_OUTBOX_TABLE = "CREATE TABLE if not exists " + "outbox" + " (id INTEGER PRIMARY KEY,sender TEXT,receiver TEXT,subject TEXT,content TEXT,time TEXT,user TEXT)";
        String CREATE_TRASHBOX_TABLE = "CREATE TABLE if not exists " + "trashbox" + " (id INTEGER PRIMARY KEY,sender TEXT,receiver TEXT,subject TEXT,content TEXT,time TEXT,user TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_INBOX_TABLE);
        sqLiteDatabase.execSQL(CREATE_OUTBOX_TABLE);
        sqLiteDatabase.execSQL(CREATE_TRASHBOX_TABLE);
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

    public void insertMail(MailItem mail, SQLiteDatabase db, String table)
    {
        //通过ContentValues在数据库中增加新内容
        ContentValues cv = new ContentValues();
        cv.put("user", loginStatus.getString("username", "none"));
        cv.put("sender", mail.sender);
        cv.put("receiver", mail.receiver);
        cv.put("subject", mail.topic);
        cv.put("content", mail.mailContent);
        cv.put("time", mail.time);

        //写入数据库
        db.insert(table, null, cv);
    }

    public MailItem getMail(int id, Cursor full_cursor)
    {
        MailItem mail = new MailItem();
        while(full_cursor.moveToNext())
        {
            if(full_cursor.getInt(0) == id)
            {
                mail.sender = full_cursor.getString(1);
                mail.receiver = full_cursor.getString(2);
                mail.topic = full_cursor.getString(3);
                mail.mailContent = full_cursor.getString(4);
                mail.time = full_cursor.getString(5);
            }
        }

        return mail;
    }

    public void deleteMail(int id, SQLiteDatabase db, String table)
    {
        db.execSQL("delete from " + table + " where id=" + id);
        db.close();
    }
}
