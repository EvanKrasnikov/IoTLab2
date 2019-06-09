package com.example.bluetoothapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceListActivity extends AppCompatActivity {
    //private final BroadcastReceiver FoundReceiver = null;
    protected ArrayList<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
    //private ListView foundDevicesListView;
    private ArrayAdapter<BluetoothDevice> btArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        final BluetoothAdapter myBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        final ListView foundDevicesListView = findViewById(R.id.listView);

        btArrayAdapter = new ArrayAdapter<>(
                DeviceListActivity.this,
                android.R.layout.simple_list_item_1,
                foundDevices);

        foundDevicesListView.setAdapter(btArrayAdapter);

        // Quick permission check
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }

        registerReceiver(FoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(FoundReceiver, filter);

        btArrayAdapter.clear();
        myBlueToothAdapter.startDiscovery();
        showToast( "Scanning Devices");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(FoundReceiver);
    }

    private final BroadcastReceiver FoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (!foundDevices.contains(device)) {
                    foundDevices.add(device);
                    showToast( "name: " + device.getName() + " " + device.getAddress());
                    btArrayAdapter.notifyDataSetChanged();
                }

                for (BluetoothDevice bd: foundDevices) {
                    showToast( "name: " + device.getName() + " " + device.getAddress());
                }
            }

            // When discovery cycle finished
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (foundDevices == null || foundDevices.isEmpty()) {
                    showToast( "No Devices");
                }
            }

        }
    };

    // toast msg function
    private void showToast(String msg) {
        Toast.makeText(DeviceListActivity.this, msg, Toast.LENGTH_LONG).show();
    }

}
