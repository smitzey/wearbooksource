package io.wearbook.wearnotification;

import android.app.Notification;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

/**
 * This example demonstrates basic synched notifications across  handheld and Wear devices by using
 * the WearableExtender.
 *
 * This example is trivial and the next example builds on top of this to create more sophisticated
 * interactions.
 *
 * @author  Sanjay M. Mishra, Wearable Android - Android Wear and Google Fit / WILEY 2015
 *
*/


public class HandheldNotificationActivity extends AppCompatActivity {

    private final int NOTIFICATION_ID = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld_notification);

        fireNotification() ;
    }

    private void fireNotification () {

        NotificationCompat.WearableExtender wearableExtender =  new NotificationCompat.WearableExtender() ;

        Notification notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.notification_text))
                        .setSmallIcon(R.drawable.ic_notification )
                        .extend(wearableExtender)
                        .setColor( getResources().getColor(R.color.colorPrimary))
                        .build();

        NotificationManagerCompat notificationManager =   NotificationManagerCompat.from(this);
        notificationManager.notify( NOTIFICATION_ID, notification);

    }


    private static final String TAG = HandheldNotificationActivity.class.toString() ;
}
