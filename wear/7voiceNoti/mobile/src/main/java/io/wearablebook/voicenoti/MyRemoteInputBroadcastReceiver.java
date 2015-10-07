package io.wearablebook.voicenoti;


import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyRemoteInputBroadcastReceiver extends BroadcastReceiver {

    static final String VOICE_INPUT_KEY = "VOICE_INPUT_KEY" ;
    static final String TEXT_FROM_WEAR_EXTRA = "TEXT_FROM_WEAR_EXTRA" ;


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive()... intent=" + intent) ;

        Bundle remoteVoiceInputBundle =  RemoteInput.getResultsFromIntent(intent) ;

        String voiceInputAsText = null ;

        try {
            voiceInputAsText = remoteVoiceInputBundle.getCharSequence( VOICE_INPUT_KEY).toString() ;
        } catch ( Throwable t) {
            Log.w ( TAG, "onReceive()... throwable/exception=" + t) ;
        }


        Log.d(TAG, "onReceive()... received from Wear device, voiceInputAsText=" + voiceInputAsText) ;

        if ( voiceInputAsText != null ) {
            startActivity ( context, voiceInputAsText ) ;
        }



    }

    private void startActivity ( Context context,  String textToDisplayAndSpeak ) {
        Intent activityIntent = new Intent(context, NotiVoiceActivity.class ) ;

        activityIntent.putExtra(TEXT_FROM_WEAR_EXTRA, textToDisplayAndSpeak) ;

        activityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) ;
        activityIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) ;
        context.startActivity( activityIntent);
    }


    private static final String TAG =  MyRemoteInputBroadcastReceiver.class.getName() ;
}
