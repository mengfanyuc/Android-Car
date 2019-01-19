package com.project.mt.androidcar;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketThread implements Runnable {

    private Socket SocketClient;
    private boolean SocketConnect;
    private Handler SocketHandler;
    public Handler SocketRevHandler;
    private InputStream SocketInputStream;
    private OutputStream SocketOutputStream;

    public SocketThread(Handler handler) {
        super();
        this.SocketHandler = handler;
    }

    @Override
    public void run(){
        try{
            SocketClient = new Socket("192.168.1.128", 5000);
            if(SocketClient.isConnected()){
                SocketConnect = true;
                SocketInputStream = SocketClient.getInputStream();
                SocketOutputStream = SocketClient.getOutputStream();
            }
            else
                SocketConnect = false;

            new Thread(){
                public void run(){
                    byte buff[] = new byte[]{0x00};
                    try{
                        while((SocketInputStream.read(buff)) != -1){
                            Message message = new Message();
                            message.what = 0x4D;
                            message.obj = buff[0];
                            SocketHandler.sendMessage(message);
                        }
                    }catch(IOException | RuntimeException e){
                        e.printStackTrace();
                    }
                }
            }.start();

            Looper.prepare();
            SocketRevHandler = new Handler(new Handler.Callback(){
                @Override
                public boolean handleMessage(Message message){
                    if(message.what == 0x46){
                        try{
                            SocketOutputStream.write((byte[])message.obj);
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });
            Looper.loop();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean isSocketConnect() {
        return this.SocketConnect;
    }

    public void SocketDestroy(){
        try{
            if(SocketClient != null && SocketClient.isConnected()){
                this.SocketConnect = false;
                this.SocketClient.close();
                this.SocketClient = null;
                this.SocketInputStream.close();
                this.SocketInputStream = null;
                this.SocketOutputStream.close();
                this.SocketOutputStream = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
