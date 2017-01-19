package com.sunzhongyang.sjd.androidmail;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

public class MailFrame extends Fragment
{
    Controller controller = new Controller();

    //存储一个弹出窗口
    private PopupWindow popupwindow;
    private PopupWindow addPopWindow;

    MailListViewAdapter tmpMailListViewAdapter;
    BroadcastReceiver mReceiver;

    SharedPreferences loginStatus;
    SharedPreferences.Editor editor;

    LinearLayout receiveboxLinearLayout;
    LinearLayout sendboxLinearLayout;
    LinearLayout rubbishboxLinearLayout;

    ImageView choiceImageView;

    String mailBox = "inbox";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.mail_frame, container, false);

        final Button inboxSelectButton = (Button) view.findViewById(R.id.inboxSelectButton);

        ImageView categoryButton = (ImageView) view.findViewById(R.id.categoryButton);
        ImageView addButton = (ImageView) view.findViewById(R.id.addButton);
        ImageView searchButton = (ImageView) view.findViewById(R.id.searchButton);

        receiveboxLinearLayout = (LinearLayout) view.findViewById(R.id.receiveBoxLinearLayout);
        sendboxLinearLayout = (LinearLayout) view.findViewById(R.id.sendBoxLinearLayout);
        rubbishboxLinearLayout = (LinearLayout) view.findViewById(R.id.rubbishBoxLinearLayout);

        choiceImageView = (ImageView) view.findViewById(R.id.choiceImageView);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        loginStatus = getActivity().getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        editor = loginStatus.edit();

        final DrawerLayout drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);

        receiveboxLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                mailBox = "inbox";
                resetBackgroundColorofDrawerItems();
                choiceImageView.setVisibility(View.VISIBLE);

                receiveboxLinearLayout.setBackgroundColor(getResources().getColor(R.color.colorAccentAlpha));
                inboxSelectButton.setText("收件箱");

                onResume();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        sendboxLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                mailBox = "outbox";
                resetBackgroundColorofDrawerItems();

                sendboxLinearLayout.setBackgroundColor(getResources().getColor(R.color.colorAccentAlpha));
                inboxSelectButton.setText("发件箱");

                onResume();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        rubbishboxLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                mailBox = "trashbox";
                resetBackgroundColorofDrawerItems();

                rubbishboxLinearLayout.setBackgroundColor(getResources().getColor(R.color.colorAccentAlpha));
                inboxSelectButton.setText("垃圾箱");

                onResume();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

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
                if(mailBox.equals("inbox"))
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
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 inboxSelectButton 执行的动作
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                bundle.putString("content", "mail");

                //打开搜索页面
                Intent intent = new Intent(getActivity(), Search.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //获取ListView
        final ListView mailListView = (ListView) view.findViewById(R.id.mailListView);

        mReceiver = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {

                if(loginStatus.getString("mailinform", "true").equals("true"))
                {
                    //振动一次
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    long [] pattern = {100, 400, 100, 400};
                    vibrator.vibrate(pattern, -1);
                }

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
                //获得一个数据库
                SQLite helper = new SQLite(getActivity());
                SQLiteDatabase current_db = helper.get_db();

                //设置一个bundle用于传送list item的文本和图像id
                Bundle bundle = new Bundle();
                bundle.putString("content", "detail");
                bundle.putInt("id", tmpMailListViewAdapter.getIdList(i));
                bundle.putString("mailbox", mailBox);

                ContentValues cv = new ContentValues();
                cv.put("status", "read");
                String whereArgs[] = new String[]{tmpMailListViewAdapter.getIdList(i) + ""};

                current_db.update("inbox", cv, "id=?", whereArgs);

                //发送广播
                Intent intent = new Intent(getActivity(), MailInformationActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                current_db.close();
            }
        });

        mailListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, final int i, long l)
            {
                //用count替换 i 以避免与其他使用 i 的函数混淆
                final int count = i;

                final SQLite tmp_db = new SQLite(getActivity());

                //弹出一个提示框
                new AlertDialog.Builder(getActivity())
                        .setTitle("是否删除")
                        //点击确定按钮则弹框提示确定按钮被按下
                        .setPositiveButton("是", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j)
                            {
                                //获得一个数据库
                                SQLite helper = new SQLite(getActivity());
                                final SQLiteDatabase current_db = helper.get_db();

                                if(mailBox.equals("trashbox"))
                                {
                                    tmp_db.deleteMail(tmpMailListViewAdapter.getIdList(i), current_db, mailBox);

                                    onResume();
                                }
                                else
                                {
                                    Cursor full_cursor = current_db.rawQuery("select * from " + mailBox, null);

                                    MailItem mail = tmp_db.getMail(tmpMailListViewAdapter.getIdList(i), full_cursor);;

                                    tmp_db.insertMail(mail, current_db, "trashbox");
                                    tmp_db.deleteMail(tmpMailListViewAdapter.getIdList(i), current_db, mailBox);

                                    onResume();
                                }

                                current_db.close();
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

        editor.putString("readstatus", "all");
        editor.commit();

        //获得一个数据库
        SQLite helper = new SQLite(getActivity());
        SQLiteDatabase current_db = helper.get_db();

        //根据数据库获取一个有数据库全部内容的游标
        Cursor full_cursor = current_db.rawQuery("select * from " + mailBox, null);

        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<String> froms = new ArrayList<String>();
        ArrayList<String> subjects = new ArrayList<String>();
        ArrayList<String> contents = new ArrayList<String>();
        ArrayList<String> times = new ArrayList<String>();

        //将游标中数据库内的数据添加到数组中
        while (full_cursor.moveToNext())
        {
            if(full_cursor.getString(6).equals(loginStatus.getString("username", "none")))
            {
                ids.add(full_cursor.getInt(0));
                froms.add(full_cursor.getString(1));
                subjects.add(full_cursor.getString(3));
                contents.add(full_cursor.getString(4));
                times.add(full_cursor.getString(5));
            }
        }

        MailItem[] mailItems = new MailItem[ids.size()];

        for(int i = 0; i < ids.size(); i ++)
        {
            mailItems[i] = new MailItem();
        }

        for(int i = 0; i < froms.size(); i++)
        {
            mailItems[i].id = ids.get(i);
            mailItems[i].sender = froms.get(i);
            mailItems[i].topic = subjects.get(i);
            mailItems[i].mailContent = contents.get(i);
            mailItems[i].time = times.get(i);
        }

        //获取ListView
        ListView mailListView = (ListView) getActivity().findViewById(R.id.mailListView);

        //初始化ListView
        tmpMailListViewAdapter = new MailListViewAdapter(mailItems, getActivity());
        mailListView.setAdapter(tmpMailListViewAdapter);

        current_db.close();
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

        final ImageView allImageView = (ImageView) customView.findViewById(R.id.allImageView);
        final ImageView unreadImageView = (ImageView) customView.findViewById(R.id.unreadImageView);

        LinearLayout allLayout = (LinearLayout) customView.findViewById(R.id.allLayout);
        LinearLayout unreadLayout = (LinearLayout) customView.findViewById(R.id.unreadLayout);

        if(loginStatus.getString("readstatus", "all").equals("all"))
        {
            allImageView.setVisibility(View.VISIBLE);
            unreadImageView.setVisibility(View.INVISIBLE);
        }
        else
        {
            allImageView.setVisibility(View.INVISIBLE);
            unreadImageView.setVisibility(View.VISIBLE);
        }

        allLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 allLayout 执行的动作
            @Override
            public void onClick(View v)
            {
                editor.putString("readstatus", "all");
                editor.commit();

                onResume();

                popupwindow.dismiss();
            }
        });

        unreadLayout.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 unreadLayout 执行的动作
            @Override
            public void onClick(View v)
            {
                editor.putString("readstatus", "unread");
                editor.commit();

                //获得一个数据库
                SQLite helper = new SQLite(getActivity());
                SQLiteDatabase current_db = helper.get_db();

                //根据数据库获取一个有数据库全部内容的游标
                Cursor full_cursor = current_db.rawQuery("select * from " + "inbox", null);

                ArrayList<Integer> ids = new ArrayList<Integer>();
                ArrayList<String> froms = new ArrayList<String>();
                ArrayList<String> subjects = new ArrayList<String>();
                ArrayList<String> contents = new ArrayList<String>();
                ArrayList<String> times = new ArrayList<String>();

                //将游标中数据库内的数据添加到数组中
                while (full_cursor.moveToNext())
                {
                    if(full_cursor.getString(6).equals(loginStatus.getString("username", "none")) && full_cursor.getString(7).equals("unread"))
                    {
                        ids.add(full_cursor.getInt(0));
                        froms.add(full_cursor.getString(1));
                        subjects.add(full_cursor.getString(3));
                        contents.add(full_cursor.getString(4));
                        times.add(full_cursor.getString(5));
                    }
                }

                MailItem[] mailItems = new MailItem[ids.size()];

                for(int i = 0; i < ids.size(); i ++)
                {
                    mailItems[i] = new MailItem();
                }

                for(int i = 0; i < froms.size(); i++)
                {
                    mailItems[i].id = ids.get(i);
                    mailItems[i].sender = froms.get(i);
                    mailItems[i].topic = subjects.get(i);
                    mailItems[i].mailContent = contents.get(i);
                    mailItems[i].time = times.get(i);
                }

                //获取ListView
                ListView mailListView = (ListView) getActivity().findViewById(R.id.mailListView);

                //初始化ListView
                tmpMailListViewAdapter = new MailListViewAdapter(mailItems, getActivity());
                mailListView.setAdapter(tmpMailListViewAdapter);

                popupwindow.dismiss();
                current_db.close();
            }
        });
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
                Bundle bundle = new Bundle();
                bundle.putString("content", "none");

                //打开写邮件页面
                Intent intent = new Intent(getActivity(), WriteMail.class);
                intent.putExtras(bundle);
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
                Bundle bundle = new Bundle();
                bundle.putString("content", "none");

                //打开新联系人页面
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                addPopWindow.dismiss();
            }
        });
    }

    public void resetBackgroundColorofDrawerItems()
    {
        choiceImageView.setVisibility(View.INVISIBLE);
        receiveboxLinearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        sendboxLinearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        rubbishboxLinearLayout.setBackgroundColor(getResources().getColor(R.color.white));
    }
}
