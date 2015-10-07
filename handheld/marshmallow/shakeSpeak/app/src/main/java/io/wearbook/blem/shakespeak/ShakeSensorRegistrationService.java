package io.wearbook.blem.shakespeak;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.IBinder;
import android.util.Log;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;

/**
 * Created by sanjay on 6/7/15.
 */

public class ShakeSensorRegistrationService extends Service {

    private SensorManager sensorManager ;

    private Sensor sensorAccelerometer ;
    private Sensor sensorGravity ;
    private Sensor sensorLinearAcceleration ;
    private Sensor sensorSignificantMotion ;

    private Sensor sensorLight ;

    private Thread sensorThread ;

    private Intent handleSignificantMotionIntent ;

    private Context context ;



    public ShakeSensorRegistrationService() {
        Log.d(TAG, "ShakeSensorRegistrationServicee() constructed at " + new Date()) ;






    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        context = getApplicationContext() ;
        handleSignificantMotionIntent = new Intent ( context, SignificantMotionHandlerActivity.class ) ;

        handleSignificantMotionIntent.setAction(Intent.ACTION_MAIN);
        handleSignificantMotionIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        handleSignificantMotionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        handleSignificantMotionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);




        // start the Sensor listener thread or executor service.
        // keep logging periodicall for purposes of debugging ?
        // as long as the executor or thread is alive, run a periodic health check

        // if the time up, the time range  that I am to be up and running ? etc etc.

        //sensorThread = new Thread() ;
        //sensorThread.setName( "SensorThread-" + new Date().toString().replaceAll("\\s", "") );

        //sensorThread.run() ;

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorSignificantMotion = sensorManager.getDefaultSensor( Sensor.TYPE_SIGNIFICANT_MOTION) ;

        SignificantMotionTriggerListener significantMotionTriggerListener = new SignificantMotionTriggerListener();

        sensorManager.requestTriggerSensor (  significantMotionTriggerListener, sensorSignificantMotion  );


        return START_STICKY ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    /*protected  void onHandleIntent (Intent intent) {
        // here, we would like to register a listener for particular types of sensor data
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.getSensorList(Sensor.TYPE_ALL) ;
        sensorAccelerometer = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER) ;
        sensorLight = sensorManager.getDefaultSensor( Sensor.TYPE_LIGHT) ;



    }*/


    class SignificantMotionTriggerListener extends TriggerEventListener {
        public void onTrigger(TriggerEvent event) {

            // being a one shot sensor trigger, it is inherently cancelled
            // if you are interested in more such events, call the request trigger again

            // SensorManager.requestTriggerSensor(this, Motion);

            Log.d ( TAG, "SignificantMotionTriggerListener::onTrigger() ...  " + new Date() ) ;

            //startActivity  (handleSignificantMotionIntent) ;
            //startActivity  (handleSignificantMotionIntent) ;
            //context.startActivity


            final Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                    .setContentTitle( "Motion ! ")
                    .setContentText("Motions !")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_VIBRATE);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(1346436, notificationBuilder.build());

            SignificantMotionTriggerListener significantMotionTriggerListener = new SignificantMotionTriggerListener();

            sensorManager.requestTriggerSensor (  significantMotionTriggerListener, sensorSignificantMotion  );

        }
    }


    private class SensorRegistrar implements  Runnable {

        @Override
        public void run() {

        }
    }


    class MySensorExecutorService implements ExecutorService {

        @Override
        public void shutdown() {

        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> callable) {
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable runnable, T t) {
            return null;
        }

        @Override
        public Future<?> submit(Runnable runnable) {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public void execute(Runnable runnable) {

        }
    }

    private static final String TAG = ShakeSensorRegistrationService.class.getName() ;
}
