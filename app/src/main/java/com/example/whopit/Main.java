package com.example.whopit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Main extends AppCompatActivity {

    final static public String[] COLUMNS = {"_id","game","score"};
    final static public String TABLE = "scores";
    private Integer gamesPlayed = 0;
    private ArrayList<Game> games;
    private SQLiteDatabase db;
    private DatabaseOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseOpenHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        updateList();
        Log.i("games_main",games.toString());

        ((Button) findViewById(R.id.but_start)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gamesPlayed += 1;
                Intent i = new Intent(getApplicationContext(),WhopIt.class);
                startActivityForResult(i,0);
            }
        });
        ((Button) findViewById(R.id.but_scores)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),HiScores.class);
                Bundle extras = new Bundle();
                extras.putSerializable("arraylist",games);
                i.putExtra("bundle",extras);
                startActivity(i);
            }
        });
        ((Button) findViewById(R.id.but_howTo)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),HowTo.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == 0)
        {
            if(data == null)
            {
                Log.i("games","data returned as null");
                return;
            }
            ContentValues cv = new ContentValues();
            cv.put(COLUMNS[1],gamesPlayed);
            cv.put(COLUMNS[2],data.getIntExtra("score",0));
            Log.i("games","game #" +gamesPlayed+" @ "+cv.get(COLUMNS[2]));
            db.insert(TABLE,null,cv);
            updateList();
        }
    }

    private void updateList()
    {
        games = new ArrayList<Game>();
        Cursor c = db.query(TABLE,COLUMNS,null,null,null,null,null);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            int curr_game = c.getInt(1);
            int curr_score = c.getInt(2);
            if(gamesPlayed < curr_game)
                gamesPlayed = curr_game;
            games.add(new Game(curr_game,curr_score));
        }
        c.close();
        Collections.sort(games, Collections.<Game>reverseOrder());
    }

}
