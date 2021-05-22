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

    public BluetoothServer(BluetoothServerSocket bluetoothServerSocket){
        this.bluetoothServerSocket = bluetoothServerSocket;
    }

    public void setOnInputReceiveListener(IOnInputReceiveListener iOnInputReceiveListener){
        this.iOnInputReceiveListener = iOnInputReceiveListener;
    }

    @Override
    public void run() {
        InputStream in = null;

        buffer = new byte[1024];
        try {
            BluetoothSocket btSocket = null;

            while(true) {
                Log.d("SERVER: ", "----------------- WAITING FOR SOCKET");
                btSocket = bluetoothServerSocket.accept();
                if (btSocket != null) break;
            }

            Log.d("SERVER: ", "----------------- ACCEPTED");

            while (true){
                in = btSocket.getInputStream();
                in.read(buffer);
                if (iOnInputReceiveListener != null)
                    iOnInputReceiveListener.onReceive(new String(buffer, Charset.defaultCharset()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
