package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Intent;
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

import org.json.JSONTokener;

public class RegisterAccountName extends Activity
{
    //获取内容控制单元
    Controller controller = new Controller();
    //获取网络访问单元
    NetWork netWork = new NetWork();
    String mailAddress;
    boolean Next = false;

    EditText setAccountEditText;
    Button accountNameNextStep;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register_account_name);

        accountNameNextStep = (Button) findViewById(R.id.accountNameNextStep);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        setAccountEditText = (EditText) findViewById(R.id.setAccountEditText);
        TextView checkNameAuthentication = (TextView) findViewById(R.id.checkNameAuthentication);

        //监听 EditText 变化, 根据情况设置按钮样式
        setAccountEditText.addTextChangedListener(textWatcher);

        accountNameNextStep.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                //获取 setAccountEditText 中的用户名
                mailAddress = setAccountEditText.getText().toString();

                if(mailAddress.equals(""))
                {
                    controller.makeToast(RegisterAccountName.this, "用户名不能为空");
                }
                else
                {
                    Next = true;
                    netWork.checkDuplicate(mailAddress, checkHandler);
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

        checkNameAuthentication.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                //获取 setAccountEditText 中的用户名
                String mailAddress = setAccountEditText.getText().toString();

                if(mailAddress.equals(""))
                {
                    controller.makeToast(RegisterAccountName.this, "用户名不能为空");
                }
                else
                {
                    Next = false;
                    netWork.checkDuplicate(mailAddress, checkHandler);
                }
            }
        });
    }

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
                        if(Next)
                        {
                            //打开输入密码页面
                            Intent intent = new Intent(RegisterAccountName.this, RegisterAccountPassword.class);
                            intent.putExtra("mailAddress", mailAddress);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            controller.makeToast(RegisterAccountName.this, "这个用户名可以使用");
                        }
                    }
                    else
                    {
                        controller.makeToast(RegisterAccountName.this, "用户名重复");
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
            String str = setAccountEditText.getText().toString();
            if(str.equals(""))
            {
                accountNameNextStep.setAlpha((float) 0.6);
            }
            else
            {
                accountNameNextStep.setAlpha((float) 1.0);
            }
        }
    };
}
