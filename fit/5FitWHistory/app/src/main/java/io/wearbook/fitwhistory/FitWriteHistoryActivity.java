package io.wearbook.fitwhistory;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.common.api.Status ;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;



/**
 * This example accesses Fitness data via using the Sensors API and writes the data  to the
 * Fit History database/ Fit Store. Such data can be retrived subsequently.
 *
 *
 *
 */

public class FitWriteHistoryActivity extends Activity {


    private static  final int CODE_PERMISSIONS = 0;

    private GoogleApiClient googleApiClient;

    private OnDataPointListener onDataPointListener;

    private boolean authInProgress = false;

    private static final int AUTH_REQUEST = 1;
    private TextView outputTextView  ;

    private ConcurrentLinkedQueue<DataSet> writeQueue = new ConcurrentLinkedQueue<DataSet>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_history);
        getActionBar().setIcon(R.drawable.ic_launcher);
        getActionBar().setDisplayShowHomeEnabled(true);
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_IN_PROGRESS);
        }

        outputTextView = (TextView) findViewById( R.id.aTextView) ;

        setTitle(getTitle() + "- Read");

        initGoogleApiClient();

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    private void initGoogleApiClient() {
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi( Fitness.BLE_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.d(TAG, "initGoogleApiClient() onConnected good...");
                                addContentToView("googleApiClient=" + googleApiClient + "\n");

                                findDataSources();
                                registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);
                                Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_HEART_RATE_BPM");
                                registerDataSourceListener(DataType.TYPE_STEP_COUNT_CUMULATIVE);
                                Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_STEP_COUNT_CUMULATIVE");
                                registerDataSourceListener(DataType.TYPE_SPEED);
                                registerDataSourceListener(DataType.TYPE_LOCATION_SAMPLE);
                                Log.d(TAG, "initGoogleApiClient() onConnected ...registerDataSourceListener TYPE_LOCATION_SAMPLE");


                                //writeSomeFitnessHistory();


                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                                Log.d(TAG, "onConnectionSuspended()... ");

                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.d(TAG, "onConnectionSuspended() network_lost bad...");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.d(TAG, "onConnectionSuspended() service_disconnected bad...");

                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {

                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.d(TAG, "addOnConnectionFailedListener() Connection failed. Cause: " + result.toString());

                                if (!result.hasResolution()) {

                                    Log.d(TAG, "addOnConnectionFailedListener() !hasResolution: " + result.toString());

                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            FitWriteHistoryActivity.this, 0).show();
                                    return;
                                }
                                //
                                if (!authInProgress) {
                                    try {
                                        Log.d(TAG, "addOnConnectionFailedListener() authInProgress=" + authInProgress);
                                        authInProgress = true;
                                        Log.d(TAG, "addOnConnectionFailedListener() attempting startResolutionForResult" );
                                        result.startResolutionForResult(FitWriteHistoryActivity.this,
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

    private void registerDataSourceListener(DataType dataType) {

        onDataPointListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {

                if (dataPoint == null) {
                    Log.d(TAG, "registerDataSourceListener onDataPoint = " + dataPoint);
                    return;
                }

                if ( dataPoint.getDataType().equals( DataType.TYPE_STEP_COUNT_CUMULATIVE) ) {
                    Log.d ( TAG, " onDataPoint of type DataType.TYPE_STEP_COUNT_CUMULATIVE " ) ;

                    for (Field field : dataPoint.getDataType().getFields()) {
                        Value aValue = dataPoint.getValue(field);
                        Log.d(TAG, "registerDataSourceListener onDataPoint L-value= " + field.getName());
                        Log.d(TAG, "registerDataSourceListener onDataPoint R-value=" + aValue);
                        addContentToView( "dataPoint=" + field.getName() + "= " + aValue + "\n") ;

                        // lets write cumulative steps
                        if ( field.getName().equals ("steps") ) {
                            // ideally one should use the same datapoint and add it to the set.
                            // rather than :-
                            DataSet originatedDataSet = originateDataSetCumulativeSteps( aValue.asInt() ) ;
                            writeQueue.add( originatedDataSet) ;
                            writeFitnessHistory();
                        }
                    }

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
                        Log.d(TAG, "findDataSources onResult()" + dataSourcesResult.getStatus().toString() + " " + Arrays.deepToString(dataSourcesResult.getDataSources().toArray()));

                        //dataSourcesResult.getStatus().getStatusCode()

                        Log.d(TAG, "findDataSources onResult()" + dataSourcesResult.getDataSources().size());

                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.d(TAG, "findDataSources onResult() " + dataSource);
                            Log.d(TAG, "findDataSources onResult() " + dataSource.getDataType().getName());
                            addContentToView( "dataSource=" + dataSource.getName() + "\n");

                            /*if (dataSource.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)
                                    && onDataPointListener == null) {
                                Log.d(TAG, "findDataSources onResult() registering dataSource=" + dataSource);
                                registerDataSourceListener(dataSource,
                                       DataType.TYPE_HEART_RATE_BPM);

                            }*/
                        }
                    }
                });

    }

    private void writeFitnessHistory() {

        Log.d ( TAG, "writeFitnessHistory ...") ;
        new WriteFitDataAsyncTask().execute() ;



    }



    private synchronized void addContentToView ( final String moreContent ) {


        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                outputTextView.setText(outputTextView.getText() + moreContent + "\n");

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fit_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private DataSet originateDataSetCumulativeSteps ( int valueStepCountDelta) {

        DataSet retVal = null ;


        Calendar calendar = Calendar.getInstance();

        Date now = new Date();
        calendar.setTime(now);
        long endTime = calendar.getTimeInMillis();


        calendar.add(Calendar.DAY_OF_YEAR, -366 );
        long startTime = calendar.getTimeInMillis();

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setType(DataSource.TYPE_RAW)
                .build();

       // int valueStepCountDelta  = 54321   ;

        retVal = DataSet.create(dataSource);

        DataPoint dataPoint = retVal.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(valueStepCountDelta);
        retVal.add(dataPoint);

        return retVal;
    }



    private class WriteFitDataAsyncTask  extends AsyncTask<Void, Void, String> {

        DataReadRequest readRequest ;
        DataReadResult dataReadResult ;
        DataSet dataSet = null ;

        protected String  doInBackground(Void... params) {
            String retVal = "SUCCESS" ;
            Log.d ( TAG, "WriteFitDataAsyncTask=doInBackground() ...") ;


            while (writeQueue.size() > 0) {
                dataSet = writeQueue.remove() ;
                //PendingResult <Status> status =
                try {
                    com.google.android.gms.common.api.Status status = Fitness.HistoryApi.insertData(googleApiClient, dataSet).await(5, TimeUnit.MINUTES);
                    Log.d ( TAG, "WriteFitDataAsyncTask=doInBackground() Fitness.HistoryApi.insertData attempted... for  " + dataSet) ;
                    Log.d ( TAG, "WriteFitDataAsyncTask=doInBackground() Fitness.HistoryApi.insertData status= " + status ) ;
                    if ( status.isSuccess()) {
                        retVal = "SUCCESS";
                    } else {
                      retVal = "FAILURE_SUBTLE";
                    }



                } catch ( Throwable t ) {
                    Log.e (  TAG, "WriteFitDataAsyncTask=doInBackground() Fitness.HistoryApi.insertData exception=" + t ) ;
                    retVal = "FAILURE_GROSS";
                }
            }




            return retVal;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if ( "SUCCESS".equals(s)) {
                if ( dataSet != null ) {
                    addContentToView("Wrote  dataSet= " + Arrays.deepToString(dataSet.getDataPoints().toArray())  )  ;
                }







            }

        }
    }


    private static final String AUTH_IN_PROGRESS = "AUTH_IN_PROGRESS";
    private static final String TAG = FitWriteHistoryActivity.class.getName();
}