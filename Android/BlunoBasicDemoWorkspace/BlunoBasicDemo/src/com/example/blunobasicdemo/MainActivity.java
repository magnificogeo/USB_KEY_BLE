package com.example.blunobasicdemo;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.*;
import android.location.*;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.*;

import java.util.List;
import java.util.Locale;

public class MainActivity extends BlunoLibrary {

	public static Button buttonScan;
    private Button buttonDecrypt;
    public static Button btnfindusbkey;
	private TextView serialReceivedText;
    private TextView rssi_indicator;
    static int silenced = 0;
    Geocoder geocoder;
    String bestProvider;
    List<Address> user = null;

    public DialogFragment dialogFragmentObject;

    public static double sourceLatitude = 1.337225;
    public static double sourceLongitude = 103.733751;
    public static double destinationLatitude = 1.299360;
    public static double destinationLongitude = 103.771132;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        dialogFragmentObject = new UsbKeySearchFragment();
        onCreateProcess();														//onCreate Process by BlunoLibrary

        serialBegin(9600);													//set the UART Baudrate on BLE chip to 9600

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Keep app window alive!

        serialReceivedText = (TextView) findViewById(R.id.serialReceivedText);	//initial the EditText of the received data
        serialReceivedText.setMovementMethod(new ScrollingMovementMethod());
        rssi_indicator = (TextView) findViewById(R.id.rssi_indicator);
        rssi_indicator.setText("Your USB Key is not protected");

        btnfindusbkey = (Button) findViewById(R.id.btnfindusbkey); // initial the button for finding USB Key
        btnfindusbkey.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO Auto-generated method stub

                if ( mConnectionState == mConnectionState.isConnected ) {
                    Toast.makeText(MainActivity.this, "USB Key is in range",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", MainActivity.sourceLatitude, MainActivity.sourceLongitude, "Your Current Location", MainActivity.destinationLatitude, MainActivity.destinationLongitude, "USB Key Last Known Location");
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=%f,%f (%s)", MainActivity.destinationLatitude, MainActivity.destinationLongitude, "USB Key Last Known Location");
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
            // Send connected to Ian
            serialSend("usb_key_connected\r\n");

            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            bestProvider = lm.getBestProvider(criteria, false);
            Location location = lm.getLastKnownLocation(bestProvider);

            if (location == null){
                //Toast.makeText(this,"Location Not found",Toast.LENGTH_LONG).show();
            }else{
                geocoder = new Geocoder(this);
                try {
                    user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    MainActivity.destinationLatitude=(double)user.get(0).getLatitude();
                    MainActivity.destinationLongitude=(double)user.get(0).getLongitude();
                    Log.d("George_debug"," DDD lat: " +MainActivity.destinationLatitude+",  longitude: "+MainActivity.destinationLongitude);

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
            rssi_indicator.setText("You are not connected to your USB Key.");
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