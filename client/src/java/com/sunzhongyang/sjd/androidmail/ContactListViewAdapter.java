package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Random;

//一个自定义的ListViewAdapter
public class ContactListViewAdapter extends BaseAdapter
{
    //用于存放所有list item的view
    View[] itemViews;
    String[] contactName;
    String[] contactWork;
    String[] contactAddresses;

    public ContactListViewAdapter(String[] names, String[] works, String[] mailAddresses, Activity activity)
    {
        itemViews = new View[names.length];
        contactName = names;
        contactWork = works;
        contactAddresses = mailAddresses;

        //按次序初始化每个list item
        for (int i = 0; i < itemViews.length; ++i) {
            itemViews[i] = makeItemView(String.valueOf(names[i].charAt(names[i].length() - 1)),
                    names[i], activity);
            Log.i("test", names[i]);
        }
    }

    private View makeItemView(String last, String fullName, Activity activity)
    {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    public String getNameList(int position)
    {
        return contactName[position];
    }

    public String getWorkList(int position)
    {
        return contactWork[position];
    }

    public String getAddressList(int position)
    {
        return contactAddresses[position];
    }
}