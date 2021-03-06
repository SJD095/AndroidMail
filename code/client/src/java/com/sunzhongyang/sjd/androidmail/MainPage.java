package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainPage extends FragmentActivity
{
    private FragmentManager fragmentManager;

    private MailFrame mailFrame;
    private ContactFrame contactFrame;
    private SettingFrame settingFrame;

    RelativeLayout mailLayout;
    RelativeLayout contactLayout;
    RelativeLayout settingLayout;

    ImageView mailToggleButton;
    ImageView contactToggleButton;
    ImageView settingToggleButton;

    TextView mailText;
    TextView contactText;
    TextView settingText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main_page);

        //获得一个数据库
        SQLite helper = new SQLite(this);
        final SQLiteDatabase current_db = helper.get_db();

        //获取标签管理控件
        fragmentManager = this.getSupportFragmentManager();

        //获取各个控件
        mailLayout = (RelativeLayout) findViewById(R.id.mailLayout);
        contactLayout = (RelativeLayout) findViewById(R.id.contactLayout);
        settingLayout = (RelativeLayout) findViewById(R.id.settingLayout);
        mailToggleButton = (ImageView) findViewById(R.id.mailToggleButton);
        contactToggleButton = (ImageView) findViewById(R.id.contactToggleButton);
        settingToggleButton = (ImageView) findViewById(R.id.settingToggleButton);
        mailText = (TextView) findViewById(R.id.mailText);
        contactText = (TextView) findViewById(R.id.contactText);
        settingText = (TextView) findViewById(R.id.settingText);

        mailLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 mailLayout 执行的动作
            @Override
            public void onClick(View view)
            {
                //载入 MailFrame
                setFrame(0);
            }
        });

        contactLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 contactLayout 执行的动作
            @Override
            public void onClick(View view)
            {
                //载入 ContactFrame
                setFrame(1);
            }
        });

        settingLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 settingLayout 执行的动作
            @Override
            public void onClick(View view)
            {
                //载入 MailFrame
                setFrame(2);
            }
        });

        //发送广播启动 GetMailService, 开始接收邮件
        Intent intent = new Intent(MainPage.this, GetMailService.class);
        startService(intent);

        //设置默认初始标签为 MailFrame
        setFrame(0);
    }

    private void setFrame(int frameNumber)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //重设底部栏为初始状态, 也就是恢复到均为被点击的样式
        clearBottomBar();
        //隐藏正在显示的标签
        closeFragments(fragmentTransaction);

        switch(frameNumber)
        {
            case 0:
                //更换按钮样式为点击后的效果
                mailToggleButton.setImageResource(R.drawable.mail);
                //设置说明文字为点击后的效果
                resetBottomBarText(mailText);

                if(mailFrame == null)
                {
                    //如果为空，则建一个新的并将它显示出来
                    mailFrame = new MailFrame();
                    fragmentTransaction.add(R.id.frameContent, mailFrame);
                }
                else
                {
                    //如果不为空，则直接将它显示出来
                    fragmentTransaction.show(mailFrame);
                }
                break;
            case 1:
                //更换按钮样式为点击后的效果
                contactToggleButton.setImageResource(R.drawable.contact);
                //设置说明文字为点击后的效果
                resetBottomBarText(contactText);

                if(contactFrame == null)
                {
                    //如果为空，则建一个新的并将它显示出来
                    contactFrame = new ContactFrame();
                    fragmentTransaction.add(R.id.frameContent, contactFrame);
                }
                else
                {
                    // 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(contactFrame);
                }
                break;
            case 2:
                //更换按钮样式为点击后的效果
                settingToggleButton.setImageResource(R.drawable.set);
                //设置说明文字为点击后的效果
                resetBottomBarText(settingText);

                if(settingFrame == null)
                {
                    //如果为空，则建一个新的并将它显示出来
                    settingFrame = new SettingFrame();
                    fragmentTransaction.add(R.id.frameContent, settingFrame);
                }
                else
                {
                    // 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(settingFrame);
                }
                break;
        }

        //执行更改
        fragmentTransaction.commit();
    }

    private void clearBottomBar()
    {
        //设置按钮背景图片
        mailToggleButton.setImageResource(R.drawable.mail_2);
        contactToggleButton.setImageResource(R.drawable.contact_2);
        settingToggleButton.setImageResource(R.drawable.set_2);

        //设置说明文字颜色
        mailText.setTextColor(getResources().getColor(R.color.frameGrey));
        contactText.setTextColor(getResources().getColor(R.color.frameGrey));
        settingText.setTextColor(getResources().getColor(R.color.frameGrey));
    }

    private void resetBottomBarText(TextView textView)
    {
        //设置说明文字颜色
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void closeFragments(FragmentTransaction fragmentTransaction)
    {
        if (mailFrame != null)
        {
            fragmentTransaction.hide(mailFrame);
        }

        if (contactFrame != null)
        {
            fragmentTransaction.hide(contactFrame);
        }

        if (settingFrame != null)
        {
            fragmentTransaction.hide(settingFrame);
        }
    }
}