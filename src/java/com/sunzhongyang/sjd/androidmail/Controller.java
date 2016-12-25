package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by SJD on 12/17/16.
 */

public class Controller
{
    public String sendMailURL = "http://sunzhongyang.com:7001/send";
    public String receiveMailURL = "http://sunzhongyang.com:7001/check";

    public void makeToast(Activity activity, String toastContent)
    {
        Toast.makeText(activity, toastContent, Toast.LENGTH_SHORT).show();
    }
}
