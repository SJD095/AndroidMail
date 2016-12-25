package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NetWork
{
    //向服务器检查用户名密码是否正确
    public boolean checkAuthentication(String username, String password)
    {
        return false;
    }

    public boolean checkDuplicate(String name)
    {
        return false;
    }

    public boolean registerUser(String username, String password)
    {
        return true;
    }

    public boolean isConnected(Activity activity)
    {
        //获取网络连接状态
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (int i = 0; i < networkInfo.length; i++)
        {
            if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
            {
                return true;
            }
        }

        return false;
    }

    public void sendContent(final String url, final String content, final Handler handler)
    {
        //新建一个线程执行网络访问
        new Thread(){
            @Override
            public void run()
            {
                //建立并设置连接
                HttpURLConnection connection = null;
                try
                {
                    connection = (HttpURLConnection) ((new URL(url.toString()).openConnection()));
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    //向服务器发送信息
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String request = content;

                    out.writeBytes(request);

                    //读取服务器返回的信息
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String line;

                    while((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }

                    //向handler对象发送消息以传递数据
                    Message message = new Message();
                    message.what = 0;
                    message.obj = response.toString();
                    handler.sendMessage(message);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                //断开连接
                finally
                {
                    if(connection != null)
                    {
                        connection.disconnect();
                    }
                }
            }
        }.start();
    }
}
