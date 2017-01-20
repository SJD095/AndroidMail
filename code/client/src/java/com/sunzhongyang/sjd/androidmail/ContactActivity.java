package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ContactActivity extends Activity
{
    Controller controller = new Controller();
    boolean update = false;

    EditText nameEditText;
    EditText workEditText;
    EditText mailEditText;
    TextView saveButton;

    SharedPreferences loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_contact);

        //获取一个数据库
        SQLite tmp_db = new SQLite(this);
        final SQLiteDatabase current_db = tmp_db.get_db();

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);

        //获取一个包含数据库全部信息的游标
        final Cursor full_cursor = current_db.rawQuery("select * from contact", null);

        TextView cancelButton = (TextView) findViewById(R.id.cancelButton);
        saveButton = (TextView) findViewById(R.id.saveButton);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        workEditText = (EditText) findViewById(R.id.workEditText);
        mailEditText = (EditText) findViewById(R.id.mailEditText);

        nameEditText.addTextChangedListener(textWatcher);
        workEditText.addTextChangedListener(textWatcher);
        mailEditText.addTextChangedListener(textWatcher);

        Intent intent = getIntent();
        //获取bundle及其中的信息
        Bundle bundle = intent.getExtras();

        //如果是关联了某个联系人, 则显示可编辑信息
        if(bundle.getString("content").equals("detail"))
        {
            String name = bundle.getString("name");
            String work = bundle.getString("work");
            String address = bundle.getString("address");

            nameEditText.setText(name);
            workEditText.setText(work);
            mailEditText.setText(address);

            //设置光标位置
            Editable text = nameEditText.getText();
            nameEditText.setSelection(text.length());

            update = true;
        }

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                String name = nameEditText.getText().toString();
                String work = workEditText.getText().toString();
                String address = mailEditText.getText().toString();

                if(name.equals(""))
                {
                    controller.makeToast(ContactActivity.this, "姓名不能为空");
                }
                else if(address.equals(""))
                {
                    controller.makeToast(ContactActivity.this, "邮件不能为空");
                }
                else
                {
                    if(update)
                    {
                        ContentValues cv = new ContentValues();
                        cv.put("name", name);
                        cv.put("work", work);
                        cv.put("address", address);
                        String whereArgs[] = new String[]{name};

                        current_db.update("contact", cv, "name=?", whereArgs);
                    }
                    else
                    {
                        //通过ContentValues在数据库中增加新内容
                        ContentValues cv = new ContentValues();
                        cv.put("name", name);
                        cv.put("work", work);
                        cv.put("address", address);
                        cv.put("user", loginStatus.getString("username", ""));
                        current_db.insert("contact", null, cv);
                    }

                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                finish();
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
            String name = nameEditText.getText().toString();
            String work = workEditText.getText().toString();
            String address = mailEditText.getText().toString();

            if(name.equals("") || work.equals("") || address.equals(""))
            {
                saveButton.setTextColor(getResources().getColor(R.color.grey));
            }
            else
            {
                saveButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }
    };
}
