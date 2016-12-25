package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends Activity
{
    //获取内容控制单元
    Controller controller = new Controller();
    //获取网络访问单元
    NetWork netWork = new NetWork();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        SharedPreferences loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        //获取可编辑 loginStatus 的编辑器
        SharedPreferences.Editor sharedPreferencesEditor = loginStatus.edit();

        //获取存储在本次的用户名和密码,如果没有则为空(注册时不能为空)
        String username = loginStatus.getString("username", "");
        String password = loginStatus.getString("password", "");

        //向服务器检查用户名密码是否正确
        if(netWork.checkAuthentication(username, password))
        {
            //打开主页面
            Intent intent = new Intent(LoginActivity.this, MainPage.class);
            startActivity(intent);

            //结束当前活动,避免通过返回键返回
            finish();
        }
        else
        {
            setContentView(R.layout.activity_login);

            //如果之前已有登录账户,且登录失败
            if(!username.equals(""))
            {
                controller.makeToast(LoginActivity.this, "用户名密码错误,请重新输入");
            }

            TextView registerAccount = (TextView) findViewById(R.id.registerAccount);
            final EditText setAccountEditText = (EditText) findViewById(R.id.setAccountEditText);
            final LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);

            setAccountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if(hasFocus)
                    {
                        mainLinearLayout.setBackgroundResource(R.drawable.edt_bg_selected);
                    }
                    else
                    {
                        mainLinearLayout.setBackgroundResource(R.drawable.edt_bg_normal);
                    }
                }
            });

            registerAccount.setOnClickListener(new View.OnClickListener()
            {
                //重写onClick,规定点击 registerAccount 执行的动作
                @Override
                public void onClick(View view)
                {
                    //打开注册页面
                    Intent intent = new Intent(LoginActivity.this, RegisterAccountName.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
