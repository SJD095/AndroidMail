package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    String username;
    SharedPreferences loginStatus;
    SharedPreferences.Editor sharedPreferencesEditor;

    EditText setAccountEditText;
    EditText passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        //获取可编辑 loginStatus 的编辑器
        sharedPreferencesEditor = loginStatus.edit();

        //获取存储在本次的用户名和密码,如果没有则为空(注册时不能为空)
        username = loginStatus.getString("username", "");
        final String password = loginStatus.getString("password", "");

        //向服务器检查用户名密码是否正确
        netWork.checkAuthentication(username, password, checkHandler);
    }

    final Handler loginHandler = new Handler()
    {
        public void handleMessage(Message message)
        {
            switch (message.what) {
                //根据服务器返回消息类型决定对应的操作
                case 0:
                    String result = message.obj.toString();
                    if(result.equals("OK"))
                    {
                        //打开主页面
                        sharedPreferencesEditor.putString("username", setAccountEditText.getText().toString());
                        sharedPreferencesEditor.putString("password", passwordEditText.getText().toString());

                        sharedPreferencesEditor.commit();

                        Intent intent = new Intent(LoginActivity.this, MainPage.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(result.equals("Error password"))
                    {
                        controller.makeToast(LoginActivity.this, "密码错误");
                    }
                    else
                    {
                        controller.makeToast(LoginActivity.this, "用户不存在");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    final Handler checkHandler = new Handler()
    {
        public void handleMessage(Message message)
        {
            switch (message.what) {
                //根据服务器返回消息类型决定对应的操作
                case 0:
                    String result = message.obj.toString();
                    if(result.equals("OK"))
                    {
                        //打开主页面
                        Intent intent = new Intent(LoginActivity.this, MainPage.class);
                        startActivity(intent);
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
                        setAccountEditText = (EditText) findViewById(R.id.setAccountEditText);
                        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                        final LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
                        loginButton = (Button) findViewById(R.id.loginButton);

                        setAccountEditText.addTextChangedListener(textWatcher);
                        passwordEditText.addTextChangedListener(textWatcher);

                        TextView forgetPassword = (TextView) findViewById(R.id.forgetPassword);

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

                        loginButton.setOnClickListener(new View.OnClickListener()
                        {
                            //重写onClick,规定点击 registerAccount 执行的动作
                            @Override
                            public void onClick(View view)
                            {
                                netWork.checkAuthentication(setAccountEditText.getText().toString(), passwordEditText.getText().toString(), loginHandler);
                            }
                        });

                        forgetPassword.setOnClickListener(new View.OnClickListener()
                        {
                            //重写onClick,规定点击 registerAccount 执行的动作
                            @Override
                            public void onClick(View view)
                            {
                                controller.makeToast(LoginActivity.this, "发邮件给我吧：szy@sunzhongyang.com");
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
                    break;
                default:
                    break;
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher()
    {

        @Override
        public void afterTextChanged(Editable s)
        {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count)
        {
            //根据输入内容是否为空设置按钮透明度
            String account = setAccountEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if(account.equals("") || password.equals(""))
            {
                loginButton.setAlpha((float) 0.6);
            }
            else
            {
                loginButton.setAlpha((float) 1.0);
            }
        }
    };
}
