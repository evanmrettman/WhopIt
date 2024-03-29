package com.example.whopit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class WhopIt extends AppCompatActivity implements SensorEventListener
{

    Button but_return;
    TextView txt_instruction;
    Integer score;

    int currentInstruction;
    // Instructions:
    // 0 - none
    // 1 - Whop It!
    // 2 - Shake It!
    // 3 - Twist It!
    final int ANS_NONE = 0;
    final int ANS_WHOP = 1;
    final int ANS_SHAKE = 2;
    final int ANS_TWIST = 3;
    final int ANS_BOUND = 4;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mRotation;
    private long mLastAccelerationUpdate; //time of last update
    private long mLastRotationUpdate; //time of last update
    private float mLastRotation; //last rotation data
    private float mLastAcceleration; //last acceleration data

    private long mTimeBetweenInstructions; // this will get smaller as the game goes on

    private MediaPlayer sound_nice1;
    private MediaPlayer sound_nice2;
    private MediaPlayer sound_great1;
    private MediaPlayer sound_whopit;
    private MediaPlayer sound_twistit;
    private MediaPlayer sound_shakeit;
    private MediaPlayer sound_oof;

    final double TWIST_SUBTRACTION_MULTIPLIER = 2; //substracts amount of twist from amount of shake
    final double SHAKE_THRESHOLD = 1.25; //amount of shake - (twist amount * mult) needed to trigger
    final double TWIST_THRESHOLD = 4.5; //amount of twist needed to trigger


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whop_it);

        // Sounds
        sound_nice1 = MediaPlayer.create(WhopIt.this,R.raw.nice1);
        sound_nice2 = MediaPlayer.create(WhopIt.this,R.raw.nice2);
        sound_great1 = MediaPlayer.create(WhopIt.this,R.raw.great1);
        sound_whopit = MediaPlayer.create(WhopIt.this,R.raw.whopit);
        sound_twistit = MediaPlayer.create(WhopIt.this,R.raw.twistit);
        sound_shakeit = MediaPlayer.create(WhopIt.this,R.raw.shakeit);
        sound_oof = MediaPlayer.create(WhopIt.this,R.raw.oof);



        // Time between instructions (miliseconds)
        mTimeBetweenInstructions = 4000;
        // No instruction to start;
        currentInstruction = 0;
        // Set score to 0
        score = 0;

        // Return button
        but_return = findViewById(R.id.but_return);
        but_return.setOnClickListener(new View.OnClickListener()
        {
            @Override
            // When clicking return.
            public void onClick(View v)
            {
                returnScore();
                finish();
            }
        });
        //but_return.setClickable(false);
        //but_return.setAlpha(0);

        // Screen taps (whop it! instruction)
        txt_instruction = findViewById(R.id.instruction);
        txt_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            // When tapping screen ( or in this case the instruction)
            public void onClick(View v) {
                System.out.println("Whopped it!");
                answer(ANS_WHOP);
            }
        });

        // Sensors
        mSensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);
        // Accelerometer
        mLastAccelerationUpdate = System.currentTimeMillis();
        mAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_LINEAR_ACCELERATION);
        mLastAcceleration = 0;
        // Rotation
        mLastRotationUpdate = System.currentTimeMillis();
        mRotation = mSensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE);
        mLastRotation = 0;


        //DEBUG
        System.out.println("Current main thread: " + Thread.currentThread());
        // START THE GAME
        nextInstruction();

    }

    // When a movement is detected
    public void onSensorChanged(SensorEvent event) {
        // Accelerometer event (for shaking)
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            long actualTime = System.currentTimeMillis();
            if (actualTime  - mLastAccelerationUpdate > 50)  {
                mLastAccelerationUpdate = actualTime;
                float x = event.values[0],  y = event.values[1],
                        z = event.values[2];

                float acceleration = Math.abs(x) + Math.abs(y) + Math.abs(z);

                // determine if a shake occured
                //if((Math.abs(acceleration - mLastAcceleration) - (mLastRotation*3)) > 1.5){
                if((Math.abs(acceleration - mLastAcceleration) - (mLastRotation*TWIST_SUBTRACTION_MULTIPLIER)) > SHAKE_THRESHOLD){
                    System.out.println("Shook it!");
                    answer(ANS_SHAKE);
                    System.out.println("|" + acceleration + " - " + mLastAcceleration + "| - " + mLastRotation*TWIST_SUBTRACTION_MULTIPLIER);
                }

                mLastAcceleration = acceleration;

                /* Prints for testing
                System.out.println("x = " + String.valueOf(x));
                System.out.println("y = " + String.valueOf(y));
                System.out.println("z = " + String.valueOf(z));

                 */


            }
        }

        // Rotation event (for twisting)
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            long actualTime = System.currentTimeMillis();
            if (actualTime  - mLastRotationUpdate > 50)  {
                mLastRotationUpdate = actualTime;
                float x = event.values[0],  y = event.values[1],
                        z = event.values[2];

                mLastRotation = Math.abs(x) + Math.abs(y) + Math.abs(z);

                if(mLastRotation > TWIST_THRESHOLD){
                    System.out.println("Twisted it!");
                    answer(ANS_TWIST);
                }

                /*
                System.out.println("rot x = " + String.valueOf(x));
                System.out.println("rot y = " + String.valueOf(y));
                System.out.println("rot z = " + String.valueOf(z));


                 */

            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    protected void onResume()  {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotation,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    // Called when giving an answer
    private void answer(int answer){
        if(answer == currentInstruction) {
            // reset instruction
            currentInstruction = 0;

            // increase score
            score++;

            // give feedback
            runOnUiThread(new Runnable() {
                // Updates to UI outside of main thread
                @Override
                public void run() {
                    txt_instruction.setText("Nice!");
                }
            });
            playNice();
            System.out.println("Correct answer!: " + answer);
        }
        else{
            System.out.println("Wrong answer!: " + answer);
        }
    }

    // plays a random sound of congratulations
    private void playNice(){
        int c = (int)(Math.random()*3);
        switch(c){
            case 0:
                sound_nice1.release();
                sound_nice1 = MediaPlayer.create(WhopIt.this,R.raw.nice1);
                sound_nice1.start();
                break;
            case 1:
                sound_nice2.release();
                sound_nice2 = MediaPlayer.create(WhopIt.this,R.raw.nice2);
                sound_nice2.start();
                break;
            case 2:
                sound_great1.release();
                sound_great1 = MediaPlayer.create(WhopIt.this,R.raw.great1);
                sound_great1.start();
                break;
            default:
                //sound_nice2.start();
        }
    }

    // plays game over sound
    private void playGameOver(){
        sound_oof.release();
        sound_oof = MediaPlayer.create(WhopIt.this,R.raw.oof);
        sound_oof.start();
    }

    // plays sound of given instruction number
    private void playInstruction(int i){
        switch(i){
            case 1:
                sound_whopit.release();
                sound_whopit = MediaPlayer.create(WhopIt.this,R.raw.whopit);
                sound_whopit.start();
                break;
            case 2:
                sound_shakeit.release();
                sound_shakeit = MediaPlayer.create(WhopIt.this,R.raw.shakeit);
                sound_shakeit.start();
                break;
            case 3:
                sound_twistit.release();
                sound_twistit = MediaPlayer.create(WhopIt.this,R.raw.twistit);
                sound_twistit.start();
                break;
            default:
        }
    }

    // calls for the next instruction, which will recursively call for the next one
    private void nextInstruction(){
        // Handles starting the next instruction and recursively calling all next instructions
        // Handles if instruction isn't completed in time
        Random rand = new Random();
        currentInstruction = rand.nextInt(ANS_BOUND - 1) + 1;
        // Instructions:
        // 0 - none
        // 1 - Whop It!
        // 2 - Shake It!
        // 3 - Twist It!
        //playInstruction(currentInstruction);
        runOnUiThread(new Runnable() {
            // Updates to UI outside of main thread
            @Override
            public void run() {
                switch(currentInstruction){
                    case 1:
                        txt_instruction.setText("Whop It!");
                        break;
                    case 2:
                        txt_instruction.setText("Shake It!");
                        break;
                    case 3:
                        txt_instruction.setText("Twist It!");
                        break;
                    case 0:
                    default:
                        txt_instruction.setText("Whop It?");
                        currentInstruction=1;
                }
            }
        });
        playInstruction(currentInstruction);


        // Start countdown
        /*
        class Countdown implements Runnable{
            int prevScore = score;

            @Override
            public void run() {
                //DEBUG
                System.out.println("Current countdown thread: " + Thread.currentThread());
                System.out.println("Starting countdown thread for " + mTimeBetweenInstructions + " seconds...");
                try {
                    Thread.sleep(mTimeBetweenInstructions * 1000);
                    if(prevScore == score){
                        System.out.println("Game over!");
                        onBackPressed(); // Gameover!
                    }
                    else{
                        System.out.println("Nice!");
                        if(mTimeBetweenInstructions > .75) {
                            mTimeBetweenInstructions -= .15;
                        }
                        nextInstruction(); // Call for the next instruction
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Countdown cd = new Countdown();

         */
        Thread countdown_thread = new Thread(){
            int prevScore = score;

            @Override
            public void run() {
                //DEBUG
                System.out.println("Current countdown thread: " + Thread.currentThread());
                System.out.println("Starting countdown thread for " + mTimeBetweenInstructions + " seconds...");
                try {
                    Thread.sleep(mTimeBetweenInstructions);
                    if(prevScore == score){
                        System.out.println("Game over!");

                        // Game over handling
                        currentInstruction = 0;

                        // play sound for game over
                        playGameOver();

                        runOnUiThread(new Runnable() {

                            // Updates to UI outside of main thread
                            @Override
                            public void run() {
                                txt_instruction.setText("Game over! You got " + score + " points!");
                                but_return.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else{
                        System.out.println("Nice!");
                        if(mTimeBetweenInstructions > 1750) {
                            mTimeBetweenInstructions -= 150;
                            System.out.println("Level 1");
                        }
                        else if(mTimeBetweenInstructions > 1400) {
                            // Once we get to level 2, things get really hard
                            mTimeBetweenInstructions -= 25;
                            System.out.println("Level 2!");
                        }
                        else{
                            // from here the game gets progressively harder forever
                            mTimeBetweenInstructions -= 10;
                            System.out.println("Level 3!!!");
                        }
                        runOnUiThread(new Runnable() {
                            // Updates to UI outside of main thread
                            @Override
                            public void run() {
                                txt_instruction.setText("Nice!...");
                            }
                        });
                        Thread.sleep(250);
                        nextInstruction(); // Call for the next instruction
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        countdown_thread.start();


    }

    private void returnScore()
    {

        // Random score for testing
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
        if (currentInstruction == 0){
            returnScore();
            super.onBackPressed();
        }
    }
}
