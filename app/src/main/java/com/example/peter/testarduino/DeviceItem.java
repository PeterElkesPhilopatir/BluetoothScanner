package com.example.peter.testarduino;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Matt on 5/12/2015.
 */
public final class DeviceItem {
    int i;
    private String deviceName;
    private String address;
    private String uuid;
    private boolean connected;
    int rssi;


    public String getDeviceName() {
        return deviceName;
    }

    public boolean getConnected() {
        return isConnected();
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceItem(String name, String address, String connected, int rssi) {
        this.deviceName = name;
        this.setAddress(address);
        this.rssi = rssi;
        if (connected == "true") {
            this.setConnected(true);
        } else {
            this.setConnected(false);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }


}
