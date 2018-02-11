package com.example.peter.testarduino;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import com.example.peter.testarduino.Connecting.*;

import static android.content.ContentValues.TAG;

public class DeviceListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static ArrayList<DeviceItem> deviceItemList;
    public static ArrayList<BluetoothDevice> bluetoothDeviceArrayList;
    private OnFragmentInteractionListener mListener;
    private static BluetoothAdapter bTAdapter;

    private BluetoothDevice selectedBluetoothDevice;

    private AbsListView mListView;

    private ArrayAdapter<DeviceItem> mAdapter;

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private boolean isBtConnected = false;


    private ProgressDialog progress;
    BluetoothSocket btSocket = null;

    String DEBUG_TAG = "peter";

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                // Create a new device item
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false", rssi);
                // Add it to our adapter
                mAdapter.add(newDevice);
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    // TODO: Rename and change types of parameters
    public static DeviceListFragment newInstance(BluetoothAdapter adapter) {
        DeviceListFragment fragment = new DeviceListFragment();
        bTAdapter = adapter;
        return fragment;
    }

    public DeviceListFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DEVICELIST", "Super called for DeviceListFragment onCreate\n");
        deviceItemList = new ArrayList<DeviceItem>();
        bluetoothDeviceArrayList = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
//                Intent intent = null;
//                String action = intent.getAction();
//                BluetoothDevice device1 = intent.getParcelableExtra(String.valueOf(BluetoothDevice.BOND_BONDED));

//                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false", 0);
                final ParcelUuid[] uu = device.getUuids();
                String UUID = uu.toString();
                newDevice.setUuid(UUID);
                deviceItemList.add(newDevice);
                bluetoothDeviceArrayList.add(device);
//                deviceItemList=sortList(deviceItemList);

            }
        }

        // If there are no devices, add an item that states so. It will be handled in the view.
        if (deviceItemList.size() == 0) {
            deviceItemList.add(new DeviceItem("No Devices", "", "false", 0));
        }

        Log.d("DEVICELIST", "DeviceList populated\n");

        mAdapter = new DeviceListAdapter(getActivity(), deviceItemList, bTAdapter);

        Log.d("DEVICELIST", "Adapter created\n");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deviceitem_list, container, false);
        ToggleButton scan = (ToggleButton) view.findViewById(R.id.scan);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if (isChecked) {
                    mAdapter.clear();
                    getActivity().registerReceiver(bReciever, filter);
                    bTAdapter.startDiscovery();
                } else {
                    getActivity().unregisterReceiver(bReciever);
                    bTAdapter.cancelDiscovery();
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), "onItemClick position:" + position +
                " name: " + deviceItemList.get(position).getDeviceName() + "\n", Toast.LENGTH_SHORT).show();

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(deviceItemList.get(position).getDeviceName());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        Toast.makeText(getContext(), "LCLK " + deviceItemList.get(position).getUuid(), Toast.LENGTH_SHORT).show();
        Log.d("size", "" + bluetoothDeviceArrayList.size());
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Connecting with" + deviceItemList.get(position).getDeviceName(), "please wait");
//
//        ConnectThread connect = new ConnectThread(bluetoothDeviceArrayList.get(position));
//        final BluetoothSocket connected = connect.connect(MY_UUID);


//                boolean connected = connectThread.connect(UUID.fromString(deviceItemList.get(position).getUuid()));


//        progressDialog.dismiss();
//        if (connected != null) {
//            Toast.makeText(getContext(), "connected", Toast.LENGTH_SHORT).show();
//            final Dialog dialog = new Dialog(getContext());
//            dialog.setContentView(R.layout.send_or_recieve);
//            dialog.setTitle("send");
//
//            Button sendBtn = dialog.findViewById(R.id.sendBtn);
//            sendBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ManageConnectThread manageConnectThread = new ManageConnectThread();
//                    try {
//                        manageConnectThread.sendData(connected, 22);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            ManageConnectThread manageConnectThread = new ManageConnectThread();
//            try {
//                manageConnectThread.sendData(connected, 22);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Toast.makeText(getContext(), "not connected", Toast.LENGTH_SHORT).show();
//        }
        selectedBluetoothDevice = bluetoothDeviceArrayList.get(position);

        new ConnectBT().execute(); //Call the class to connect

        return false;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    public ArrayList<DeviceItem> sortList(ArrayList<DeviceItem> deviceItemList1) {
        for (int counter = 0; counter < deviceItemList1.size(); counter++) {
            DeviceItem device = new DeviceItem(null, null, null, 0);
            device = deviceItemList1.get(counter);
            for (int counter2 = 0; counter2 < deviceItemList1.size(); counter2++) {
                if (device.getRssi() < deviceItemList1.get(counter2).getRssi()) {
                    deviceItemList1.set(deviceItemList1.indexOf(device), deviceItemList1.get(counter2));
                    deviceItemList1.set(counter2, device);
                }
            }
        }
        return deviceItemList1;
    }

    public ArrayList<DeviceItem> setIndices(ArrayList<DeviceItem> deviceItems) {
        for (int i = 0; i < deviceItems.size(); i++) {
            deviceItems.get(i).setI(i);
        }
        return deviceItems;
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getContext(), "Connecting...", "Please wait!!!");  //show a progress dialog

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    bTAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = bTAdapter.getRemoteDevice(selectedBluetoothDevice.getAddress());//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(MY_UUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                Toast.makeText(getContext(),"Connection Failed. Is it a SPP Bluetooth? Try again.",Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(getContext(),"Connected.",Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            progress.dismiss();


            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.send_or_recieve);
            dialog.setTitle("Control");
            dialog.show();


            Button onButton = dialog.findViewById(R.id.on);
            onButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnOnLed();
                    mMsg("on",Toast.LENGTH_SHORT);
                }
            });


            Button offButton = dialog.findViewById(R.id.off);
            offButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMsg("off",Toast.LENGTH_SHORT);

                    turnOffLed();
                }
            });

            Button okButton = dialog.findViewById(R.id.dismiss);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        }
    }

    private void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                Log.d("error",""+e.getMessage());
            }
        }
    }

    private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                Log.d("error",""+e.getMessage());

            }
        }
    }

    private void mMsg(String msg,int choice)
    {
        if (choice==Toast.LENGTH_SHORT){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();}

        if (choice==Toast.LENGTH_LONG){
            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();}

    }
}


