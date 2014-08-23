package com.example.blunobasicdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by george on 23/8/14.
 */
public class UsbKeySearchFragment extends DialogFragment {

    private Context mainContext= getActivity();
    private Handler mHandler= new Handler();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.usbkeysearch_layout, null))
        .setTitle("Search for USB Key")
                .setPositiveButton("Try connecting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            //lolol
                    }
                });
        return builder.create();


    }

    // George
    public void startPassiveProtectionScan() {
        if (BlunoLibrary.mConnectionState != BlunoLibrary.connectionStateEnum.isConnected) {


            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BlunoLibrary.mScanning = false;
                    Log.d("George_debug", "Stopping Passive Scan");
                    stopPassiveProtectionScan();
                    startPassiveProtectionScan();
                }
            }, BlunoLibrary.SCAN_PERIOD);

            BlunoLibrary.mScanning = true;
            BlunoLibrary.mBluetoothAdapter.startLeScan(passiveLeScanCallback);

        } else {
            Toast.makeText(mainContext, "Your USB Key is already connected and protected",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // George -Passive Device scan callback.
    private BluetoothAdapter.LeScanCallback passiveLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {

            // George
            Log.d("George_debug", "Passive Scan Callback - Device value is " + device);
            if (device.toString().equals(BlunoLibrary.BlunoNanoMacAddr)) {

//                ((Activity) mainContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView rssi_indicator = (TextView) findViewById(R.id.rssi_indicator);
//                        //rssi_indicator.setText("Your USB Key is currently protected");
//                            /*Toast.makeText(mainContext, "RSSI: " + -rssi,
//                                    Toast.LENGTH_SHORT).show();*/
//
//                        rssi_indicator.setText("RSSI: " + rssi);
//
//
//                        if (-rssi > 80) {
//
//                            /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                            r.play();*/
//
//
//                            Toast.makeText(mainContext, "USB Key is out of range!",Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });

            }

           /* ((Activity) mainContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("mLeScanCallback onLeScan run ");
                    //mLeDeviceListAdapter.addDevice(device);
                    //mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });*/
        }
    };

    // George
    public void stopPassiveProtectionScan() {
        BlunoLibrary.mBluetoothAdapter.stopLeScan(passiveLeScanCallback);

//        ((Activity) mainContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TextView rssi_indicator = (TextView) findViewById(R.id.rssi_indicator);
//                rssi_indicator.setText("00 - Your USB Key is not protected");
//            }
//        });
//        Log.d("George_debug","Starting Passive Protection Scan Again!");

    }



}
