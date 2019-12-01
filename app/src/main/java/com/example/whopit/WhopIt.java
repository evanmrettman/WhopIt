package com.example.whopit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class WhopIt extends AppCompatActivity
{

    Button but_return;
    Integer score;

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
                returnScore();
                finish();
            }
        });
        //but_return.setClickable(false);
        //but_return.setAlpha(0);
    }

    private void returnScore()
    {

        if(score == null)
        {
            Random rand = new Random();
            score = rand.nextInt(100);
        }
        Intent i = new Intent();
        i.putExtra("score",score);
        setResult(RESULT_OK,i);
    }

    @Override
    public void onBackPressed()
    {
        returnScore();
        super.onBackPressed();
    }
}
