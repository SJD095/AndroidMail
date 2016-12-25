package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ContactActivity extends Activity
{
    Controller controller = new Controller();

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

        //获取一个包含数据库全部信息的游标
        final Cursor full_cursor = current_db.rawQuery("select * from contact", null);

        TextView cancelButton = (TextView) findViewById(R.id.cancelButton);
        TextView saveButton = (TextView) findViewById(R.id.saveButton);

        final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        final EditText workEditText = (EditText) findViewById(R.id.workEditText);
        final EditText mailEditText = (EditText) findViewById(R.id.mailEditText);

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
                    //通过ContentValues在数据库中增加新内容
                    ContentValues cv = new ContentValues();
                    cv.put("name", name);
                    cv.put("work", work);
                    cv.put("address", address);
                    current_db.insert("contact", null, cv);

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
}
