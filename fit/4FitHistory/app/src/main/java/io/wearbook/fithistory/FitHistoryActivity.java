package io.wearbook.fithistory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class FitHistoryActivity extends Activity {

    private static final int CODE_PERMISSIONS = 0;

    private GoogleApiClient googleApiClient;
    private boolean authInProgress = false;

    private static final int AUTH_REQUEST = 1;
    private TextView outputTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_history);
        getActionBar().setIcon(R.drawable.ic_launcher);
        getActionBar().setDisplayShowHomeEnabled(true);
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_IN_PROGRESS);
        }

        outputTextView = (TextView) findViewById(R.id.aTextView);

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
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {

                                Log.d(TAG, "initGoogleApiClient() onConnected good...");
                                addContentToView("googleApiClient=" + googleApiClient + "\n");
                                accessFitnessHistory();

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
                                            FitHistoryActivity.this, 0).show();
                                    return;
                                }
                                //
                                if (!authInProgress) {
                                    try {
                                        Log.d(TAG, "addOnConnectionFailedListener() authInProgress=" + authInProgress);
                                        authInProgress = true;
                                        Log.d(TAG, "addOnConnectionFailedListener() attempting startResolutionForResult");
                                        result.startResolutionForResult(FitHistoryActivity.this,
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

    private void accessFitnessHistory() {

        Log.d(TAG, "accessFitnessHistory()...");
        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.set(2014, 4, 4);

        DataReadRequest readDataRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setLimit(18)
                .setTimeRange(startTimeCalendar.getTimeInMillis(), System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> pendingResultDataReadResult = Fitness.HistoryApi.readData(googleApiClient, readDataRequest);

        pendingResultDataReadResult.setResultCallback(new ResultCallback<DataReadResult>() {
                                                          public void onResult(DataReadResult dataReadResult) {

                                                              String historyInfo = Arrays.deepToString(dataReadResult.getDataSets().toArray());

                                                              Log.d(TAG, "accessFitnessHistory() dataReadResult=" + historyInfo);
                                                              addContentToView("DataReadResult = " + historyInfo);
                                                          }
                                                      }
        );
    }


    private synchronized void addContentToView(final String moreContent) {
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

    private static final String AUTH_IN_PROGRESS = "AUTH_IN_PROGRESS";
    private static final String TAG = FitHistoryActivity.class.getName();
}