package io.wearbook.wearablelistener;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

public class MainWearActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView titleTextView;
    private TextView dataTextView ;
    private WatchViewStub stub ;

    private String data ;


    private GoogleApiClient googleApiClient ;

    private boolean authInProgress = false ;
    private static final int RESULT_RESOLUTION = 99 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);

        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                titleTextView = (TextView) stub.findViewById(R.id.headingTextView);
                dataTextView = (TextView) stub.findViewById(R.id.dataTextView);
                Log.d  ( TAG, "onCreate() titleTextView| dataTextView=" + titleTextView + " | " + dataTextView) ;
                dataTextView.setText("D...");

                if ( data!=null) {
                    dataTextView.setText( data) ;
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        data = getIntent().getStringExtra("data") ;
        Log.d ( TAG, "onResume() data=" + data + "/ dataTextView= " + dataTextView   ) ;

        if ( data != null ) {
            if ( data.length()>0) {
                if ( dataTextView!=null) {
                    dataTextView.setText(data);
                }

            }
        }


        connectGoogleApiClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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


    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d( TAG, "GoogleApiClient.ConnectionCallbacks::onConnectionSuspended ..." );

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d( TAG, "GoogleApiClient.ConnectionCallbacks::onConnectionFailed connectionResult=" + connectionResult );
        //connectionResult.getErrorCode()

        if (!connectionResult.hasResolution()) {

            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                    this, 0).show();

        } else   if ( !authInProgress) {
            authInProgress = true ;
            try {

                connectionResult.startResolutionForResult( this, RESULT_RESOLUTION) ;
            } catch ( IntentSender.SendIntentException ise) {
                Log.e ( TAG, "onConnectionFailed exception ise=" + ise ) ;

            }

        }

    }


    private static final String TAG = MainWearActivity.class.getSimpleName() ;
}
