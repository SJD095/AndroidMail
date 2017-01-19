package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ResetPasswordActivity extends Activity
{
    Controller controller = new Controller();
    NetWork netWork = new NetWork();

    SharedPreferences loginStatus;
    SharedPreferences.Editor sharedPreferencesEditor;

    EditText oldPasswordEditText;
    EditText setPasswordEditText;
    EditText ConfirmPasswordEditText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        //获取可编辑 loginStatus 的编辑器
        sharedPreferencesEditor = loginStatus.edit();

        oldPasswordEditText = (EditText) findViewById(R.id.oldPasswordEditText);
        setPasswordEditText = (EditText) findViewById(R.id.setPasswordEditText);
        ConfirmPasswordEditText = (EditText) findViewById(R.id.ConfirmPasswordEditText);

        submitButton = (Button) findViewById(R.id.submitButton);

        oldPasswordEditText.addTextChangedListener(textWatcher);
        setPasswordEditText.addTextChangedListener(textWatcher);
        ConfirmPasswordEditText.addTextChangedListener(textWatcher);

        submitButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(oldPasswordEditText.getText().toString().equals(""))
                {
                    controller.makeToast(ResetPasswordActivity.this, "请输入旧密码");
                }
                else if(setPasswordEditText.getText().toString().equals(""))
                {
                    controller.makeToast(ResetPasswordActivity.this, "请输入新密码");
                }
                else if(ConfirmPasswordEditText.getText().toString().equals(""))
                {
                    controller.makeToast(ResetPasswordActivity.this, "请确认新密码");
                }
                else if(!ConfirmPasswordEditText.getText().toString().equals(setPasswordEditText.getText().toString()))
                {
                    controller.makeToast(ResetPasswordActivity.this, "两次输入的新密码不一致");
                }
                else
                {
                    netWork.changePassword(loginStatus.getString("username", ""), oldPasswordEditText.getText().toString(), ConfirmPasswordEditText.getText().toString(), changeHandler);
                }
            }
        });
    }

    final Handler changeHandler = new Handler()
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
                        sharedPreferencesEditor.putString("password", ConfirmPasswordEditText.getText().toString());

                        sharedPreferencesEditor.commit();

                        controller.makeToast(ResetPasswordActivity.this, "更换密码成功，请重新登录");

                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(result.equals("oldpassword wrong"))
                    {
                        controller.makeToast(ResetPasswordActivity.this, "旧密码错误");
                    }
                    else if(result.equals("No account"))
                    {
                        controller.makeToast(ResetPasswordActivity.this, "账户没有注册");
                    }
                    else
                    {
                        controller.makeToast(ResetPasswordActivity.this, "更新密码失败");
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
            String oldPassword = oldPasswordEditText.getText().toString();
            String newPassword = setPasswordEditText.getText().toString();
            String confirmNewPassword = ConfirmPasswordEditText.getText().toString();

            if(oldPassword.equals("") || newPassword.equals("") || confirmNewPassword.equals(""))
            {
                submitButton.setAlpha((float) 0.6);
            }
            else
            {
                submitButton.setAlpha((float) 1.0);
            }
        }
    };
}
