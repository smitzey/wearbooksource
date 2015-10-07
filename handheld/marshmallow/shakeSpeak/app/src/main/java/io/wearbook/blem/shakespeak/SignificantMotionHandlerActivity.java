package io.wearbook.blem.shakespeak;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.hardware.Camera;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by sanjay on 5/31/15
 */
public class SignificantMotionHandlerActivity extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);








        /**
        final Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                .setContentTitle( "Motion !!! ")
                .setContentText("Motion ")
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_VIBRATE);


        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(1346436, notificationBuilder.build());
         */

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d( TAG, "onStart ...") ;

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        String[] cameraList = null ;

        try {
            cameraList = cameraManager.getCameraIdList() ;
            Log.d( TAG, "onStart cameraList = " + Arrays.deepToString(cameraList) ) ;



        } catch (CameraAccessException cameraAccessException ) {
            // disabled etc. handle these issues on first run

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
//        camera.stopPreview();
//        camera.release();

        super.onPause() ;
    }

    @Override
    protected void onStop() {


        super.onStop() ;
    }

    private static final String TAG = SignificantMotionHandlerActivity.class.getName() ;

}
