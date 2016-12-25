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

public class RegisterAccountName extends Activity
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

        setContentView(R.layout.activity_register_account_name);

        Button accountNameNextStep = (Button) findViewById(R.id.accountNameNextStep);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        final EditText setAccountEditText = (EditText) findViewById(R.id.setAccountEditText);
        TextView checkNameAuthentication = (TextView) findViewById(R.id.checkNameAuthentication);

        accountNameNextStep.setOnClickListener(new View.OnClickListener()
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
                else if(netWork.checkDuplicate(mailAddress))
                {
                    controller.makeToast(RegisterAccountName.this, "用户名重复");
                }
                else
                {
                    //打开输入密码页面
                    Intent intent = new Intent(RegisterAccountName.this, RegisterAccountPassword.class);
                    intent.putExtra("mailAddress", mailAddress);
                    startActivity(intent);
                    finish();
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
                else if(netWork.checkDuplicate(mailAddress))
                {
                    controller.makeToast(RegisterAccountName.this, "用户名重复");
                }
            }
        });
    }
}
