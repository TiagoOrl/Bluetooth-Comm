package com.example.arduinobluecontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private static UUID uuid = UUID.fromString("1c351fcc-217e-4974-b328-b0441407a604");
    private static final int REQUEST_ENABLE_BT = 10;
    public BluetoothAdapter bluetoothAdapter;
    private Button sendDataBtn;
    private RecyclerView devcsListRv;
    public TextView msgsBox;
    EditText outputText;
    private ArrayList<DeviceItem> devicesList;

    public BluetoothServer bluetoothServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        devicesList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        initViewListeners();
        startBTServer();
        listPairedDvcs();

    }

    private void startBTServer() {
        try {
            bluetoothServer = new BluetoothServer(bluetoothAdapter.listenUsingRfcommWithServiceRecord("app", uuid));
            bluetoothServer.setOnInputReceiveListener(new IOnInputReceiveListener() {
                @Override
                public void onReceive(String data) {
                    if (msgsBox.getText().toString().equals("msg")) msgsBox.setText("");
                    msgsBox.append(data);
                }
            });
            bluetoothServer.start();
        } catch (IOException e) { e.printStackTrace(); }


    }

    private void listPairedDvcs() {
        if (bluetoothAdapter == null) return;

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                devicesList.add(new DeviceItem(device, bluetoothAdapter, uuid));
            }

            adapter = new DevItemAdapter(devicesList);
            devcsListRv.setLayoutManager(layoutManager);
            devcsListRv.setAdapter(adapter);
        }
    }


    private void initViewListeners(){

        msgsBox = findViewById(R.id.receivedText);
        devcsListRv = findViewById(R.id.pairedDevcsRv);

        outputText = findViewById(R.id.outputText);

        sendDataBtn = findViewById(R.id.sendBtn);
        sendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DeviceItem device : devicesList){
                    if (device.isConnected){
                        device.sendData("\n" + outputText.getText().toString());
                        msgsBox.append("\n" + outputText.getText().toString());
                    }
                }
            }
        });

    }
}


