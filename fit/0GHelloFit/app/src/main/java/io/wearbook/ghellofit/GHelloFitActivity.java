package io.wearbook.ghellofit;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.PendingResult;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.fitness.request.BleScanCallback ;
import com.google.android.gms.fitness.request.StartBleScanRequest;
import com.google.android.gms.fitness.data.BleDevice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.wearbook.ghellofit.R ;



public class GHelloFitActivity extends Activity {

    // Marshmallow permissions, check them at runtime, everytime
    private static final String[]  PERMISSIONS_NEEDED  = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.INTERNET
    } ;

    private static  final int CODE_PERMISSIONS = 0;

    private GoogleApiClient googleApiClient;
    private boolean authInProgress = false;
    private OnDataPointListener onDataPointListener;
    private TextView foundDevicesTextView ;
    private BleDevice bleDeviceFound ;
    private MyBleScanCallbackAndHelper bleScanCallback = new MyBleScanCallbackAndHelper() ;
    private MyScanResultCallback scanResultCallback = new MyScanResultCallback();


    private static final int AUTH_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghello_fit);
        Log.d(TAG, "onCreate() at time= " + new Date() + " /  " + System.nanoTime());

        foundDevicesTextView  = (TextView) findViewById( R.id.aTextView) ;

        getActionBar().setIcon(R.drawable.ic_launcher);
        getActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_IN_PROGRESS);
        }


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

          //  checkAddressUserPermissions();
        }


        initGoogleApiClient();

    }

    /*private void checkAddressUserPermissions () {
        ArrayList<String> permissionsToBeRequested = new ArrayList<String>( );
        for  ( String aPermission : PERMISSIONS_NEEDED ) {
            if ( PackageManager.PERMISSION_GRANTED !=  checkSelfPermission( aPermission) ) {
                permissionsToBeRequested.add(aPermission);
            }
        }

        if ( permissionsToBeRequested.size() > 0 ) {
            requestPermissions( permissionsToBeRequested.toArray ( new String [permissionsToBeRequested.size()] )  , CODE_PERMISSIONS);
        }

    }*/



    /*@RequireAndroidApi(23) targetAPI
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ( requestCode == CODE_PERMISSIONS) {

            int idx = 0 ;
            for ( int aGrantResult : grantResults  ) {
                if ( aGrantResult != PackageManager.PERMISSION_GRANTED) {
                    // @TODO TODO   show a dialog that permissions  are needed for reason /specify
                    requestPermissions(  new String[] { permissions[idx]}   , CODE_PERMISSIONS);
                }

                ++idx;
            }


        }

    }*/


    private void initGoogleApiClient() {
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi( Fitness.SENSORS_API)
                 .addApi (Fitness.BLE_API)
                 // author's note :
                 // earlier one could use the  broader  Fitness.API -- as in the now deprecated hellofit example --
                  // However since March 2015/ Google Play Services V 7.x onwards,
                  // changes to GoogleApiClient mandate use of the more spefic
                  // Fitness API such as  Fitness.BLE_API SENSORS_API  Fitness.RECORDING_API
                  // Fitness.HISTORY_API Fitness.SESSIONS_API Fitness.CONFIG_API
                  // No longer can one use Fitness.API
                  //
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

                                pendingResult.setResultCallback(scanResultCallback);
                                
                                
                                findDataSources();
                                
                                registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);
                                Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_HEART_RATE_BPM");
                                registerDataSourceListener(DataType.TYPE_STEP_COUNT_CUMULATIVE);
                                Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_STEP_COUNT_CUMULATIVE");
                                registerDataSourceListener(DataType.TYPE_SPEED);
                                registerDataSourceListener(DataType.TYPE_LOCATION_SAMPLE);
                                Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_LOCATION_SAMPLE");


                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                                Log.w ( TAG, "onConnectionSuspended()... ") ;

                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.w(TAG, "onConnectionSuspended() CAUSE_NETWORK_LOST  bad...");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.w(TAG, "onConnectionSuspended() CAUSE_SERVICE_DISCONNECTED  bad ...");

                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {

                            public void onConnectionFailed(ConnectionResult result) {
                                Log.d(TAG, "addOnConnectionFailedListener() Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {

                                    Log.d(TAG, "addOnConnectionFailedListener() !hasResolution: " + result.toString());

                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            GHelloFitActivity.this, 0).show();
                                    return;
                                }
                                //
                                if (!authInProgress) {
                                    try {
                                        Log.d(TAG, "addOnConnectionFailedListener() authInProgress=" + authInProgress);
                                        authInProgress = true;
                                        result.startResolutionForResult(GHelloFitActivity.this,
                                                AUTH_REQUEST);
                                    } catch (Exception e) {
                                        Log.e(TAG,
                                                "addOnConnectionFailedListener() encountered exception =", e);
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
                .setDataTypes(DataType.TYPE_SPEED)
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {

                        String dataSourcesResultString =  Arrays.deepToString(dataSourcesResult.getDataSources().toArray()) ;
                        Log.d(TAG, "findDataSources onResult()" + dataSourcesResult.getStatus().toString() + " " +   dataSourcesResultString);

                        //dataSourcesResult.getStatus().getStatusCode()

                        addContentToView( dataSourcesResultString  ) ;

                        Log.d(TAG, "findDataSources onResult()" + dataSourcesResult.getDataSources().size());

                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.d(TAG, "findDataSources onResult() " + dataSource);
                            Log.d(TAG, "findDataSources onResult() " + dataSource.getDataType().getName());
                            addContentToView( "dataSource=" + dataSource.getName() + "\n");

                            registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);
                            Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_HEART_RATE_BPM");
                            registerDataSourceListener(DataType.TYPE_STEP_COUNT_CUMULATIVE);
                            Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_STEP_COUNT_CUMULATIVE");
                            registerDataSourceListener(DataType.TYPE_SPEED);
                            registerDataSourceListener(DataType.TYPE_LOCATION_SAMPLE);
                            Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_LOCATION_SAMPLE");



                            /*if (dataSource.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)
                                    && onDataPointListener == null) {
                                Log.d(TAG, "findDataSources onResult() registering dataSource=" + dataSource);
                                registerDataSourceListener(dataSource, DataType.TYPE_HEART_RATE_BPM);

                            }*/

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
                    Log.d(TAG, "registerDataSourceListener onDataPoint L-value= " + field.getName());
                    Log.d(TAG, "registerDataSourceListener onDataPoint R-value=" + aValue);
                    addContentToView( "dataPoint=" + field.getName() + "= " + aValue + "\n") ;



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
                            Log.d(TAG, "unregisterFitnessDataListener()Fitness.SensorsApi.remove isSuccess true ");
                        } else {
                            Log.d(TAG, "unregisterFitnessDataListener()Fitness.SensorsApi.remove isSuccess false");
                        }
                    }
                });

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



    /* some trivial constants, at the end of the file*/
    private static final String AUTH_IN_PROGRESS = "AUTH_IN_PROGRESS";
    private static final String TAG = GHelloFitActivity.class.getName();

}
