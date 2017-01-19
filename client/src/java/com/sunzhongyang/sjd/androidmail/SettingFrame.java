package com.sunzhongyang.sjd.androidmail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingFrame extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.setting_frame, container, false);

        //获取保存有登陆用户信息的 SharedPreferences 对象
        SharedPreferences loginStatus = getActivity().getSharedPreferences("loginStatus", Context.MODE_PRIVATE);
        //获取可编辑 loginStatus 的编辑器
        final SharedPreferences.Editor editor = loginStatus.edit();

        TextView mailInformTextView = (TextView) view.findViewById(R.id.mailInformTextView);
        TextView refreshTextView = (TextView) view.findViewById(R.id.refreshTextView);
        TextView logoutTextView = (TextView) view.findViewById(R.id.logoutTextView);
        TextView resetTextView = (TextView) view.findViewById(R.id.resetTextView);

        resetTextView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        mailInformTextView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), MailInformActivity.class);
                startActivity(intent);
            }
        });

        refreshTextView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), RefreshRateActivity.class);
                startActivity(intent);
            }
        });

        logoutTextView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                editor.putString("username", "");
                editor.putString("password", "");

                editor.commit();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        return view;
    }
}
