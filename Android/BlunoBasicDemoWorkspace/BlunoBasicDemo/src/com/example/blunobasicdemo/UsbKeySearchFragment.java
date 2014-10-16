package com.example.blunobasicdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.io.IOException;
import java.util.Locale;

/**
 * This fragment is the popup that notifies the user when the USB Key is out of range or is missing.
 */
public class UsbKeySearchFragment extends DialogFragment {

    private Context mainContext= getActivity();
    private Handler mHandler= new Handler();
    private int numRescan = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.usbkeysearch_layout, null))
        .setTitle("Search for USB Key")
                .setPositiveButton("Try connecting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean reconnectStatus = BlunoLibrary.mBluetoothLeService.connect(BlunoLibrary.BlunoNanoMacAddr);
                        BlunoLibrary.dialogShown = 0;
                        //BlunoLibrary.mConnectionState = BlunoLibrary.connectionStateEnum.isScanning;

                        if (BlunoLibrary.mConnectionState == BlunoLibrary.connectionStateEnum.isToScan) {
                            // Program execution comes here when user clicks on the Try Connecting button and the USB Key is not found
                            BlunoLibrary.mBluetoothAdapter.startLeScan(activeFragmentScanCallback);
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                            alert.setTitle("Cannot connect to USB Key at the moment. See your last location?");
                            //alert.setMessage("Message");
                            // Set an EditText view to get user input
                            //final EditText passwordText = new EditText(getActivity());
                            //alert.setView(passwordText);
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    MainActivity.btnfindusbkey.performClick();

                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                    BlunoLibrary.mScanDeviceDialog.dismiss();
                                }
                            });


                            alert.show();

                            BlunoLibrary.mBluetoothAdapter.stopLeScan(activeFragmentScanCallback);


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


            } else { // George - If we can't find USB Key, execute this code block

                    System.out.println("activeFragmentScan onLeScan cannot find USB Key ");
                    numRescan++;
                    System.out.println("numRescan is:" + numRescan);
                    BlunoLibrary.mBluetoothAdapter.stopLeScan(activeFragmentScanCallback);

                // TODO
                // Add another ReScan dialog after 10 and end scan after that


            }

       };

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
