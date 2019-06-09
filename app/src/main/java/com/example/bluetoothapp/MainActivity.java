package com.example.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothapp.connectivity.DataTransfer;
import com.example.bluetoothapp.threads.Client;
import com.example.bluetoothapp.threads.Server;

import java.util.ArrayList;
import java.util.Set;

import de.mxapplications.openfiledialog.OpenFileDialog;

public class MainActivity extends AppCompatActivity {

    private ImageView bluetoothIndicationIv;
    private Button onOffBtn;
    private Button choosePairedDeviceBtn;
    private Button modeBtn;
    private Button fileOpenBtn;
    private Button runBtn;

    private BluetoothAdapter bluetoothAdapter;

    private int mode = 1;
    final private static int MODE_SERVER = 1;
    final private static int MODE_CLIENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothIndicationIv = findViewById(R.id.bluetoothIndicationIv);
        onOffBtn =              findViewById(R.id.onOffBtn);
        choosePairedDeviceBtn = findViewById(R.id.choosePairedDeviceBtn);
        modeBtn =               findViewById(R.id.modeBtn);
        fileOpenBtn =        findViewById(R.id.fileOpenBtn);
        runBtn =             findViewById(R.id.runBtn);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initListeners();

        // check bluetooth availability
        if (bluetoothAdapter == null)
            showToast("Bluetooth is not available");
        else
            showToast("Bluetooth is available");

        // if enabled set the image to the status
        if (bluetoothAdapter.isEnabled()) {
            bluetoothIndicationIv.setImageResource(R.drawable.ic_action_on);
            onOffBtn.setText("Turn Off");
        }
        else {
            bluetoothIndicationIv.setImageResource(R.drawable.ic_action_off);
            onOffBtn.setText("Turn On");
        }
    }

    // toast msg function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // initialize listeners
    private void initListeners() {

        // on/off click
        onOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    showToast("Turning On Bluetooth");
                    startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

                    if (bluetoothAdapter.isEnabled()) {
                        onOffBtn.setText("Turn Off");
                        bluetoothIndicationIv.setImageResource(R.drawable.ic_action_on);
                    }

                    if (!bluetoothAdapter.isDiscovering()) {
                        showToast("Making Your Device Discoverable");
                        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
                    }
                }
                else {
                    showToast("Turning Bluetooth off");
                    //bluetoothIndicationIv.setImageResource(R.drawable.ic_action_off);
                    onOffBtn.setText("Turn On");
                    bluetoothAdapter.disable();
                    bluetoothIndicationIv.setImageResource(R.drawable.ic_action_off);
                }
            }
        });

        // paired devices btn
        choosePairedDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivity(intent);
            }
        });

        // switching mode btn
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MODE_CLIENT) {
                    mode = MODE_SERVER;
                    modeBtn.setText("Mode: Server");
                }
                else {
                    mode = MODE_CLIENT;
                    modeBtn.setText("Mode: Client");
                }
            }
        });

        final BluetoothDevice dev = null; // change it

        // runs the chosen thread
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable runnable;

                if (mode == MODE_SERVER) {
                    runnable = new Server(bluetoothAdapter);
                }
                else {
                    runnable = new Client(dev);
                }

                showToast("Connecting to chosen device");
                new Thread(runnable).start();

            }
        });

    }

}
