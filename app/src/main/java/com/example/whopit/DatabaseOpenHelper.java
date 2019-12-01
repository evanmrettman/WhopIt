package com.example.whopit;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Random;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    final private Random rand = new Random();
    final private static String DB_NAME = "whopit";
    final private static Integer VERSION = 5;
    final private Context context;
    final private static String CREATE_CMD =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER DEFAULT -1, %s INTEGER DEFAULT 0);",
                    Main.TABLE,Main.COLUMNS[0],Main.COLUMNS[1],Main.COLUMNS[2]);

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
        addDefaults(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Main.TABLE);
        onCreate(db);
    }

    void deleteDatabase ( ) {
        context.deleteDatabase(DB_NAME);
    }

    private void addDefaults(SQLiteDatabase db)
    {
        for(int i = 0; i < 20; i++)
        {
            ContentValues cv = new ContentValues();
            cv.put(Main.COLUMNS[1],i+1);
            cv.put(Main.COLUMNS[2],rand.nextInt(100));
            db.insert(Main.TABLE,null,cv);
        }
    }
}
