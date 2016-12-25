package com.sunzhongyang.sjd.androidmail;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ContactInfomationActivity extends Activity
{

    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //不显示标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_contact_infomation);


        Intent intent=getIntent();

        //获取bundle及其中的信息
        Bundle bundle = intent.getExtras();

        ImageView backButton = (ImageView) findViewById(R.id.backButton);

        TextView editButton = (TextView) findViewById(R.id.editButton);
        TextView mailAddress = (TextView) findViewById(R.id.mailAddress);
        TextView workTextView = (TextView) findViewById(R.id.workTextView);
        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);

        ImageView sendButton = (ImageView) findViewById(R.id.sendButton);

        if(bundle.getString("content").equals("detail"))
        {
            String name = bundle.getString("name");
            String work = bundle.getString("work");
            address = bundle.getString("address");

            nameTextView.setText(name);
            workTextView.setText(work);
            mailAddress.setText(address);
        }

        backButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ContactInfomationActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            //重写onClick,规定点击 registerAccount 执行的动作
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("content", "detail");
                bundle.putString("address", address);

                Intent intent = new Intent(ContactInfomationActivity.this, WriteMail.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
