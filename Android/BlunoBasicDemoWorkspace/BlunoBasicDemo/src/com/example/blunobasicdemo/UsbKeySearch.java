package com.example.blunobasicdemo;

import android.app.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.content.DialogInterface;

/**
 * Created by george on 17/8/14.
 */
public class UsbKeySearch extends Activity {

    ImageView signalStatus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usbkeysearch_layout);

        signalStatus = (ImageView) findViewById(R.id.signalStatus);
    }

    @Override
    protected void onDestroy() {

        // TODO Auto-generated method stub
        super.onDestroy();

    }

}
