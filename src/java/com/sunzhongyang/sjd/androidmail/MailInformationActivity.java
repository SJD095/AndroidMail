package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MailInformationActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent=getIntent();

        //获取bundle及其中的信息
        Bundle bundle = intent.getExtras();

        setContentView(R.layout.activity_mail_information);

        ImageView replyButton = (ImageView) findViewById(R.id.replyButton);
        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        ImageView deleteButton = (ImageView) findViewById(R.id.deleteButton);

        final TextView receiverTextView = (TextView) findViewById(R.id.receiverTextView);
        final TextView senderTextView = (TextView) findViewById(R.id.senderTextView);
        final TextView topicTextView = (TextView) findViewById(R.id.topicTextView);
        final TextView contentTextView = (TextView) findViewById(R.id.contentTextView);

        receiverTextView.setText("szy@sunzhongyang.com");

        if(bundle.getString("content").equals("detail"))
        {
            String sender = bundle.getString("sender");
            String topic = bundle.getString("topic");
            String content = bundle.getString("brief");

            topicTextView.setText(topic);
            senderTextView.setText(sender);
            contentTextView.setText(content);
        }

        replyButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                //打开写邮件页面
                Intent intent = new Intent(MailInformationActivity.this, WriteMail.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
