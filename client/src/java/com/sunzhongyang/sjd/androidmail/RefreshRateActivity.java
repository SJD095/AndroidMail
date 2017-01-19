package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class RefreshRateActivity extends Activity
{
    Controller controller = new Controller();
    EditText refreshrateEdittext;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_refresh_rate);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        SharedPreferences loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        //获取可编辑 loginStatus 的编辑器
        final SharedPreferences.Editor editor = loginStatus.edit();

        refreshrateEdittext = (EditText) findViewById(R.id.refreshrateEdittext);
        saveButton = (Button) findViewById(R.id.saveButton);

        refreshrateEdittext.addTextChangedListener(textWatcher);

        saveButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(refreshrateEdittext.getText().toString().equals(""))
                {
                    controller.makeToast(RefreshRateActivity.this, "输入不能为空");
                }
                else
                {
                    editor.putInt("frequency", Integer.parseInt(refreshrateEdittext.getText().toString()));
                    editor.commit();
                    
                    controller.makeToast(RefreshRateActivity.this, "保存成功");
                }
            }
        });
    }

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
            String rate = refreshrateEdittext.getText().toString();
            if(rate.equals(""))
            {
                saveButton.setAlpha((float) 0.6);
            }
            else
            {
                saveButton.setAlpha((float) 1.0);
            }
        }
    };
}
