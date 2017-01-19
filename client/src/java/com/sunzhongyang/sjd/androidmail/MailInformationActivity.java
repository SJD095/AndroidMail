package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MailInformationActivity extends Activity
{
    String mailbox;
    MailItem mail;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent=getIntent();

        //获取一个数据库
        final SQLite tmp_db = new SQLite(this);
        final SQLiteDatabase current_db = tmp_db.get_db();

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
        final TextView timeTextView = (TextView) findViewById(R.id.timeTextView);

        if(bundle.getString("content").equals("detail"))
        {
            id = bundle.getInt("id");
            mailbox = bundle.getString("mailbox");

            final Cursor full_cursor = current_db.rawQuery("select * from " + mailbox, null);

            mail = tmp_db.getMail(id, full_cursor);

            topicTextView.setText(mail.topic);
            senderTextView.setText(mail.sender);
            contentTextView.setText(mail.mailContent);
            receiverTextView.setText(mail.receiver);
            timeTextView.setText(mail.time);
        }

        replyButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                bundle.putString("content", "detail");
                bundle.putString("address", senderTextView.getText().toString());

                //打开写邮件页面
                Intent intent = new Intent(MailInformationActivity.this, WriteMail.class);
                intent.putExtras(bundle);
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
                if(mailbox.equals("trashbox"))
                {
                    tmp_db.deleteMail(id, current_db, mailbox);
                    finish();
                }
                else
                {
                    tmp_db.insertMail(mail, current_db, "trashbox");
                    tmp_db.deleteMail(id, current_db, mailbox);
                    finish();
                }
            }
        });
    }
}
