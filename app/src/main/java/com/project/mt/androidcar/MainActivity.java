package com.project.mt.androidcar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mt.androidcar.DirectionButton.DirectionButtonListener;
import com.project.mt.androidcar.KeyButton.KeyButtonListener;

public class MainActivity extends AppCompatActivity implements KeyButtonListener, DirectionButtonListener{

    private int count = 0;
    private int speed = 0;
    private int angle = 0xFF;
    private TextView Speed;
    private TextView Angle;
    private TextView Direct;
    public KeyButton Button_Key;
    public SpeedButton Button_Add;
    private SocketThread SocketClient;
    private boolean WiFiState = false;
    public SpeedButton Button_Subtract;
    public DirectionButton Image_Direction;
    private Handler SendHandler = new Handler();
    private byte[] RevBuff = new byte[]{0x00};
    private byte[] PreRevBuff = new byte[]{0x00};
    private byte[] SendBuff = new byte[]{0x4D, 0x46, 0x59, 0x00, 0x00, 0x00, 0x30, 0x00};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.Button_Key = findViewById(R.id.key);
        this.Button_Key.SetOnKeyButtonListener(this);
        this.Image_Direction = findViewById(R.id.direction);
        this.Image_Direction.SetOnDirectionButtonListener(this);

        this.Angle = findViewById(R.id.angle);
        this.Speed = findViewById(R.id.speed);
        this.Direct = findViewById(R.id.direct);
        this.Button_Add = findViewById(R.id.speed_add);
        this.Button_Subtract = findViewById(R.id.speed_subtract);
        this.Speed.setText(this.Speed.getContext().getString(R.string.speed, 0));
        this.Angle.setText(this.Angle.getContext().getString(R.string.angle, 0));
        this.Direct.setText(String.format(this.Direct.getResources().getString(R.string.direct),
                this.Direct.getResources().getString(R.string.stop)));

        Handler RevHandler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message message){
                if(message.what == 0x4D){
                    RevBuff[0] = (byte)(message.obj);
                }
                return false;
            }
        });

        SocketClient = new SocketThread(RevHandler);

        this.Button_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speed < 100)
                    speed++;
                Speed.setText(Speed.getContext().getString(R.string.speed, speed));
            }
        });

        this.Button_Subtract.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(speed > 0)
                    speed--;
                Speed.setText(Speed.getContext().getString(R.string.speed, speed));
            }
        });

        this.SendHandler.postDelayed(this.SendTask,1000);
    }

    private Runnable SendTask = new Runnable(){
        @Override
        public void run(){
            SendHandler.postDelayed(this,500);
            CalSendBuff(angle, speed);
            Message message = new Message();
            message.what = 0x46;
            message.obj = SendBuff;
            if(WiFiState){
                SocketClient.SocketRevHandler.sendMessage(message);
                if(PreRevBuff[0] == RevBuff[0]){
                    count++;
                    if(count >= 10){
                        WiFiState = false;
                        Button_Key.SetState(false);
                        SocketClient.SocketDestroy();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.disconnect), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    count = 0;
                PreRevBuff[0] = RevBuff[0];
            }

            if(WiFiState){
                if(angle == 0xFF)
                    Direct.setText(String.format(Direct.getResources().getString(R.string.direct),
                            Direct.getResources().getString(R.string.stop)));
                else if(angle >= 0)
                    Direct.setText(String.format(Direct.getResources().getString(R.string.direct),
                            Direct.getResources().getString(R.string.forward)));
                else
                    Direct.setText(String.format(Direct.getResources().getString(R.string.direct),
                            Direct.getResources().getString(R.string.backward)));
            }
            else{
                Direct.setText(String.format(Direct.getResources().getString(R.string.direct),
                        Direct.getResources().getString(R.string.stop)));
            }
        }
    };

    private void CalSendBuff(int angle, int speed){
        byte temp = 0;

        if(angle == 0xFF)
            this.SendBuff[3] = 0x00;
        else if(angle >= 0)
            this.SendBuff[3] = 0x01;
        else
            this.SendBuff[3] = 0x02;
        this.SendBuff[4] = (byte)Math.abs(angle);
        this.SendBuff[5] = (byte)speed;
        this.SendBuff[6]++;
        if(this.SendBuff[6] == 0x3A)
            this.SendBuff[6] = 0x30;
        for(int i = 0; i < 7; i++){
            temp += this.SendBuff[i];
        }
        this.SendBuff[7] = temp;
    }

    @Override
    public void ListenState(boolean State){
        if(State){
            count = 0;
            new Thread(SocketClient).start();
            Handler Delayhandler = new Handler();
            Delayhandler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    if(SocketClient.isSocketConnect()){
                        WiFiState = true;
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectsuccess), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        WiFiState = false;
                        Button_Key.SetState(false);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectwifi), Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000);
        }else{
            WiFiState = false;
            SocketClient.SocketDestroy();
        }
    }

    @Override
    public void ListenAngle(int angle){
        this.angle = angle;
        if(this.angle == 0xFF)
            this.Angle.setText(this.Angle.getContext().getString(R.string.angle, 0));
        else
            this.Angle.setText(this.Angle.getContext().getString(R.string.angle, Math.abs(this.angle)));
    }
}
