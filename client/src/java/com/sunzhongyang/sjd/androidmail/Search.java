package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Search extends Activity
{
    EditText searchEditText;
    Button cancelButton;
    Bundle bundle;
    MailListViewAdapter mailListViewAdapter;
    ContactListViewAdapter contactListViewAdapter;
    SharedPreferences loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = getSharedPreferences("loginStatus", Context.MODE_PRIVATE);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        //获取bundle及其中的信息
        bundle = intent.getExtras();

        cancelButton = (Button) findViewById(R.id.cancelButton);
        searchEditText = (EditText) findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(textWatcher);
        final ListView searchListView = (ListView) findViewById(R.id.searchListView);

        //为ListView设置点击事件
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l)
            {

                if(bundle.getString("content").equals("mail"))
                {
                    //设置一个bundle用于传送list item的文本和图像id
                    Bundle bundle = new Bundle();
                    bundle.putString("content", "detail");
                    bundle.putInt("id", mailListViewAdapter.getIdList(i));
                    bundle.putString("mailbox", "inbox");

                    //发送广播
                    Intent intent = new Intent(Search.this, MailInformationActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(bundle.getString("content").equals("contact"))
                {
                    //设置一个bundle用于传送list item的文本和图像id
                    Bundle bundle = new Bundle();
                    bundle.putString("content", "detail");
                    bundle.putString("name", contactListViewAdapter.getNameList(i));
                    bundle.putString("work", contactListViewAdapter.getWorkList(i));
                    bundle.putString("address", contactListViewAdapter.getAddressList(i));

                    //发送广播
                    Intent intent = new Intent(Search.this, ContactInfomationActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 cancelButton 执行的动作
            @Override
            public void onClick(View v)
            {
                if(cancelButton.getText().toString().equals("搜索"))
                {
                    if(bundle.getString("content").equals("mail"))
                    {
                        //获取一个数据库
                        SQLite tmp_db = new SQLite(Search.this);
                        final SQLiteDatabase current_db = tmp_db.get_db();

                        //获取一个包含数据库全部信息的游标
                        final Cursor full_cursor = current_db.rawQuery("select * from inbox", null);

                        ArrayList<Integer> ids = new ArrayList<Integer>();
                        ArrayList<String> froms = new ArrayList<String>();
                        ArrayList<String> subjects = new ArrayList<String>();
                        ArrayList<String> contents = new ArrayList<String>();
                        ArrayList<String> times = new ArrayList<String>();

                        String searchContent = searchEditText.getText().toString();

                        //将游标中数据库内的数据添加到数组中
                        while (full_cursor.moveToNext())
                        {
                            if((full_cursor.getString(1).equals(searchContent) || full_cursor.getString(3).equals(searchContent)) && full_cursor.getString(6).equals(loginStatus.getString("username", "none")))
                            {
                                ids.add(full_cursor.getInt(0));
                                froms.add(full_cursor.getString(1));
                                subjects.add(full_cursor.getString(3));
                                contents.add(full_cursor.getString(4));
                                times.add(full_cursor.getString(5));
                            }
                        }

                        Log.i("ids", froms.size()+"");

                        MailItem[] mailItems = new MailItem[ids.size()];
                        for(int i = 0; i < froms.size(); i++)
                        {
                            mailItems[i] = new MailItem();
                        }

                        for(int i = 0; i < froms.size(); i++)
                        {
                            mailItems[i].id = ids.get(i);
                            mailItems[i].sender = froms.get(i);
                            mailItems[i].receiver = froms.get(i);
                            mailItems[i].topic = subjects.get(i);
                            mailItems[i].mailContent = contents.get(i);
                            mailItems[i].time = times.get(i);
                        }

                        //初始化ListView
                        mailListViewAdapter = new MailListViewAdapter(mailItems, Search.this);
                        searchListView.setAdapter(mailListViewAdapter);

                        current_db.close();
                    }
                    else if(bundle.getString("content").equals("contact"))
                    {
                        //获取一个数据库
                        SQLite tmp_db = new SQLite(Search.this);
                        final SQLiteDatabase current_db = tmp_db.get_db();

                        //获取一个包含数据库全部信息的游标
                        final Cursor full_cursor = current_db.rawQuery("select * from contact", null);

                        ArrayList<String> names = new ArrayList<String>();
                        final ArrayList<String> works = new ArrayList<String>();
                        final ArrayList<String> mailAddresses = new ArrayList<String>();

                        String searchContent = searchEditText.getText().toString();

                        //将游标中数据库内的数据添加到数组中
                        while (full_cursor.moveToNext())
                        {
                            if(full_cursor.getString(4).equals(loginStatus.getString("username", "")) && full_cursor.getString(1).equals(searchContent) || full_cursor.getString(2).equals(searchContent))
                            {
                                names.add(full_cursor.getString(1));
                                works.add(full_cursor.getString(2));
                                mailAddresses.add(full_cursor.getString(3));
                            }
                        }

                        final String[] name = new String[names.size()];
                        for(int i = 0; i < name.length; i++)
                        {
                            name[i] = names.get(i);
                        }

                        final String[] work = new String[names.size()];
                        for(int i = 0; i < name.length; i++)
                        {
                            work[i] = works.get(i);
                        }

                        final String[] mailAddress = new String[names.size()];
                        for(int i = 0; i < name.length; i++)
                        {
                            mailAddress[i] = mailAddresses.get(i);
                        }

                        contactListViewAdapter = new ContactListViewAdapter(name, work, mailAddress, Search.this);
                        searchListView.setAdapter(contactListViewAdapter);

                        current_db.close();
                    }
                }
                else
                {
                    finish();;
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
            String searchContent = searchEditText.getText().toString();
            if(searchContent.equals(""))
            {
                cancelButton.setText("取消");
            }
            else
            {
                cancelButton.setText("搜索");
            }
        }
    };
}
