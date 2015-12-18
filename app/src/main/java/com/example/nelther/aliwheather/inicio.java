package com.example.nelther.aliwheather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by nelther on 13/12/2015.
 */
public class inicio extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(getApplicationContext(),AliWeather.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();



    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
