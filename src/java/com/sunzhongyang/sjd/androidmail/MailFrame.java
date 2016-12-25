package com.sunzhongyang.sjd.androidmail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MailFrame extends Fragment
{
    //存储一个弹出窗口
    private PopupWindow popupwindow;
    private PopupWindow addPopWindow;

    ListViewAdapter tmpListViewAdapter;
    BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.mail_frame, container, false);

        Button inboxSelectButton = (Button) view.findViewById(R.id.inboxSelectButton);

        ImageView categoryButton = (ImageView) view.findViewById(R.id.categoryButton);
        ImageView addButton = (ImageView) view.findViewById(R.id.addButton);
        ImageView searchButton = (ImageView) view.findViewById(R.id.searchButton);

        final DrawerLayout drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);

        categoryButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                if(addPopWindow != null && addPopWindow.isShowing())
                {
                    //收回弹出窗口
                    addPopWindow.dismiss();
                }
                else
                {
                    //创建一个弹出窗口
                    initAddPopupWindowView();

                    //显示弹出窗口
                    addPopWindow.showAsDropDown(v);
                }
            }
        });

        inboxSelectButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                if(popupwindow != null && popupwindow.isShowing())
                {
                    //收回弹出窗口
                    popupwindow.dismiss();
                }
                else
                {
                    //创建一个弹出窗口
                    initmPopupWindowView();

                    //显示弹出窗口
                    popupwindow.showAsDropDown(v);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                //打开搜索页面
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
            }
        });

        //获取ListView
        final ListView mailListView = (ListView) view.findViewById(R.id.mailListView);

        mReceiver = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                //刷新主Activity界面
                onResume();
            }
        };

        IntentFilter intentFilter = new IntentFilter("android.intent.action.refreshUI");
        getActivity().registerReceiver(mReceiver, intentFilter);

        //为ListView设置点击事件
        mailListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l)
            {
                //设置一个bundle用于传送list item的文本和图像id
                Bundle bundle = new Bundle();
                bundle.putString("content", "detail");
                bundle.putString("sender", tmpListViewAdapter.getSenderList(i));
                bundle.putString("topic", tmpListViewAdapter.getTopicList(i));
                bundle.putString("brief", tmpListViewAdapter.getBriefList(i));

                //发送广播
                Intent intent = new Intent(getActivity(), MailInformationActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //获得一个数据库
        SQLite helper = new SQLite(getActivity());
        final SQLiteDatabase current_db = helper.get_db();

        //根据数据库获取一个有数据库全部内容的游标
        Cursor full_cursor = current_db.rawQuery("select * from inbox", null);

        ArrayList<String> froms = new ArrayList<String>();
        ArrayList<String> subjects = new ArrayList<String>();
        ArrayList<String> contents = new ArrayList<String>();
        ArrayList<String> times = new ArrayList<String>();

        //将游标中数据库内的数据添加到数组中
        while (full_cursor.moveToNext())
        {
            froms.add(full_cursor.getString(1));
            subjects.add(full_cursor.getString(2));
            contents.add(full_cursor.getString(3));
            times.add(full_cursor.getString(4));
        }

        final String[] from = new String[froms.size()];
        for(int i = 0; i < from.length; i++)
        {
            from[i] = froms.get(i);
        }

        final String[] subject = new String[subjects.size()];
        for(int i = 0; i < subject.length; i++)
        {
            subject[i] = subjects.get(i);
        }

        final String[] content = new String[contents.size()];
        for(int i = 0; i < content.length; i++)
        {
            content[i] = contents.get(i);
        }

        final String[] time = new String[times.size()];
        for(int i = 0; i < time.length; i++)
        {
            time[i] = times.get(i);
        }

        //获取ListView
        ListView mailListView = (ListView) getActivity().findViewById(R.id.mailListView);

        //初始化ListView
        tmpListViewAdapter = new MailFrame.ListViewAdapter(from, subject, content);
        mailListView.setAdapter(tmpListViewAdapter);
    }

    //一个自定义的ListViewAdapter
    public class ListViewAdapter extends BaseAdapter
    {
        //用于存放所有list item的view
        View[] itemViews;

        String[] senderList = {};
        String[] topicList = {};
        String[] briefList = {};
        int indexL = 0;

        public ListViewAdapter(String[] senders, String[] topics, String[] briefs)
        {
            itemViews = new View[senders.length];
            senderList = senders;
            topicList = topics;
            briefList = briefs;

            indexL = senders.length - 1;

            //按次序初始化每个list item
            for (int i = itemViews.length - 1; i >= 0; --i)
            {
                int index = itemViews.length - 1 - i;
                itemViews[i] = makeItemView(String.valueOf(senders[index].charAt(senders[index].length() - 1)),
                        senders[index], topics[index], briefs[index]);
            }
        }

        private View makeItemView(String last, String sender, String topic, String brief)
        {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        public String getSenderList(int position)
        {
            return senderList[indexL - position];
        }

        public String getTopicList(int position)
        {
            return topicList[indexL - position];
        }

        public String getBriefList (int position)
        {
            return briefList[indexL - position];
        }
    }

    public void initmPopupWindowView()
    {

        //获取自定义布局文件 popwindow.xml 的视图
        View customView = getActivity().getLayoutInflater().inflate(R.layout.popwindow,
                null, false);

        //创建 PopupWindow 实例
        popupwindow = new PopupWindow(customView, getActivity().getWindowManager().getDefaultDisplay().getWidth(), 300);
        popupwindow.setAnimationStyle(R.style.AnimationFade);

        //使得点击 PopupWindow 外能够收回窗口
        popupwindow.setBackgroundDrawable(new BitmapDrawable());
        popupwindow.setOutsideTouchable(true);
        popupwindow.setFocusable(true);
    }

    public void initAddPopupWindowView()
    {
        //获取自定义布局文件 popwindow.xml 的视图
        View customView = getActivity().getLayoutInflater().inflate(R.layout.add_popwindow,
                null, false);

        addPopWindow = new PopupWindow(customView, 400, 300);

        //使得点击 PopupWindow 外能够收回窗口
        addPopWindow.setBackgroundDrawable(new BitmapDrawable());
        addPopWindow.setOutsideTouchable(true);
        addPopWindow.setFocusable(true);

        LinearLayout newMailLinearLayout = (LinearLayout) customView.findViewById(R.id.newMailLinearLayout);
        LinearLayout newContactLinearLayout = (LinearLayout) customView.findViewById(R.id.newContactLinearLayout);

        newMailLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                //打开写邮件页面
                Intent intent = new Intent(getActivity(), WriteMail.class);
                startActivity(intent);
                addPopWindow.dismiss();
            }
        });

        newContactLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                //打开新联系人页面
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
                addPopWindow.dismiss();
            }
        });
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
