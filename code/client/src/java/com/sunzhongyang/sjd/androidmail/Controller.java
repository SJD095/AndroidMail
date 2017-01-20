package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by SJD on 12/17/16.
 */

public class Controller
{
    public String sendMailURL = "http://sunzhongyang.com:7001/send";
    public String receiveMailURL = "http://sunzhongyang.com:7001/check";

    //显示一个通知
    public void makeToast(Activity activity, String toastContent)
    {
        Toast.makeText(activity, toastContent, Toast.LENGTH_SHORT).show();
    }
}
