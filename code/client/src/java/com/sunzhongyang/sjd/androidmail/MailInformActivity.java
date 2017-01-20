package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MailInformActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_mail_inform);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        SharedPreferences loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        //获取可编辑 loginStatus 的编辑器
        final SharedPreferences.Editor editor = loginStatus.edit();

        LinearLayout openLinearLayout = (LinearLayout) findViewById(R.id.openLinearLayout);
        LinearLayout closeLinearLayout = (LinearLayout) findViewById(R.id.closeLinearLayout);

        final ImageView openImageView = (ImageView) findViewById(R.id.openImageView);
        final ImageView closeImageView = (ImageView) findViewById(R.id.closeImageView);

        if(loginStatus.getString("mailinform", "true").equals("true"))
        {
            openImageView.setVisibility(View.VISIBLE);
            closeImageView.setVisibility(View.INVISIBLE);
        }
        else
        {
            openImageView.setVisibility(View.INVISIBLE);
            closeImageView.setVisibility(View.VISIBLE);
        }

        openLinearLayout.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                openImageView.setVisibility(View.VISIBLE);
                closeImageView.setVisibility(View.INVISIBLE);

                editor.putString("mailinform", "true");

                editor.commit();
            }
        });

        closeLinearLayout.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                openImageView.setVisibility(View.INVISIBLE);
                closeImageView.setVisibility(View.VISIBLE);

                editor.putString("mailinform", "false");

                editor.commit();
            }
        });
    }
}