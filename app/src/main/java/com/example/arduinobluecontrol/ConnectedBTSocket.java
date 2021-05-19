package com.example.arduinobluecontrol;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 Finally the ConnectedThread which is responsible for maintaining the BTConnection, Sending the data, and
 receiving incoming data through input/output streams respectively.
 **/
public class ConnectedBTSocket extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedBTSocket(BluetoothSocket socket) {
        Log.d("CONN", "ConnectedThread: Starting.");

        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run(){
        byte[] buffer = new byte[1024];  // buffer store for the stream

        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            // Read from the InputStream
            try {
                bytes = mmInStream.read(buffer);
                String incomingMessage = new String(buffer, 0, bytes);
                Log.d("CONN", "InputStream: " + incomingMessage);
            } catch (IOException e) {
                Log.e("CONN", "write: Error reading Input Stream. " + e.getMessage() );
                break;
            }
        }
    }


    public void write(byte[] bytes) {
        String text = new String(bytes, Charset.defaultCharset());
        Log.d("CONN", "write: Writing to outputstream: " + text);
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e("CONN", "write: Error writing to output stream. " + e.getMessage() );
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}