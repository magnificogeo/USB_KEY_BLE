package com.example.blunobasicdemo;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.*;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import java.util.Locale;

public class MainActivity extends BlunoLibrary {

	public static Button buttonScan;
    private Button buttonProtectUSBKey;
    private Button buttonDecrypt;
    private Button btnfindusbkey;
	private TextView serialReceivedText;
    private TextView rssi_indicator;
    static int silenced = 0;
    public LocationManager mLocManager;
    public DialogFragment dialogFragmentObject;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        dialogFragmentObject = new UsbKeySearchFragment();
        onCreateProcess();														//onCreate Process by BlunoLibrary
        
        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200
		
        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        rssi_indicator = (TextView) findViewById(R.id.rssi_indicator);
        rssi_indicator.setText("Your USB Key is not protected");

        btnfindusbkey = (Button) findViewById(R.id.btnfindusbkey); // initial the button for finding USB Key
        btnfindusbkey.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO Auto-generated method stub
                double sourceLatitude = 1.337225;
                double sourceLongitude = 103.733751;
                double destinationLatitude = 1.438818;
                double destinationLongitude = 103.793489;

                if ( mConnectionState == mConnectionState.isConnected ) {
                    Toast.makeText(MainActivity.this, "USB Key is in range",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", sourceLatitude, sourceLongitude, "Your Current Location", destinationLatitude, destinationLongitude, "USB Key Last Known Location");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(intent);
                }
            }

        });

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});

        buttonDecrypt = (Button) findViewById(R.id.buttonDecrypt);
        buttonDecrypt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                if (mConnectionState != connectionStateEnum.isConnected) {
                    Toast.makeText(MainActivity.this, "Please connect to a USB Key first",
                            Toast.LENGTH_SHORT).show();
                } else {

                    alert.setTitle("Input your passkey to decrypt");
                    //alert.setMessage("Message");
                    // Set an EditText view to get user input
                    final EditText passwordText = new EditText(MainActivity.this);
                    alert.setView(passwordText);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            try {
                                serialSend(passwordText.getText().toString());
                            } catch (Exception io) {
                                //
                            }
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });
                    alert.show();
                }
            }


        });

        buttonScanOnClickProcess();


	}

    // George
//    public void onProtectToggleClicked(View view) {
//
//        // Is the toggle on?
//        boolean on = ((ToggleButton) view).isChecked();
//        if (on) {
//            // Enable vibrate
//            startPassiveProtectionScan();
//            System.out.println("Toggle On");
//        } else {
//            // Disable vibrate
//            stopPassiveProtectionScan();
//            System.out.println("Toggle Off");
//        }
//
//    }

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
            rssi_indicator.setText("You are now connected to your USB Key.");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
            rssi_indicator.setText("00 - Your USB Key is not protected");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
		serialReceivedText.append(theString);							//append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.

	}

    public void onToggleClicked(View view) {

        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            // Enable vibrate
            AudioManager aManager=(AudioManager)getSystemService(AUDIO_SERVICE);
            aManager.setRingerMode(aManager.RINGER_MODE_NORMAL);
            silenced = 1;
            Log.d("George_debug", "Toggle On - Notification Off");
        } else {
            // Disable vibrate
            AudioManager aManager=(AudioManager)getSystemService(AUDIO_SERVICE);
            aManager.setRingerMode(aManager.RINGER_MODE_SILENT);
            silenced = 0;
            Log.d("George_debug","Toggle Off - Notification On");
        }

    }

}