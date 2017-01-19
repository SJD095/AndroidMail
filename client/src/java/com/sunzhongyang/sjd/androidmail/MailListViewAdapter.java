package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Random;

//一个自定义的ListViewAdapter
public class MailListViewAdapter extends BaseAdapter
{
    //用于存放所有list item的view
    View[] itemViews;

    MailItem[] mails;
    int indexL = 0;

    public MailListViewAdapter(MailItem[] mailItems, Activity activity)
    {
        itemViews = new View[mailItems.length];
        mails = mailItems;
        indexL = mailItems.length - 1;

        //按次序初始化每个list item
        for (int i = itemViews.length - 1; i >= 0; --i)
        {
            int index = itemViews.length - 1 - i;
            itemViews[i] = makeItemView(String.valueOf(mailItems[index].topic.charAt(0)),
                    mailItems[index].sender, mailItems[index].topic, mailItems[index].mailContent, activity);
        }
    }

    private View makeItemView(String last, String sender, String topic, String brief, Activity activity)
    {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 使用View的对象itemView与R.layout.item关联
        View itemView = inflater.inflate(R.layout.mail_layout, null);

        //初始化某个list item的描述信息
        TextView lastChar = (TextView)itemView.findViewById(R.id.lastChar);

        lastChar.setText(last);

        int randomColor = Color.parseColor(getRandColor());
        lastChar.setTextColor(randomColor);
        GradientDrawable bgShape = (GradientDrawable) lastChar.getBackground();
        bgShape.setStroke(1, randomColor);

        //初始化某个list item的
        TextView senderTextView = (TextView)itemView.findViewById(R.id.sender);

        senderTextView.setText(sender);

        //初始化某个list item的
        TextView topicTextView = (TextView)itemView.findViewById(R.id.topic);

        topicTextView.setText(topic);

        //初始化某个list item的
        TextView briefTextView = (TextView)itemView.findViewById(R.id.brief);

        briefTextView.setText(brief);

        return itemView;
    }

    //返回当前ListView中item的数目
    public int getCount()
    {
        return itemViews.length;
    }

    //获取某个位置的list item
    public View getItem(int position)
    {
        return itemViews[position];
    }

    //获取某一个位置的list item的id
    public long getItemId(int position)
    {
        return position;
    }

    //获取某一个位置的list item的view
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return itemViews[position];
    }

    public int getIdList(int position)
    {
        return mails[indexL - position].id;
    }

    public String getSenderList(int position)
    {
        return mails[indexL - position].sender;
    }

    public String getTopicList(int position)
    {
        return mails[indexL - position].topic;
    }

    public String getBriefList(int position)
    {
        return mails[indexL - position].mailContent;
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