package com.example.peter.testarduino.Connecting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Handler;

public class ConnectThread extends Thread {

    private BluetoothSocket bTSocket;
    BluetoothSocket socket;
    Handler bt_handler;
    int handlerState;
    OutputStream outputStream;
    InputStream inputStream;
    //    ConnectedThread connectedThread;
    BluetoothDevice bluetoothDevice;
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();


    public ConnectThread(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice=bluetoothDevice;
        bluetoothDevice= adapter.getRemoteDevice(bluetoothDevice.getAddress());
    }

    public BluetoothSocket connect(UUID mUUID) {
        BluetoothSocket temp = null;
        try {
            temp = bluetoothDevice.createRfcommSocketToServiceRecord(mUUID);
        } catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not create RFCOMM socket:" + e.toString());
            return null;
        }
        try {
            temp.connect();
        } catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not connect: " + e.toString());

        }
        return temp;
    }

    public boolean cancel() {
        try {
            bTSocket.close();
        } catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString());
            return false;
        }
        return true;
    }

}