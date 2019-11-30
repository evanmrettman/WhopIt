package com.example.whopit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WhopIt extends AppCompatActivity
{

    Button but_return;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whop_it);
        but_return = findViewById(R.id.but_return);
        but_return.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        but_return.setClickable(false);
        but_return.setAlpha(0);
    }
}
