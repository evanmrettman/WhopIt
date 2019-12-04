package com.example.whopit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whop_it);

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
        // START THE GAM
        nextInstruction();

    }

    public void onSensorChanged(SensorEvent event) {
        // Accelerometer event
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            long actualTime = System.currentTimeMillis();
            if (actualTime  - mLastAccelerationUpdate > 50)  {
                mLastAccelerationUpdate = actualTime;
                float x = event.values[0],  y = event.values[1],
                        z = event.values[2];

                float acceleration = Math.abs(x) + Math.abs(y) + Math.abs(z);

                if((Math.abs(acceleration - mLastAcceleration) - (mLastRotation*3)) > 1.5){
                    System.out.println("Shook it!");
                    answer(ANS_SHAKE);
                    System.out.println("|" + acceleration + " - " + mLastAcceleration + "| - " + mLastRotation*3);
                }

                mLastAcceleration = acceleration;

                /* Prints for testing
                System.out.println("x = " + String.valueOf(x));
                System.out.println("y = " + String.valueOf(y));
                System.out.println("z = " + String.valueOf(z));

                 */


            }
        }

        // Rotation event
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            long actualTime = System.currentTimeMillis();
            if (actualTime  - mLastRotationUpdate > 50)  {
                mLastRotationUpdate = actualTime;
                float x = event.values[0],  y = event.values[1],
                        z = event.values[2];

                mLastRotation = Math.abs(x) + Math.abs(y) + Math.abs(z);

                if(mLastRotation > 3){
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

    private void answer(int answer){
        if(answer == currentInstruction) {
            currentInstruction = 0;
            score++;
            runOnUiThread(new Runnable() {
                // Updates to UI outside of main thread
                @Override
                public void run() {
                    txt_instruction.setText("Nice!");
                }
            });
            System.out.println("Correct answer!: " + answer);
        }
        else{
            System.out.println("Wrong answer!: " + answer);
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
                        txt_instruction.setText("...");
                }
            }
        });


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

                        runOnUiThread(new Runnable() {

                            // Updates to UI outside of main thread
                            @Override
                            public void run() {
                                txt_instruction.setText("Game over! You got " + score + " points!");
                            }
                        });
                    }
                    else{
                        System.out.println("Nice!");
                        if(mTimeBetweenInstructions > 750) {
                            mTimeBetweenInstructions -= 150;
                        }
                        runOnUiThread(new Runnable() {
                            // Updates to UI outside of main thread
                            @Override
                            public void run() {
                                txt_instruction.setText("Nice!...");
                            }
                        });
                        Thread.sleep(100);
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
        returnScore();
        super.onBackPressed();
    }
}
