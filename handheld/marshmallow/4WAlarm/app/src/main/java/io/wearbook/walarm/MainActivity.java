package io.wearbook.walarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager ;
    private PendingIntent operationPendingIntent ;
    private static  final long MILLIS_IN_A_SECOND = 1000 ;
    private static  final long SECONDS_IN_MINUTE = 60 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startAlarm () ;

                Snackbar.make(view, "Alarm Started", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private  void startAlarm ()  {

        Log.d ( TAG, "startAlarm()...") ;
        operationPendingIntent = PendingIntent.getBroadcast( getApplicationContext(), 0,
                new Intent( getApplicationContext(), MyAlarmBroadcastReceiver.class) , 0);


        alarmManager=  ( AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime () + 3* SECONDS_IN_MINUTE *  MILLIS_IN_A_SECOND,
                operationPendingIntent );

        Log.d ( TAG, "startAlarm() setExact alarm for 3 minutes later...") ;
        // in real world applications, exact alarms should be avoided as much as possible for
        // efficiency reasons...

    }


    // trivial/ routine constants at end of file.
    private static final String TAG = MainActivity.class.getName() ;
}
