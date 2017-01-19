package com.sunzhongyang.sjd.androidmail;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences loginStatus;

    public GetMailService()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);

        final Handler handler = new Handler()
        {
            public void handleMessage(Message message)
            {
                switch (message.what)
                {
                    //根据服务器返回消息类型决定对应的操作
                    case 0:
                        //如果服务器表示没有邮件
                        if(message.obj.equals("NO"))
                        {

                        }
                        else
                        {
                            //获取一个解析含有邮件信息的JSON字符串的对象
                            JSONTokener jsonParser = new JSONTokener(message.obj.toString());

                            try
                            {
                                //获取一个数据库
                                SQLite tmp_db = new SQLite(getApplicationContext());
                                final SQLiteDatabase current_db = tmp_db.get_db();

                                //获取邮件信息
                                JSONObject mail = (JSONObject) jsonParser.nextValue();
                                String From = mail.getString("From");
                                String Subject = mail.getString("Subject");
                                String time = mail.getString("Date");
                                String content = mail.getString("content");

                                //通过ContentValues在数据库中增加新内容
                                ContentValues cv = new ContentValues();
                                cv.put("user", loginStatus.getString("username", "none"));
                                cv.put("sender", From);
                                cv.put("receiver", loginStatus.getString("username", "none") + "@sunzhongyang.com");
                                cv.put("subject", Subject);
                                cv.put("content", content);
                                cv.put("time", time);
                                cv.put("status", "unread");

                                //写入数据库
                                current_db.insert("inbox", null, cv);

                                //发送一个广播给 MailFrame, 更新含有所有邮件的 ListView
                                Intent intent = new Intent("android.intent.action.refreshUI");
                                sendBroadcast(intent);

                                //发送一个下拉之后可以看到的通知,包含邮件的发送者和邮件主题
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
                //通过网络控制器向服务器发送一个检查新邮件的请求
                netWork.sendContent(controller.receiveMailURL, loginStatus.getString("username", "none") + "@sunzhongyang.com", handler);
                //netWork.sendContent(controller.receiveMailURL, "checkunseenmail", handler);
                //每2.5秒检查一次新邮件
                handler.postDelayed(this, loginStatus.getInt("frequency", 2500));
            }
        };

        //开始第一次检查
        handler.postDelayed(runnable, loginStatus.getInt("frequency", 2500));
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
