package com.capstone_team_capstone_meal_master;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Thread th = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(2000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                finally{
                    Intent obj = new Intent(splash_screen.this,MainActivity.class);
                    startActivity(obj);
                }
            }
        };
        th.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}