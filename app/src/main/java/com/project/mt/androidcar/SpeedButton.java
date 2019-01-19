package com.project.mt.androidcar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;

public class SpeedButton extends AppCompatButton{

    private MyHandler handler;

    public SpeedButton(Context context){
        this(context, null);
        this.Button_Init();
    }
    public SpeedButton(Context context, AttributeSet attrs){
        this(context, attrs, android.support.v7.appcompat.R.attr.buttonStyle);
        this.Button_Init();
    }
    public SpeedButton(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.Button_Init();
    }

    private void Button_Init(){
        this.handler = new MyHandler(this);
        this.setOnLongClickListener(new OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                new Thread(new LongClickThread()).start();
                return true;
            }
        });
    }

    private class LongClickThread implements Runnable{
        private int num;
        @Override
        public void run(){
            while(SpeedButton.this.isPressed()){
                num++;
                if(num % 5 == 0)
                    handler.sendEmptyMessage(1);
                SystemClock.sleep(10);
            }
        }
    }

    private static class MyHandler extends Handler{
        private WeakReference<SpeedButton> ref;
        MyHandler(SpeedButton button){
            ref = new WeakReference<>(button);
        }
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            SpeedButton button = ref.get();
            if(button != null){
                button.performClick();
            }
        }
    }
}
