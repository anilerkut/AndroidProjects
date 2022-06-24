package com.anilerkut.catchthekenny;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Game extends AppCompatActivity {

    TextView timeLeft,scoreText;
    ImageView bulb0;
    ImageView bulb1;
    ImageView bulb2;
    ImageView bulb3;
    ImageView bulb4;
    ImageView bulb5;
    ImageView bulb6;
    ImageView bulb7;
    ImageView bulb8;
    ImageView [] bulb_array;
    int user_score;
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        timeLeft=findViewById(R.id.timeLeft);
        bulb0=findViewById(R.id.bulb0);
        bulb1=findViewById(R.id.bulb1);
        bulb2=findViewById(R.id.bulb2);
        bulb3=findViewById(R.id.bulb3);
        bulb4=findViewById(R.id.bulb4);
        bulb5=findViewById(R.id.bulb5);
        bulb6=findViewById(R.id.bulb6);
        bulb7=findViewById(R.id.bulb7);
        bulb8=findViewById(R.id.bulb8);
        bulb_array=new ImageView[] {bulb0,bulb1,bulb2,bulb3,bulb4,bulb5,bulb6,bulb7,bulb8};
        scoreText=findViewById(R.id.score);
        user_score=0;
        hideBulbs();

        new CountDownTimer(31000, 1000) //CountDown code, decreases from 30 one by one
        {
            @Override
            public void onTick(long l)
            {
                timeLeft.setText(": "+l/1000);
            }
            @Override
            public void onFinish()
            {
                timeLeft.setText(": 0");
                handler.removeCallbacks(runnable);
                for (ImageView image:bulb_array)
                {
                    image.setVisibility(View.INVISIBLE);
                }

                AlertDialog.Builder alert= new AlertDialog.Builder(Game.this);
                alert.setTitle("Restart");
                alert.setMessage("Are You Sure to Restart the Game?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent intent=new Intent();
                        finish();
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent intent=new Intent(Game.this,MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
                alert.show();
            }
        }.start();
    }

    public void increaseScore(View view) //when the user click the bulb, increasing score
    {
        user_score++;
        scoreText.setText(": "+user_score);
    }

    public void hideBulbs()
    {
        handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {
                for (ImageView image:bulb_array)
                {
                    image.setVisibility(View.INVISIBLE);
                }

                Random random=new Random();
                int i=random.nextInt(9);
                bulb_array[i].setVisibility(View.VISIBLE) ;
                handler.postDelayed(runnable,400);
            }
        };
        handler.post(runnable);
    }
}