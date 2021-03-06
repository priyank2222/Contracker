package com.example.jennifer.contracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //splash screen loads for 3 seconds, and then goes to Main page. 
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(mainIntent);
                }

            }

        };
        thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
