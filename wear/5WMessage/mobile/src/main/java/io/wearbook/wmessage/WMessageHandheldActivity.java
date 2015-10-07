package io.wearbook.wmessage;

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
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class WMessageHandheldActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {


    private TextView dataTextView ;
    private GoogleApiClient googleApiClient ;
    private boolean authInProgress = false ;
    private static final int RESULT_RESOLUTION = 99 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wmessage_handheld);
        dataTextView = (TextView) findViewById( R.id.messageTextView) ;
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
        googleApiClient = new GoogleApiClient.Builder(
                getApplicationContext())
                .addApi(Wearable.API
                )
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();


        Log.d(TAG, "connectGoogleApiClient() googleApiClient isConnecting=" + googleApiClient.isConnecting()) ;

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "GoogleApiClient.ConnectionCallbacks::onConnected() " + Wearable.API.getName()) ;

        Log.d ( TAG, "GoogleApiClient.ConnectionCallbacks::onConnected() added MessageListener..." ) ;

        Wearable.MessageApi.addListener(googleApiClient, this);


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

        Wearable.MessageApi.removeListener( googleApiClient, this) ;
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

    @Override
    public void onMessageReceived( final MessageEvent messageEvent ) {
        Log.d ( TAG, "onMessageReceived() messageEvent=" + messageEvent) ;
        Log.d(TAG, "onMessageReceived() messageEvent=" + messageEvent.getPath() + " " +  messageEvent.getSourceNodeId() + " " +  new String  (  messageEvent.getData() ) ) ;
        //dataTextView.setText( new String (messageEvent.getData()) );


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // dataTextView.setText(messageEvent.getPath() + messageEvent.getSourceNodeId() + String.valueOf(messageEvent.getData()));
                //dataTextView.setText( String.valueOf(messageEvent.getData() ));
                dataTextView.setText( new String (messageEvent.getData() ));

            }
        });


    }

    private static final String MESSAGE_PATH = "/io.wearbook.wmessage.IMPORTANT_RANDOM_MESSAGE" ;

    private static  final String TAG = WMessageHandheldActivity.class.getName();





}
