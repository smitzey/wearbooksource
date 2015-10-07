package com.wearableandroidbook.basicbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BluetoothLeInitialCheckActivity extends Activity {
  
  private BluetoothAdapter bluetoothAdapter ;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }


  @Override
  protected void onResume() {
    super.onResume();   }

  @Override
  protected void onStart() {
    super.onStart(); 
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    Toast.makeText(this, R.string.bluele_not_system_feature, Toast.LENGTH_LONG).show();
    finish();
    }
    // in case its not turned on,
    
    final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

         this.bluetoothAdapter = bluetoothManager.getAdapter();
         
         Log.d ( TAG, "onStart() bluetoothAdapter=" + bluetoothAdapter) ;
         
         if ( ! this.bluetoothAdapter.isEnabled() ) {
           Log.d( TAG, "onStart() this.bluetoothAdapter.isEnabled() false, starting Request") ;
           startActivityForResult( new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), ENABLED_BLE);
         }
         else {
           proceedToFindingDevices () ;
         }
    
}
    
    
    
  
  
  
  protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
    
    Log.d ( TAG, "onActivityResult  " + requestCode + " " +resultCode) ;
    if (requestCode == ENABLED_BLE) {
      
      // start the ble scan activity
      
      proceedToFindingDevices () ;
      
             
    }
    
}
  
  private void proceedToFindingDevices () {
    
    Intent findIntent = new Intent(getApplicationContext(), BluetoothLeFindDevicesActivity.class) ;
    startActivity(findIntent);
    
  }
  
  

    @Override
  protected void onDestroy() {
    super.onDestroy(); 
  }

  @Override
  protected void onStop() {
    super.onStop(); 
  }

  @Override
  protected void onPause() {
    super.onPause();   }

  
  private static final int ENABLED_BLE = 1;
  
  private static final String TAG = BluetoothLeInitialCheckActivity.class.getName() ;
}
