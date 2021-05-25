package com.example.arduinobluecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

// Client Thread: tries to connect to the Server Thread
public class BluetoothClient extends Thread {
    private BluetoothSocket mBTSocket;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    private BluetoothAdapter mainBluetoothAdapter;
    private ConnectedBTSocket connectedBTSocket;


    public BluetoothClient(BluetoothDevice device, UUID uuid, BluetoothAdapter adapter) {
        Log.d("CONN", "Client: started.");
        mmDevice = device;
        deviceUUID = uuid;
        mainBluetoothAdapter = adapter;
    }


    public void run() {
        BluetoothSocket tmp = null;
        Log.i("CONN", "RUN Client ");

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
        } catch (IOException e) {
            Log.e("CONN", "Client: Could not create InsecureRfcommSocket " + e.getMessage());
        }

        mBTSocket = tmp;

        // Always cancel discovery because it will slow down a connection
        mainBluetoothAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket

        try {
            //blocking call
            mBTSocket.connect();

            Log.d("CONN", "run: Client connected.");
        } catch (IOException e) {
            // Close the socket
            try {
                mBTSocket.close();
            } catch (IOException e1) {
                Log.e("CONN", "Client: run: Unable to close connection in socket " + e1.getMessage());
            }
            Log.d("CONN", "run: Client: Could not connect to UUID: " + deviceUUID );
        }

        connectedBTSocket = new ConnectedBTSocket(mBTSocket);
        connectedBTSocket.start();
    }


    public void writeData(byte[] b){
        connectedBTSocket.write(b);
    }


    public void closeConn() {
        try {
            Log.d("CONN", "cancel: Closing Client Socket.");
            mBTSocket.close();

        } catch (IOException e) {
            Log.e("CONN", "cancel: close() of mmSocket in Client failed. " + e.getMessage());
        }
    }
}
