package com.sunzhongyang.sjd.androidmail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class ContactFrame extends Fragment
{
    SharedPreferences loginStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.contact_frame, container, false);

        ImageView addContactButton = (ImageView) view.findViewById(R.id.addContactButton);
        ImageView searchButton = (ImageView) view.findViewById(R.id.searchButton);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = getActivity().getSharedPreferences("loginStatus", Context.MODE_PRIVATE);

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("content", "contact");

                //打开新建联系人页面
                Intent intent = new Intent(getActivity(), Search.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        addContactButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("content", "none");

                //打开新建联系人页面
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //获得一个数据库
        SQLite helper = new SQLite(getActivity());
        final SQLiteDatabase current_db = helper.get_db();

        //根据数据库获取一个有数据库全部内容的游标
        Cursor full_cursor = current_db.rawQuery("select * from contact", null);
        ArrayList<String> names = new ArrayList<String>();
        final ArrayList<String> works = new ArrayList<String>();
        final ArrayList<String> mailAddresses = new ArrayList<String>();

        //将游标中数据库内的数据添加到数组中
        while (full_cursor.moveToNext()) {
            if(full_cursor.getString(4).equals(loginStatus.getString("username", "")))
            {
                names.add(full_cursor.getString(1));
                works.add(full_cursor.getString(2));
                mailAddresses.add(full_cursor.getString(3));
            }
        }

        final String[] name = new String[names.size()];
        for (int i = 0; i < name.length; i++) {
            name[i] = names.get(i);
        }

        ListView contactListView = (ListView) getActivity().findViewById(R.id.contactListView);

        contactListView.setAdapter(new ContactListViewAdapter(name, null, null, getActivity()));

        //为ListView设置点击事件
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {
                //设置一个bundle用于传送list item的文本和图像id
                Bundle bundle = new Bundle();
                bundle.putString("content", "detail");
                bundle.putString("name", name[i]);
                bundle.putString("work", works.get(i));
                bundle.putString("address", mailAddresses.get(i));

                //发送广播
                Intent intent = new Intent(getActivity(), ContactInfomationActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //为ListView的每个Item设置长按动作
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, final int i, long l) {
                //用count替换 i 以避免与其他使用 i 的函数混淆
                final int count = i;

                //弹出一个提示框
                new AlertDialog.Builder(getActivity())
                        .setTitle("是否删除")
                        //点击确定按钮则弹框提示确定按钮被按下
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                current_db.execSQL("delete from contact where name='" + name[i].toString() + "'");

                                onResume();
                            }
                        })
                        //点击取消按钮则提示取消按钮被按下
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {

                            }
                        })
                        .create()
                        .show();

                return true;
            }
        });
    }
}
