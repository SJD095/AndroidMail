package com.sunzhongyang.sjd.androidmail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.contact_frame, container, false);

        ImageView addContactButton = (ImageView) view.findViewById(R.id.addContactButton);

        addContactButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                //打开新建联系人页面
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
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
        while (full_cursor.moveToNext())
        {
            names.add(full_cursor.getString(1));
            works.add(full_cursor.getString(2));
            mailAddresses.add(full_cursor.getString(3));
        }

        final String[] name = new String[names.size()];
        for(int i = 0; i < name.length; i++)
        {
            name[i] = names.get(i);
        }

        ListView contactListView = (ListView) getActivity().findViewById(R.id.contactListView);

        contactListView.setAdapter(new ListViewAdapter(name));

        //为ListView设置点击事件
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l)
            {
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
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, final int i, long l)
            {
                //用count替换 i 以避免与其他使用 i 的函数混淆
                final int count = i;

                //弹出一个提示框
                new AlertDialog.Builder(getActivity())
                        .setTitle("是否删除")
                        //点击确定按钮则弹框提示确定按钮被按下
                        .setPositiveButton("是", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j)
                            {
                                current_db.execSQL("delete from contact where name='" + name[i].toString() + "'");

                                onResume();
                            }
                        })
                        //点击取消按钮则提示取消按钮被按下
                        .setNegativeButton("否", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j)
                            {

                            }
                        })
                        .create()
                        .show();

                return true;
            }
        });
    }


    //一个自定义的ListViewAdapter
    public class ListViewAdapter extends BaseAdapter
    {
        //用于存放所有list item的view
        View[] itemViews;

        public ListViewAdapter(String[] names)
        {
            itemViews = new View[names.length];

            //按次序初始化每个list item
            for (int i = 0; i < itemViews.length; ++i) {
                itemViews[i] = makeItemView(String.valueOf(names[i].charAt(names[i].length() - 1)),
                        names[i]);
                Log.i("test", names[i]);
            }
        }

        private View makeItemView(String last, String fullName)
        {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 使用View的对象itemView与R.layout.item关联
            View itemView = inflater.inflate(R.layout.contact_layout, null);

            //初始化某个list item的描述信息
            TextView lastChar = (TextView)itemView.findViewById(R.id.lastChar);

            lastChar.setText(last);

            int randomColor = Color.parseColor(getRandColor());
            lastChar.setTextColor(randomColor);
            GradientDrawable bgShape = (GradientDrawable) lastChar.getBackground();
            bgShape.setStroke(1, randomColor);

            //初始化某个list item的图像
            TextView contactName = (TextView)itemView.findViewById(R.id.contactName);

            contactName.setText(fullName);

            return itemView;
        }

        //返回当前ListView中item的数目
        public int getCount() {
            return itemViews.length;
        }

        //获取某个位置的list item
        public View getItem(int position) {
            return itemViews[position];
        }

        //获取某一个位置的list item的id
        public long getItemId(int position) {
            return position;
        }

        //获取某一个位置的list item的view
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return itemViews[position];
        }
    }

    public String getRandColor()
    {
        String r, g, b;

        Random random = new Random();

        r = Integer.toHexString(random.nextInt(32 + 32)).toUpperCase();
        g = Integer.toHexString(random.nextInt(128) + 64).toUpperCase();
        b = Integer.toHexString(random.nextInt(192) + 64).toUpperCase();

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;

        return "#" + r + g + b;
    }
}
