package io.wearbook.wearnodes;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Arrays;
import java.util.List;

public class MainWearActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener         {

    private TextView foundNodesTextView ;
    private GoogleApiClient googleApiClient ;
    private NodeApi nodeApi ;
    private Node aNode ;
    private boolean authInProgress = false ;
    private static final int RESULT_RESOLUTION = 99 ;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                foundNodesTextView = (TextView) stub.findViewById(R.id.foundNodesTextView);
            }
        });

        //setTitle( "Wear Nodes");

    }

    @Override
    protected void onStart() {
        super.onStart();

connectGoogleApiClient();

    }

    private void connectGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();


        Log.d( TAG, "connectGoogleApiClient() googleApiClient isConnecting=" + googleApiClient.isConnecting()) ;

    }
    // connect callbacks here for ease of cross referencing
    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle bundle) {
        Log.d( TAG, "GoogleApiClient.ConnectionCallbacks::onConnected() " + Wearable.API.getName()  ) ;

        PendingResult<NodeApi.GetLocalNodeResult> getLocalNodePendingResult =  Wearable.NodeApi.getLocalNode( googleApiClient ) ;

        final StringBuffer nodeInfoText = new StringBuffer() ;


        getLocalNodePendingResult.setResultCallback(new ResultCallback<NodeApi.GetLocalNodeResult>() {
            @Override
            public void onResult(NodeApi.GetLocalNodeResult getLocalNodeResult) {
                Log.d ( TAG, "GoogleApiClient.ConnectionCallbacks::onConnected()-NodeApi.GetLocalNodeResult getLocalNodeResult::onResult getLocalNodeResult= " + getLocalNodeResult ) ;
                aNode = getLocalNodeResult.getNode() ;


                Log.d ( TAG, "GoogleApiClient.ConnectionCallbacks::onConnected()-NodeApi.GetLocalNodeResult getLocalNodeResult::onResult aNode(local)=" + aNode ) ;
                //foundNodesTextView.setText( aNode.toString()) ;
                nodeInfoText.append("Local " + aNode.toString()) ;
                //OUTPUT Node{Gear Live 772A, id=ab1f00d5, hops=0, isNearby=true}
            }
        });


        final PendingResult<NodeApi.GetConnectedNodesResult> getConnectedNodesPendingResult =  Wearable.NodeApi.getConnectedNodes( googleApiClient) ;
        getConnectedNodesPendingResult.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                Log.d ( TAG, "GoogleApiClient.ConnectionCallbacks::onConnected()-NodeApi.GetConnectedNodesCallback::onResult getConnectedNodesPendingResult = " + getConnectedNodesPendingResult ) ;
                List<Node> connectedNodesList =  getConnectedNodesResult.getNodes() ;

                String tmpString = Arrays.deepToString(connectedNodesList.toArray() );
                nodeInfoText.append ( "\n Connected " + tmpString) ;
                //OUTPUT [Node{Nexus 6, id=30680774, hops=1, isNearby=true}, Node{cloud, id=cloud, hops=2, isNearby=false}]

                Log.d ( TAG, "GoogleApiClient.ConnectionCallbacks::onConnected()-NodeApi.GetConnectedNodesCallback::onResult getConnectedNodesPendingResult = " +  tmpString ) ;

                foundNodesTextView.setText( nodeInfoText);

            }
        });


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d( TAG, "GoogleApiClient.ConnectionCallbacks::onConnectionSuspended " );

    }

    //GoogleApiClient.OnConnectionFailedListener
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


    @Override
    protected void onRestart() {

        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    private static  final String TAG = MainWearActivity.class.getName() ;



}
