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

public class DirectionButton extends View implements OnTouchListener{

    private Paint paint;
    private float PressX, PressY;
    private boolean isPress = false;
    private DirectionButtonListener DirectionListener;
    private Bitmap ButtonBig, ButtonSmallUp, ButtonSmallDown;

    public DirectionButton(Context context){
        super(context);
        this.Button_Init();
        paint = new Paint();
    }

    public DirectionButton(Context context, AttributeSet attrs){
        super(context, attrs);
        this.Button_Init();
        paint = new Paint();
    }

    private void Button_Init(){
        this.ButtonBig = BitmapFactory.decodeResource(getResources(),
                R.drawable.direction_big);
        this.ButtonSmallUp = BitmapFactory.decodeResource(getResources(),
                R.drawable.direction_small_up);
        this.ButtonSmallDown = BitmapFactory.decodeResource(getResources(),
                R.drawable.direction_small_down);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawBitmap(this.ButtonBig, 0, 0, this.paint);
        if(this.isPress)
            canvas.drawBitmap(this.ButtonSmallDown,
                    this.PressX  - this.ButtonSmallDown.getWidth()/2,
                    this.PressY  - this.ButtonSmallDown.getHeight()/2, this.paint);
        else
            canvas.drawBitmap(this.ButtonSmallUp, this.ButtonSmallUp.getWidth()/2,
                    this.ButtonSmallUp.getHeight()/2, this.paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(this.CheckPressPosition(event)){
                    this.isPress = true;
                    this.PressX = event.getX();
                    this.PressY = event.getY();
                }
                else
                    return false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(this.CheckMovePosition(event) && this.isPress){
                    this.SetButtonSmallPosition(event);
                    this.DirectionListener.ListenAngle(this.GetRotationAngle());
                }
                else
                    return false;
                break;
            case MotionEvent.ACTION_UP:
                this.isPress = false;
                this.DirectionListener.ListenAngle(0xFF);
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    private boolean CheckMovePosition(MotionEvent event){
        return (Math.pow(event.getX() - this.ButtonBig.getWidth()/2, 2)
                + Math.pow(event.getY() - this.ButtonBig.getHeight()/2, 2))
                <= Math.pow(this.ButtonBig.getHeight(), 2);
    }

    private boolean CheckPressPosition(MotionEvent event){
        return (Math.pow(event.getX() - this.ButtonBig.getWidth()/2, 2)
                + Math.pow(event.getY() - this.ButtonBig.getHeight()/2, 2))
                <= Math.pow(this.ButtonSmallDown.getHeight()/2, 2);
    }

    private void SetButtonSmallPosition(MotionEvent event){
        if(this.CheckPressPosition(event)){
            this.PressX = event.getX();
            this.PressY = event.getY();
        }
        else{
            float k = (this.ButtonSmallDown.getHeight()/2)
                    /(float)Math.sqrt(Math.pow(event.getX() - this.ButtonBig.getWidth()/2, 2)
                    + Math.pow(event.getY() - this.ButtonBig.getHeight()/2, 2));
            this.PressX = (event.getX() - this.ButtonBig.getHeight()/2)*k + this.ButtonBig.getWidth()/2;
            this.PressY = this.ButtonBig.getHeight()/2 - (this.ButtonBig.getWidth()/2 - event.getY())*k;
        }
    }

    private int GetRotationAngle(){
        return (int)(Math.atan2(this.ButtonBig.getHeight()/2 - this.PressY,
                this.PressX - this.ButtonBig.getWidth()/2) * 180/Math.PI);
    }

    public void SetOnDirectionButtonListener(DirectionButtonListener Listener){
        this.DirectionListener = Listener;
    }

    public interface DirectionButtonListener{
        void ListenAngle(int angle);
    }
}

