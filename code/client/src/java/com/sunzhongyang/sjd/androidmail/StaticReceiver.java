package com.sunzhongyang.sjd.androidmail;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class StaticReceiver extends BroadcastReceiver
{
    private static final String STATICACTION = "com.sunzhongyang.sjd.AndroidMail.staticcreceiver";

    public StaticReceiver()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //如果获取到的广播是某个准备响应的广播
        if(intent.getAction().equals(STATICACTION))
        {
            //获取bundle及其中的信息
            Bundle bundle = intent.getExtras();
            String sender = bundle.getString("sender");
            String subject = bundle.getString("subject");

            //获取保存有登陆用户信息的 SharedPreferences 对象
            SharedPreferences loginStatus = context.getSharedPreferences("loginStatus", Context.MODE_PRIVATE);

            if(loginStatus.getString("mailinform", "true").equals("true"))
            {
                //创建一个通知
                Notification.Builder builder = new Notification.Builder(context);

                builder.setContentTitle(sender)
                        .setContentText(subject)
                        .setTicker("您有一封新邮件")
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.radar_1))
                        .setSmallIcon(R.drawable.radar_1)
                        .setAutoCancel(true);

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                //设置点击通知为返回主界面
                Intent mintent = new Intent(context, MainPage.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mintent, 0);
                builder.setContentIntent(pendingIntent);

                manager.notify(0, builder.build());
            }
        }
    }
}
