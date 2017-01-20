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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RegisterAccountPassword extends Activity
{
    //获取内容控制单元
    Controller controller = new Controller();
    //获取网络访问单元
    NetWork netWork = new NetWork();
    EditText setPasswordEditText;
    EditText ConfiemPasswordEditText;
    Button passwordNextStep;

    SharedPreferences loginStatus;
    SharedPreferences.Editor sharedPreferencesEditor;

    String mailAddress;

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


        //获取自上一页面传来的用户名
        Bundle bundle = getIntent().getExtras();
        mailAddress = bundle.getString("mailAddress");

        setContentView(R.layout.activity_register_account_password);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        TextView mailAddressTextView = (TextView) findViewById(R.id.mailAddressTextView);
        setPasswordEditText = (EditText) findViewById(R.id.setPasswordEditText);
        ConfiemPasswordEditText = (EditText) findViewById(R.id.ConfiemPasswordEditText);
        passwordNextStep = (Button) findViewById(R.id.passwordNextStep);

        setPasswordEditText.addTextChangedListener(textWatcher);
        ConfiemPasswordEditText.addTextChangedListener(textWatcher);

        //显示正在注册的用户名
        mailAddressTextView.setText(mailAddress + "@sunzhongyang.com");

        passwordNextStep.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                //获取密码
                String originalPassword = setPasswordEditText.getText().toString();
                String confirmPassword = ConfiemPasswordEditText.getText().toString();

                if(originalPassword.equals("") || confirmPassword.equals(""))
                {
                    controller.makeToast(RegisterAccountPassword.this, "密码不能为空");
                }
                else if(!originalPassword.equals(confirmPassword))
                {
                    controller.makeToast(RegisterAccountPassword.this, "两次输入的密码不一致");
                }
                else
                {
                    netWork.registerUser(mailAddress, confirmPassword, registerHandler);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                //终止当前活动,返回上一级
                finish();
            }
        });
    }

    final Handler registerHandler = new Handler()
    {
        public void handleMessage(Message message)
        {
            switch (message.what) {
                //根据服务器返回消息类型决定对应的操作
                case 0:
                    String result = message.obj.toString();
                    if(result.equals("OK"))
                    {
                        sharedPreferencesEditor.putString("username", mailAddress);
                        sharedPreferencesEditor.putString("password", ConfiemPasswordEditText.getText().toString());

                        sharedPreferencesEditor.commit();

                        //打开主页面
                        Intent intent = new Intent(RegisterAccountPassword.this, MainPage.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(result.equals("User exist"))
                    {
                        controller.makeToast(RegisterAccountPassword.this, "用户名重复,请重新申请");
                    }
                    else
                    {
                        controller.makeToast(RegisterAccountPassword.this, "注册失败,请检查网络");
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
            String set = setPasswordEditText.getText().toString();
            String confirm = ConfiemPasswordEditText.getText().toString();
            if(set.equals("") || confirm.equals(""))
            {
                passwordNextStep.setAlpha((float) 0.6);
            }
            else
            {
                passwordNextStep.setAlpha((float) 1.0);
            }
        }
    };
}
