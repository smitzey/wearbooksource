package com.wearableandroidbook.basicbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This activity does not perform any checks regarding Bluetooth availability on the device
 * 
 * @author sanjay
 */


public class BluetoothLeFindDevicesActivity extends Activity implements BluetoothAdapter.LeScanCallback {

  private BluetoothAdapter bluetoothAdapter ;
  private TextView foundDevicesTextView ;
  private Queue<BluetoothDevice> deviceQueue = new LinkedList<BluetoothDevice>();

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    foundDevicesTextView  = (TextView) findViewById( R.id.aTextView) ;
    

  }
  
  private void startLeScan () {
    final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

    this.bluetoothAdapter = bluetoothManager.getAdapter();
    
    Log.d ( TAG, "startLeScan() ... ") ;
    this.bluetoothAdapter.startLeScan(this) ;
    
  }
  
  private void stopLeScan () {
    this.bluetoothAdapter.stopLeScan(this);
  }

 //device	Identifies the remote device
//rssi	The RSSI value for the remote device as reported by the Bluetooth hardware. 0 if no RSSI value is available.
//scanRecord	The content of the advertisement record offered by the remote device.
  public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    
    Log.d ( TAG, "onLeScan() device=" + device +"scanRecord=" + new String (scanRecord) ) ;
   
    addDeviceToView(  device);
    
    

  }
  
  
  
  private synchronized void addDeviceToView ( final BluetoothDevice  device ) {
    
     
    runOnUiThread(new Runnable() {

    @Override
    public void run() {
      
       foundDevicesTextView.setText ( foundDevicesTextView.getText() + device.getName() + " [ " + device.getAddress() + " ]" + "\n") ;
      
     }
});
    
   
  
    
    
  }

  @Override
  public void onBackPressed() {
   finish();
  }


  @Override
  protected void onStop() {
    stopLeScan () ;
    
    super.onStop(); 
  }

  @Override
  protected void onStart() {
    super.onStart();
    startLeScan () ;
  
  }
  
  
  
  
  
  private static final String TAG = BluetoothLeFindDevicesActivity.class.getName() ;
}
