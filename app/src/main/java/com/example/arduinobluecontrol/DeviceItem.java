package com.example.arduinobluecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class DeviceItem {
    private final BluetoothAdapter adapter;
    private final UUID uuid;
    String name;
    private BluetoothDevice device;
    private BluetoothClient btClient;
    public boolean isConnected = false;

    public DeviceItem(BluetoothDevice device, BluetoothAdapter adapter, UUID uuid){
        this.adapter = adapter;
        this.uuid = uuid;
        this.device = device;
        this.name = device.getName();
    }

    public void connectAsClient() {
        btClient = new BluetoothClient(device, uuid, adapter);
        btClient.start();
        isConnected = true;
    }

    public void disconnectClient(){
        btClient.closeConn();
        isConnected = false;
        try {
            btClient.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String msg){
        btClient.writeData(msg.getBytes());
    }


    public String getDevName(){return name;}

}
