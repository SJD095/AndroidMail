package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //获取自上一页面传来的用户名
        Bundle bundle = getIntent().getExtras();
        final String mailAddress = bundle.getString("mailAddress");

        setContentView(R.layout.activity_register_account_password);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        TextView mailAddressTextView = (TextView) findViewById(R.id.mailAddressTextView);
        final EditText setPasswordEditText = (EditText) findViewById(R.id.setPasswordEditText);
        final EditText ConfiemPasswordEditText = (EditText) findViewById(R.id.ConfiemPasswordEditText);
        Button passwordNextStep = (Button) findViewById(R.id.passwordNextStep);

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
                else if(netWork.checkDuplicate(mailAddress))
                {
                    controller.makeToast(RegisterAccountPassword.this, "用户名重复,请重新申请");
                }
                else
                {
                    if(netWork.registerUser(mailAddress, confirmPassword))
                    {
                        //打开主页面
                        Intent intent = new Intent(RegisterAccountPassword.this, MainPage.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        controller.makeToast(RegisterAccountPassword.this, "注册失败,请检查网络");
                    }
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
}
