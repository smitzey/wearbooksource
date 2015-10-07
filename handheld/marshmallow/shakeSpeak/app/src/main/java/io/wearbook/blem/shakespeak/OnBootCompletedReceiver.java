package io.wearbook.blem.shakespeak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.Date;

/**
 * Created by sanjay on 7/3/15.
 */
public class OnBootCompletedReceiver extends  BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive()... detected boot at time" + new Date()  ) ;

        Intent nextIntent = new Intent (  context , ShakeSensorRegistrationService.class) ;
        context.startService(nextIntent) ;

        Log.d(TAG, "onReceive()... started SensorRegistrationService " + new Date()) ;
    }

    private static final String TAG = OnBootCompletedReceiver.class.getName() ;
}
