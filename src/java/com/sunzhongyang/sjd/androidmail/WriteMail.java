package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class WriteMail extends Activity
{
    NetWork netWork = new NetWork();
    Controller controller = new Controller();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_write_mail);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        SharedPreferences loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        //获取可编辑 loginStatus 的编辑器
        SharedPreferences.Editor sharedPreferencesEditor = loginStatus.edit();

        //获取存储在本次的用户名和密码,如果没有则为空(注册时不能为空)
        //final String sender = loginStatus.getString("username", "") + "@sunzhongyang.com";
        final String sender = "szy@sunzhongyang.com";

        Intent intent=getIntent();

        //获取bundle及其中的信息
        Bundle bundle = intent.getExtras();

        TextView sendButton = (TextView) findViewById(R.id.sendButton);

        ImageView cancelButton = (ImageView) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        final EditText receiverEdittext = (EditText) findViewById(R.id.receiverEdittext);
        final TextView senderTextView = (TextView) findViewById(R.id.senderTextView);
        final EditText topicEditText = (EditText) findViewById(R.id.topicEditText);
        final EditText contentEditText = (EditText) findViewById(R.id.contentEditText);

        if(bundle.getString("content").equals("detail"))
        {
            String address = bundle.getString("address");

            receiverEdittext.setText(address);
        }

        senderTextView.setText(sender);

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                if(netWork.isConnected(WriteMail.this))
                {
                    String receiver = receiverEdittext.getText().toString();
                    String topic = topicEditText.getText().toString();
                    String content = contentEditText.getText().toString();

                    JSONObject mail = new JSONObject();

                    try
                    {
                        mail.put("receiver", receiver);
                        mail.put("sender", sender);
                        mail.put("topic", topic);
                        mail.put("content", content);

                        netWork.sendContent(controller.sendMailURL, mail.toString(), handler);
                        finish();
                    }
                    catch (JSONException ex)
                    {
                        // 键为null或使用json不支持的数字格式(NaN, infinities)
                        throw new RuntimeException(ex);
                    }
                }
                else
                {
                    controller.makeToast(WriteMail.this, "网络未连接");
                }
            }
        });
    }

    //建立一个handler从线程接收数据,并根据情况决定UI
    private Handler handler = new Handler()
    {
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                //根据服务器返回消息类型决定对应的操作
                case 0:
                    controller.makeToast(WriteMail.this, message.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };
}
