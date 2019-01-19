package com.project.mt.androidcar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class KeyButton extends View implements OnTouchListener{

    private Paint paint;
    private float PressX;
    private boolean isMove = false;
    private boolean isChoose = false;
    private KeyButtonListener KeyListener;
    private Bitmap Button, Button_On, Button_Off;

    public KeyButton(Context context){
        super(context);
        this.Button_Init();
        paint = new Paint();
    }

    public KeyButton(Context context, AttributeSet attrs){
        super(context, attrs);
        this.Button_Init();
        paint = new Paint();
    }

    private void Button_Init(){
        this.Button_On = BitmapFactory.decodeResource(getResources(),
                R.drawable.key_on);
        this.Button_Off = BitmapFactory.decodeResource(getResources(),
                R.drawable.key_off);
        this.Button = BitmapFactory.decodeResource(getResources(),
                R.drawable.key);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float x;

        if(this.isMove){
            if(this.PressX >= this.Button_On.getWidth() - this.Button.getWidth()/2) {
                x = this.Button_On.getWidth() - this.Button.getWidth();
                canvas.drawBitmap(this.Button_On, 0, 0, this.paint);
            }
            else if(this.PressX <= this.Button.getWidth()/2) {
                x = 0;
                canvas.drawBitmap(this.Button_Off, 0, 0, this.paint);
            }
            else {
                x = this.PressX - this.Button.getWidth() / 2;
                if(this.PressX < (this.Button_On.getWidth()/2))
                    canvas.drawBitmap(this.Button_Off, 0, 0, this.paint);
                else
                    canvas.drawBitmap(this.Button_On, 0, 0, this.paint);
            }
        }
        else{
            if(this.isChoose){
                x = this.Button_Off.getWidth() - this.Button.getWidth();
                canvas.drawBitmap(this.Button_On, 0, 0, this.paint);
            }
            else {
                x = 0;
                canvas.drawBitmap(this.Button_Off, 0, 0, this.paint);
            }
        }
        canvas.drawBitmap(this.Button, x,0, this.paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() > this.Button_On.getWidth()
                        || event.getX() < 0 || event.getY() < 0
                        || event.getY() > this.Button_On.getHeight())
                    return false;
                this.isMove = true;
                this.PressX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                this.PressX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                this.isMove = false;
                boolean preChoose = this.isChoose;
                this.isChoose = (event.getX() >= this.Button_On.getWidth()/2);
                if(preChoose != this.isChoose)
                    this.KeyListener.ListenState(this.isChoose);
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    public void SetOnKeyButtonListener(KeyButtonListener Listener){
        this.KeyListener = Listener;
    }

    public void SetState(boolean state){
        boolean preChoose = this.isChoose;
        this.isChoose = state;
        if(preChoose != this.isChoose)
            invalidate();
    }

    public interface KeyButtonListener{
        void ListenState(boolean State);
    }
}
