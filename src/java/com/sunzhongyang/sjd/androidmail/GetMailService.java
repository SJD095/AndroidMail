package com.sunzhongyang.sjd.androidmail;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class GetMailService extends Service
{
    NetWork netWork = new NetWork();
    Controller controller = new Controller();

    public GetMailService()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        final Handler handler=new Handler()
        {
            public void handleMessage(Message message)
            {
                switch (message.what)
                {
                    //根据服务器返回消息类型决定对应的操作
                    case 0:
                        if(message.obj.equals("NO"))
                        {

                        }
                        else
                        {
                            JSONTokener jsonParser = new JSONTokener(message.obj.toString());
                            try
                            {
                                //获取一个数据库
                                SQLite tmp_db = new SQLite(getApplicationContext());
                                final SQLiteDatabase current_db = tmp_db.get_db();

                                JSONObject mail = (JSONObject) jsonParser.nextValue();
                                String From = mail.getString("From");
                                String Subject = mail.getString("Subject");
                                String time = mail.getString("Date");
                                String content = mail.getString("content");

                                Log.i("d", Subject);

                                //通过ContentValues在数据库中增加新内容
                                ContentValues cv = new ContentValues();
                                cv.put("sender", From);
                                cv.put("subject", Subject);
                                cv.put("content", content);
                                cv.put("time", time);

                                current_db.insert("inbox", null, cv);

                                Intent intent = new Intent("android.intent.action.refreshUI");
                                sendBroadcast(intent);

                                Intent notificationIntent = new Intent("com.sunzhongyang.sjd.AndroidMail.staticcreceiver");
                                Bundle bundle = new Bundle();
                                bundle.putString("sender", From);
                                bundle.putString("subject", Subject);

                                notificationIntent.putExtras(bundle);
                                sendBroadcast(notificationIntent);
                            }
                            catch (JSONException ex)
                            {

                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                netWork.sendContent(controller.receiveMailURL, "checkunseenmail", handler);
                handler.postDelayed(this, 2500);
            }
        };

        handler.postDelayed(runnable, 2500);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
