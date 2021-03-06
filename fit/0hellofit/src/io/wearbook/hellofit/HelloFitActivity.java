package io.wearbook.hellofit;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;

import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.request.StartBleScanRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import java.util.concurrent.TimeUnit;
import com.google.android.gms.fitness.request.BleScanCallback ;

public class HelloFitActivity extends Activity  {

  private GoogleApiClient googleApiClient;
  private boolean authInProgress = false;
  private OnDataPointListener onDataPointListener;
  private static final int AUTH_REQUEST = 1;
  private TextView foundDevicesTextView ;
  BleDevice bleDeviceFound ;
  MyBleScanCallbackAndHelper bleScanCallback = new MyBleScanCallbackAndHelper() ;
  MyScanResultCallback scanResultCallback = new MyScanResultCallback();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    Log.d(TAG, "onCreate()");
    foundDevicesTextView  = (TextView) findViewById( R.id.aTextView) ;
    
    if (savedInstanceState != null) {
      authInProgress = savedInstanceState.getBoolean(AUTH_IN_PROGRESS);
    }
    initGoogleApiClient();

  }

  private void initGoogleApiClient() {
    this.googleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Fitness.API)
            .addScope(new Scope(Scopes.FITNESS_BODY_READ))
            .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
            .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
            .addConnectionCallbacks(
                    new GoogleApiClient.ConnectionCallbacks() {

                      @Override
                      public void onConnected(Bundle bundle) {
                        Log.d(TAG, "initGoogleApiClient() onConnected good...");
                        addContentToView("googleApiClient=" + googleApiClient + "\n");

                        PendingResult<Status> pendingResult = Fitness.BleApi.startBleScan(
                                googleApiClient,
                                new StartBleScanRequest.Builder()
                                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                                .setBleScanCallback(bleScanCallback)
                                .build());
                        
                        pendingResult.setResultCallback( scanResultCallback);


                        findDataSources();
                        registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);
                        registerDataSourceListener(DataType.TYPE_STEP_COUNT_CUMULATIVE);
                        //registerDataSourceListener(DataType.TYPE_SPEED);
                        registerDataSourceListener(DataType.TYPE_LOCATION_SAMPLE);

                      }

                      @Override
                      public void onConnectionSuspended(int i) {

                        if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                          Log.d(TAG, "onConnectionSuspended() network_lost bad...");
                        } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                          Log.d(TAG, "onConnectionSuspended() service_disconnected bad...");

                        }
                      }
                    }
            )
            .addOnConnectionFailedListener(
                    new GoogleApiClient.OnConnectionFailedListener() {
                      
                      @Override
                      public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "Connection failed. Cause: " + result.toString());
                        if (!result.hasResolution()) {
                          // Show the localized error dialog
                          GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                  HelloFitActivity.this, 0).show();
                          return;
                        }
                      
                        if (!authInProgress) {
                          try {
                            Log.d(TAG, "Attempting to resolve failed connection");
                            authInProgress = true;
                            result.startResolutionForResult(HelloFitActivity.this,
                                    AUTH_REQUEST);
                          } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG,
                                    "Exception while starting resolution activity", e);
                          }
                        }
                      }
                    }
            )
            .build();
  }

  private void findDataSources() {
    Fitness.SensorsApi.findDataSources(googleApiClient, new DataSourcesRequest.Builder()
            .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
           // .setDataTypes(DataType.TYPE_SPEED)
           // .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .setDataSourceTypes(DataSource.TYPE_RAW)
            .build())
            .setResultCallback(new ResultCallback<DataSourcesResult>() {
              @Override
              public void onResult(DataSourcesResult dataSourcesResult) {
                Log.d(TAG, "findDataSources onResult()" + dataSourcesResult.getStatus().toString());
                
                Log.d(TAG, "findDataSources onResult()" + dataSourcesResult.getDataSources().size());
                
                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                  Log.d(TAG, "findDataSources onResult() " + dataSource);
                  Log.d(TAG, "findDataSources onResult() " + dataSource.getDataType().getName());
                  addContentToView( "dataSource DATATYPE=" + dataSource.getDataType().getName() + "\n");
                  
                  if (dataSource.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)
                  && onDataPointListener == null) {
                    Log.d(TAG, "findDataSources onResult() registering dataSource=" + dataSource);
                    registerDataSourceListener ( DataType.TYPE_HEART_RATE_BPM);
                    
                  }
                }
              }
            });

  }

 // private void registerDataSourceListener(DataSource dataSource, DataType dataType) {
 private void registerDataSourceListener(DataType dataType) {
    onDataPointListener = new OnDataPointListener() {
      @Override
      public void onDataPoint(DataPoint dataPoint) {
        for (Field field : dataPoint.getDataType().getFields()) {
          Value aValue = dataPoint.getValue(field);
          Log.d(TAG, "Detected DataPoint field: " + field.getName());
          Log.d(TAG, "Detected DataPoint value: " + aValue);
          addContentToView( "dataPoint=" + field.getName() + " " + aValue + "\n") ;
        }
      }
    };

    Fitness.SensorsApi.add(
            googleApiClient,
            new SensorRequest.Builder()
          //  .setDataSource(dataSource)
            .setDataType(dataType)
            .setSamplingRate(12, TimeUnit.SECONDS)
            .setAccuracyMode(SensorRequest.ACCURACY_MODE_DEFAULT)        
            .build(),
            onDataPointListener)
            .setResultCallback(new ResultCallback<Status>() {
              @Override
              public void onResult(Status status) {
                if (status.isSuccess()) {
                  Log.d(TAG, "onDataPointListener  registered good");
                } else {
                  Log.d(TAG, "onDataPointListener failed to register bad");
                }
              }
            });

  }
  
  private void unregisterFitnessDataListener() {
        if (this.onDataPointListener == null) {
             return;
        }

        Fitness.SensorsApi.remove(
                this.googleApiClient,
                this.onDataPointListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Listener was removed!");
                        } else {
                            Log.d(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }

  @Override
  protected void onStart() {
    super.onStart();

    this.googleApiClient.connect();
  
    
    Log.d(TAG, "onStart connect attempted");
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (bleDeviceFound != null) {
      bleScanCallback.releaseDevice(bleDeviceFound);
    }
    unregisterFitnessDataListener();

    if (this.googleApiClient.isConnected()) {
      this.googleApiClient.disconnect();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == AUTH_REQUEST) {
      authInProgress = false;
      if (resultCode == RESULT_OK) {

        if (!this.googleApiClient.isConnecting() && !this.googleApiClient.isConnected()) {
          this.googleApiClient.connect();
          Log.d(TAG, "onActivityResult googleApiClient.connect() attempted in background");

        }
      }
    }
  }
  
   private synchronized void addContentToView ( final String moreContent ) {
    
     
    runOnUiThread(new Runnable() {

    @Override
    public void run() {
      
       foundDevicesTextView.setText ( foundDevicesTextView.getText() + moreContent + "\n") ;
      
     }
});
}
   
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(AUTH_IN_PROGRESS, authInProgress);
  }
  
  class MyScanResultCallback implements ResultCallback {

    public void onResult(Result r) {
      Log.d ( TAG , "MyScanResultCallback onResult result = " + r) ;
      if ( r != null ) {
      Log.d ( TAG , "MyScanResultCallback onResult result = " + r.getStatus()) ; 
     
      
      }
      
    }
    
  }
  
   class MyBleScanCallbackAndHelper extends BleScanCallback  {

    @Override
    public void onDeviceFound(BleDevice device) {
      Log.d (  TAG, "MyBleScanCallbackAndHelper:onDeviceFound found device=" + device ) ;
      
      bleDeviceFound = device ; // add to list of devices TODO
      
      
      addContentToView("MyBleScanCallbackAndHelper:onDeviceFound foundBleDevice=" + device + "\n");
      
     
      //findDataSources();
      //registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);
      
    }

       @Override
       public void onScanStopped() {
           Log.d(TAG, "MyBleScanCallback:onScanStopped...");
           if (bleDeviceFound != null) {

               Fitness.BleApi.claimBleDevice(googleApiClient, bleDeviceFound).await();

        //  PendingResult<Status> pendingResult =
               //               Fitness.BleApi.claimBleDevice(googleApiClient, bleDeviceFound);
               //Status status =       pendingResult.await() ;
      // resolve unsuccessful statu here ....
               //Log.d (TAG, "MyBleScanCallbackAndHelper:onScanStopped claim attempt status=" + status ) ;
               //  error checks to be done here ...
           }
           
           findDataSources();
       }
    
    void releaseDevice ( BleDevice device ) {
      try {
      Fitness.BleApi.unclaimBleDevice(googleApiClient, device);
      } catch ( Throwable t ) {
        //ignore
      }
    }
  
}

  private static final String AUTH_IN_PROGRESS = "AUTH_IN_PROGRESS";

  private static final String TAG = HelloFitActivity.class.getName();
}
