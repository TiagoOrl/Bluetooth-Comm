package com.example.arduinobluecontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private static UUID uuid = UUID.fromString("1c351fcc-217e-4974-b328-b0441407a604");
    private static final int REQUEST_ENABLE_BT = 10;
    public BluetoothAdapter bluetoothAdapter;
    private Button sendDataBtn, connBtn;
    public TextView receivedText;
    EditText outputText;
    private ListView pairedDevicesLv;
    private ArrayList<BluetoothDevice> devices;
    private BluetoothDevice deviceChoosen = null;

    public BluetoothServer bluetoothServer;
    public BluetoothClient bluetoothClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devices = new ArrayList<>();

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

        try {
            bluetoothServer = new BluetoothServer(bluetoothAdapter.listenUsingRfcommWithServiceRecord("app", uuid));
            bluetoothServer.setOnInputReceiveListener(new IOnInputReceiveListener() {
                @Override
                public void onReceive(String data) {
                    receivedText.setText(data);
                }
            });
            bluetoothServer.start();
        } catch (IOException e) { e.printStackTrace(); }

    }


    private void initViewListeners(){

        pairedDevicesLv = findViewById(R.id.pairedDevicesLv);
        receivedText = findViewById(R.id.receivedText);

        Button listPairedBtn = findViewById(R.id.listPairedBtn);
        listPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    ArrayList<String> s_pairedDevcs = new ArrayList<>();
                    for (BluetoothDevice device : pairedDevices) {
                        devices.add(device);
                        s_pairedDevcs.add(device.getName() + "\n" + device.getAddress() +"\n"+ device.getUuids()[2]);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_expandable_list_item_1, s_pairedDevcs);
                    pairedDevicesLv.setAdapter(arrayAdapter);

                    pairedDevicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            deviceChoosen = devices.get(position);
                        }
                    });
                }
            }
        });

        outputText = findViewById(R.id.outputText);

        sendDataBtn = findViewById(R.id.sendBtn);
        sendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothClient.writeData(outputText.getText().toString().getBytes(StandardCharsets.UTF_8));
            }
        });


        connBtn = findViewById(R.id.connBtn);
        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothClient != null) bluetoothClient.cancel();
                bluetoothClient = new BluetoothClient(deviceChoosen, uuid, bluetoothAdapter);
                bluetoothClient.start();
            }
        });
    }

}


