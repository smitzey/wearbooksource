package io.wearbook.walarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;

/**
 * Created by sanjay on 12/3/15.
 */
public class MyAlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int referenceId = (int) SystemClock.elapsedRealtime() ;
        Log.d ( TAG, "onReceive()... " + referenceId ) ;

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder( context)
                        .setSmallIcon(R.drawable.alarm36)
                        .setContentTitle("WAlarm")
                        .setContentText("WAlarm Alarm was Triggered at "  + new Date() )
                        .setColor( R.color.colorPrimary) ;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify( referenceId, notificationBuilder.build() );

    }

    private static  final String TAG = MyAlarmBroadcastReceiver.class.getName() ;
}
