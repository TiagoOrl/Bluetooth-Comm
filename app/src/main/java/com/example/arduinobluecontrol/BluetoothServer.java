package com.example.arduinobluecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

interface IOnInputReceiveListener{
    public void onReceive(String buffer);
}


public class BluetoothServer extends Thread {
    private BluetoothServerSocket bluetoothServerSocket;
    private byte[] buffer;
    private IOnInputReceiveListener iOnInputReceiveListener;
    private Handler handler;
    BluetoothSocket btSocket = null;
    InputStream inStream = null;

    public BluetoothServer(BluetoothServerSocket bluetoothServerSocket){
        this.bluetoothServerSocket = bluetoothServerSocket;
        handler = new Handler();
    }

    public void setOnInputReceiveListener(IOnInputReceiveListener iOnInputReceiveListener){
        this.iOnInputReceiveListener = iOnInputReceiveListener;
    }

    @Override
    public void run() {

        buffer = new byte[1024];
        try {


            while(true) {
                Log.d("SERVER: ", "----------------- WAITING FOR SOCKET");
                btSocket = bluetoothServerSocket.accept();
                if (btSocket != null) break;
            }

            Log.d("SERVER: ", "----------------- ACCEPTED");

            while (true){
                inStream = btSocket.getInputStream();
                inStream.read(buffer);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (iOnInputReceiveListener != null)
                            iOnInputReceiveListener.onReceive(new String(buffer, Charset.defaultCharset()));
                    }
                });

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
