package io.wearbook.wearablelistener;

import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

/**
 * Created by sanjay on 7/26/15.
 */
public class MyWearableListenerService extends WearableListenerService {

    private GoogleApiClient googleApiClient ;


    public MyWearableListenerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()...") ;

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
        Log.d(TAG, "onCreate()...googleApiClient.isConnected ? = " + googleApiClient.isConnected()) ;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "onStartCommand()...") ;

        return START_STICKY;
    }



    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()...") ;

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind()...") ;
        super.onRebind(intent);
    }



    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...") ;
        super.onDestroy();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        Log.d(TAG, "onDataChanged()... dataEvents=" + dataEvents.iterator().toString());

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();

        DataItem dataItem = null ;

        for (DataEvent event : events) {

            Log.d(TAG, "onDataChanged data=" + event.getDataItem().toString() + " [ " + event.getDataItem().getUri() + " ]");
            //: onDataChanged()... dataEvents=com.google.android.gms.common.data.zzb@30a943a6
//            D/io.wearbook.wearablelistener.MyWearableListenerService(16068): onDataChanged data=DataItemEntity{ uri=wear://30680774/io.wearbook.wearablelistener.A_LITTLE_NUGGET_OF_DATA, dataSz=44, numAssets=0 } [ wear://30680774/io.wearbook.wearablelistene.A_LITTLE_NUGGET_OF_DATA ]

            Uri uri = event.getDataItem().getUri();
            byte[] payload = uri.toString().getBytes();

            String host = uri.getHost() ;

            //String data = host + new String( payload) ;
            dataItem = event.getDataItem() ;
            String data = new String ( dataItem.getData() )  ;

            //String data = event.getDataItem().getData().toString() ;

            Log.d (  TAG, "onDataChanged data=" + data ) ;

            Intent startActivityIntent = new Intent(this, MainWearActivity.class);
            startActivityIntent.putExtra( "data", data) ;
            startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startActivityIntent);


        }


    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(TAG, "onMessageReceived()...") ;
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.d(TAG, "onPeerConnected()...") ;
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.d(TAG, "onPeerDisconnected()...") ;
    }

    @Override
    public void onConnectedNodes(List<Node> connectedNodes) {
        super.onConnectedNodes(connectedNodes);
        Log.d(TAG, "onConnectedNodes() ...") ;
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        super.onCapabilityChanged(capabilityInfo);
        Log.d(TAG, "onCapabilityChanged() ...") ;
    }

    @Override
    public void onChannelOpened(Channel channel) {
        super.onChannelOpened(channel);
        Log.d(TAG, "onChannelOpened() ...") ;
    }

    @Override
    public void onChannelClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        super.onChannelClosed(channel, closeReason, appSpecificErrorCode);
        Log.d(TAG, "onChannelClosed() ...") ;
    }

    @Override
    public void onInputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        super.onInputClosed(channel, closeReason, appSpecificErrorCode);
        Log.d(TAG, "onInputClose() ...") ;
    }

    @Override
    public void onOutputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        super.onOutputClosed(channel, closeReason, appSpecificErrorCode);
        Log.d(TAG, "onOutputClosed() ...") ;
    }


    private static final String TAG = MyWearableListenerService.class.getName();
}
