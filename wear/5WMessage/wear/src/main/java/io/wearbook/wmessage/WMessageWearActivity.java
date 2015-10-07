package io.wearbook.wmessage;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.security.SecureRandom;

public class WMessageWearActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

   // private TextView titleTextView;
    private TextView dataTextView ;
    private WatchViewStub stub ;

    private String message ;




    private GoogleApiClient googleApiClient ;

    private boolean authInProgress = false ;
    private static final int RESULT_RESOLUTION = 99 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wmessage_wear);

        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
               // titleTextView = (TextView) stub.findViewById(R.id.headingTextView);
                dataTextView = (TextView) stub.findViewById(R.id.text);
                //Log.d(TAG, "onCreate() titleTextView| dataTextView=" + titleTextView + " | " + dataTextView) ;

                message =  String.valueOf( new SecureRandom().nextInt()) ;


                if ( message!=null) {
                    dataTextView.setText(message) ;
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




        connectGoogleApiClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
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

        // send the message
        sendMessage( message);


    }

    private void sendMessage ( final String message ) {

        Log.d ( TAG, "sendMessage() gonna attempt sending message=" + message) ;

        new Thread( new Runnable() {
            @Override
            public void run() {

                NodeApi.GetConnectedNodesResult connectedNodesResult =
                        Wearable.NodeApi.getConnectedNodes( googleApiClient ).await() ;

                Log.d ( TAG, "sendMessage() connectedNodesResult | status |    connectedNodesResult.size" + connectedNodesResult + " | status= " + connectedNodesResult.getStatus() +
                        " |  " +  connectedNodesResult.getNodes().size()) ;

                for(Node aNode : connectedNodesResult.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            googleApiClient, aNode.getId(), MESSAGE_PATH, message.getBytes() ).await();

                    Log.d ( TAG, "sendMessage() sent message --> nodeId/displayName/isNearby " + message + "--> [ " + aNode.getId() + "," + aNode.getDisplayName() +
                            ", " + aNode.isNearby()  + " ]"  )  ;


                }


            }
        }).start();


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

    private static final String MESSAGE_PATH = "/io.wearbook.wmessage.IMPORTANT_RANDOM_MESSAGE" ;

    private static final String TAG = WMessageWearActivity.class.getName() ;



}
