package com.example.whopit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class HiScores extends AppCompatActivity {

    private ArrayList<Game> games = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_scores);

        Bundle extras = getIntent().getBundleExtra("bundle");
        ArrayList<Game> games = (ArrayList<Game>) extras.getSerializable("arraylist");
        ListView lv = findViewById(R.id.lv_scores);
        lv.setAdapter(
                new ArrayAdapter<Game>(
                    getApplicationContext(),android.R.layout.simple_list_item_1,games
                )
        );

        ((Button) findViewById(R.id.but_return)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
