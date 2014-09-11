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
                        BlunoLibrary.mBluetoothLeService.connect(BlunoLibrary.BlunoNanoMacAddr);
                        BlunoLibrary.dialogShown = 0;
                        if ( BlunoLibrary.mConnectionState == BlunoLibrary.connectionStateEnum.isScanning) {
                            try {
                                Toast.makeText(mainContext, "Cannot find USB Key! Attempting to start scan", Toast.LENGTH_SHORT).show();
                            } catch ( Exception e ) {
                                System.out.println("Cannot find USB Key! Attempting to restart scan.");
                            }
                            BlunoLibrary.mBluetoothAdapter.startLeScan(activeFragmentScanCallback);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        BlunoLibrary.dialogShown = 0;
                        BlunoLibrary.mBluetoothAdapter.stopLeScan(activeFragmentScanCallback);
                    }
        });
        return builder.create();
    }

      // George
//    public void startPassiveProtectionScan() {
//        if (BlunoLibrary.mConnectionState != BlunoLibrary.connectionStateEnum.isConnected) {
//
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    BlunoLibrary.mScanning = false;
//                    Log.d("George_debug", "Stopping Passive Scan");
//                    stopPassiveProtectionScan();
//                    startPassiveProtectionScan();
//                }
//            }, BlunoLibrary.SCAN_PERIOD);
//
//            BlunoLibrary.mScanning = true;
//            BlunoLibrary.mBluetoothAdapter.startLeScan(activeFragmentScanCallback);
//
//        } else {
//            Toast.makeText(mainContext, "Your USB Key is already connected and protected",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

    //George - Passive Device scan callback.
    private BluetoothAdapter.LeScanCallback activeFragmentScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
       public void onLeScan(final BluetoothDevice device, final int rssi,byte[] scanRecord) {

            // George
            Log.d("George_debug", "activeFragmentScan Callback - Device value is " + device);
            if (device.toString().equals(BlunoLibrary.BlunoNanoMacAddr)) {


                // George - LOSS LOGIC
                Log.d("George_debug","activeFragmentScan Callback - Device value is " + device);
                if (device.toString().equals(BlunoLibrary.BlunoNanoMacAddr)) {


                } else { // George - If we can't find USB Key, execute this code block

                    ((Activity) mainContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("mLeScanCallback onLeScan cannot find USB Key ");

                        }
                    });
                    return;

                }
                /*((Activity) mainContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("activeFragmentScanCallback!");
                    }
                });*/

            }

       }

    // George
//    public void stopPassiveProtectionScan() {
//        BlunoLibrary.mBluetoothAdapter.stopLeScan(activeFragmentScanCallback);
//
//        ((Activity) mainContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TextView rssi_indicator = (TextView) findViewById(R.id.rssi_indicator);
//                rssi_indicator.setText("00 - Your USB Key is not protected");
//            }
//        });
//        Log.d("George_debug","Starting Passive Protection Scan Again!");

    };



}
