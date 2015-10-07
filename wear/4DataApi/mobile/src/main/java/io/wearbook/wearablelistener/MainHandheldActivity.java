package io.wearbook.wearablelistener;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

public class MainHandheldActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView sentDataTextView ;
    private GoogleApiClient googleApiClient ;
    private boolean authInProgress = false ;
    private static final int RESULT_RESOLUTION = 99 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_handheld);
        sentDataTextView = (TextView) findViewById( R.id.sentDataTextView) ;
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectGoogleApiClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void connectGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();


        Log.d(TAG, "connectGoogleApiClient() googleApiClient isConnecting=" + googleApiClient.isConnecting()) ;

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d( TAG, "GoogleApiClient.ConnectionCallbacks::onConnected() " + Wearable.API.getName()  ) ;

        PutDataRequest putDataRequest = PutDataRequest.create("/io.wearbook.wearablelistener.A_LITTLE_NUGGET_OF_DATA") ;

        StringBuffer data = new StringBuffer("origin:Handheld ").append( new Date()) ;

        putDataRequest.setData(data.toString().getBytes()) ;

        PendingResult <DataApi.DataItemResult> dataItemPendingResult =  Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);

        Log.d(TAG, "GoogleApiClient.ConnectionCallbacks::onConnected() called putDataItem =" + putDataRequest  ) ;

        dataItemPendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "onConnected()-->dataItemPendingResult-->onResult() Status-->getStatus status= " + dataItemResult.getStatus().toString() ) ;
            }
        });

        sentDataTextView.setText(data.toString() ) ;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "GoogleApiClient.ConnectionCallbacks::onConnectionFailed connectionResult=" + connectionResult);
        //connectionResult.getErrorCode()

        if (!connectionResult.hasResolution()) {

            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                    this, 0).show();

        } else if (!authInProgress) {
            authInProgress = true;
            try {

                connectionResult.startResolutionForResult(this, RESULT_RESOLUTION);
            } catch (IntentSender.SendIntentException ise) {
                Log.e(TAG, "onConnectionFailed exception ise=" + ise);

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_handheld, menu);
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

    private static  final String TAG = MainHandheldActivity.class.getName();
}
