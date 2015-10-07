package io.wearbook.blem.blem0;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class BleScanScanActivity extends Activity  {
        //implements BluetoothAdapter.LeScanCallback {

    private BluetoothManager bluetoothManager ;
    private BluetoothAdapter bluetoothAdapter ;
    private BluetoothLeScanner bluetoothLeScanner ;
    private MyScanCallback    scanCallback ;
    private SortedMap <String, BluetoothDevice> foundBluetoothDevices = new TreeMap<String, BluetoothDevice>();
    
       // BluetoothDevice encompasses BLE devices via  DEVICE_TYPE_LE

    /**  BLE permission grant status via Android OS*/
    private static final int BLE_PERMISSION_GRANTED_RESULT = 91 ;
    /**  BLE ble enablement status via Android OS*/
    private static final int BLE_ENABLED_RESULT = 81 ;

    private TextView textViewFoundBluetoothDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewFoundBluetoothDevices = (TextView) findViewById( R.id.aTextView) ;
    }


    @Override
    protected void onStart() {
        super.onStart(); // super-start first
        startLeScan () ;  // followed by this-start operations
    }

    @Override
    protected void onStop() {
        stopLeScan () ; // this-stop operations first,
        super.onStop(); // followed by super-stop
    }

    @Override
    public void onBackPressed() {
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {


        switch ( resultCode ) {

            case BLE_ENABLED_RESULT :
                // continue with the BLE SCAN, calling this method will result in the
                // scanning operations to proceed.
                startLeScan();
                break;

            default:
                // show a toast with an appropriate method such as
                // in this current situation "Bluetooth LE needs to be enabled in order to
                // discover BLE devices and talk to them
                Toast.makeText(getApplicationContext(), "Bluetooth LE needs to be enabled in order to discover Bluetooth LE devices interact with them", Toast.LENGTH_LONG ) ;

        }




    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case BLE_PERMISSION_GRANTED_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // proceed with scan operations
                    startLeScan();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth LE permissions need to be granted in order to discover Bluetooth LE devices and interact with them", Toast.LENGTH_LONG ) ;
                }

             break;

            default :
                //


        }
    }


    ///// BLE operations/implementation  here
    private void startLeScan () {

        // CAMERA ALL GOOD, Settings --> Apps --> App --> Permissions shows
/*        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    BLE_PERMISSION_GRANTED_RESULT);
        } YES SETTINGS SHOWS status*/

/** individual permissions
 if (  checkSelfPermission(Manifest.permission.BLUETOOTH)  != PackageManager.PERMISSION_GRANTED  ) {
 requestPermissions(new String[]{ Manifest.permission.BLUETOOTH },
 BLE_PERMISSION_GRANTED_RESULT);
 }


 if (  checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN)  != PackageManager.PERMISSION_GRANTED  ) {
 requestPermissions(new String[]{ Manifest.permission.BLUETOOTH_ADMIN },
 BLE_PERMISSION_GRANTED_RESULT);
 }

 **** */

        //  check if the permission has been accepted, as a precursor to attempting any BLE operation
        // else App crashes are possible, likely ...
        if (getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "startLeScan about to requestPermissions BLUETOOTH, BLUETOOTH_ADMIN...");
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, BLE_PERMISSION_GRANTED_RESULT);
        }

        // you should perform this check everytime and attempt to  cache the permission acceptance status within the App
        //  because the user can remove a permission previously granted, from outside of the App via Settings --> App
        // --> Permissions at any time !
        //


        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        }

        if (bluetoothAdapter == null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }


        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        } else {
            // the hardware must have bluetooth however it must also be enabled.
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLE_ENABLED_RESULT);
            // this engages the Android platform to request the user to turn on Bluetooth
        }

        if (bluetoothLeScanner == null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            Log.d(TAG, "startLeScan() bluetoothLeScanner=" + bluetoothLeScanner);
        }


        if (scanCallback == null) {

           scanCallback = new MyScanCallback();
           bluetoothLeScanner.startScan( scanCallback);

        }
    }

    private void stopLeScan () {

        //if ( bluetoothManager != null ){
            //bluetoothManager.shutdown
        //}

        try {
            if ( bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(scanCallback);
            }
            if ( bluetoothAdapter !=null) {
                bluetoothAdapter.getBluetoothLeScanner().stopScan(null);
                // STOPSHIP: 6/19/
            }
        } catch ( Throwable t) {
            //ignore
        }



    }

     private synchronized void refreshView ( final Map<String, BluetoothDevice> foundBluetoothDevices ) {


         runOnUiThread(new Runnable() {

             @Override
             public void run() {

                 textViewFoundBluetoothDevices.setText("Found Devices:\n" +  Arrays.deepToString(foundBluetoothDevices.entrySet().toArray()) ) ;



                 

             }
         });
     }

    private void handleScanResult (  ScanResult scanResult) {

        BluetoothDevice aBluetoothDevice = scanResult.getDevice() ;

        Log.d (TAG, "handleScanResult aBluetoothDevice =" + aBluetoothDevice.toString()) ;

        foundBluetoothDevices.put(aBluetoothDevice.getAddress(), aBluetoothDevice) ;

        refreshView(foundBluetoothDevices);

        aBluetoothDevice.getName() ;
        aBluetoothDevice.getAddress() ;
        aBluetoothDevice.getBluetoothClass() ;

        ScanRecord aScanRecord =  scanResult.getScanRecord() ;
        int signalStrengthDbm =  scanResult.getRssi() ;
        // signal strength in dBm.
        //dBmW or Decibel-milliwatts),  power ratio in decibels (dB) of the measured power
        // referenced to one milliwatt (mW)

        scanResult.getTimestampNanos() ;


    }


    class MyScanCallback extends  ScanCallback {


        public void onScanResult (int callbackType, ScanResult scanResult) {
            //CALLBACK_TYPE_ALL_MATCHES is currently the only type of callback

            handleScanResult ( scanResult) ;

        }

        public void onBatchScanResults (List<ScanResult> results) {

        }

        public void onScanFailed (int errorCode) {
            // its more important to focus on and understand the errors and their causes
            // as it is to successfully connect ...
            // observing the details of the errors and  identifying persist error conditions  is the
            // first step...


            switch ( errorCode ) {

                case SCAN_FAILED_FEATURE_UNSUPPORTED :
                    Log.e ( TAG, "onScanFailed errorCode=SCAN_FAILED_FEATURE_UNSUPPORTED Fails to start power optimized scan as this feature is not supported") ;
                    break;

                case SCAN_FAILED_ALREADY_STARTED :
                    Log.e ( TAG, "onScanFailed error= SCAN_FAILED_ALREADY_STARTED Fails to start scan as BLE scan with the same settings is already started by the app") ;
                    break;

                case SCAN_FAILED_INTERNAL_ERROR :
                    Log.e ( TAG, "onScanFailed SCAN_FAILED_INTERNAL_ERROR") ;
                    break ;

                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED :
                    Log.e ( TAG, "onScanFailed error SCAN_FAILED_APPLICATION_REGISTRATION_FAILED  Fails to start scan as app cannot be registered.") ;
                    break;

                default :
                    Log.e ( TAG, "onScanFailed could not classify error code(UNEXPECTED but not necessarily FATAL)") ;


            }
        }

    }




    /**
     * Callback reporting an LE device found during a device scan initiated
     * by the {@link BluetoothAdapter#startLeScan} function.
     *
     * @param device     Identifies the remote device
     * @param rssi       The RSSI value for the remote device as reported by the
     *                   Bluetooth hardware. 0 if no RSSI value is available.
     * @param scanRecord The content of the advertisement record offered by
     */
    //@Override
    //public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
 //   }


    private static final String TAG=  BleScanScanActivity.class.getName() ;
    // it can be useful to use the fully qualified classname when studying the long term behaviour of operations
    // by  collecting adb logcat long duration large files.

}
