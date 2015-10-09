package io.wearbook.wlist;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WListWearActivity extends Activity implements WearableListView.ClickListener {

    private WearableListView wearableListView ;
    private List<String> listSensors = new ArrayList<String>() ;
    private MyWearableListViewAdapter myWearableListViewAdapter ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlist_wear);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableListView = (WearableListView) stub.findViewById(R.id.wearableListView1);
                populateSensorInfo();
                LayoutInflater layoutInflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
                myWearableListViewAdapter = new MyWearableListViewAdapter( getApplicationContext() ,layoutInflater , listSensors);
                wearableListView.setAdapter(myWearableListViewAdapter);
            }
        });



    }


    private void populateSensorInfo () {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List <Sensor> sensors = sensorManager.getSensorList( Sensor.TYPE_ALL) ;

        if ( sensors!= null) {
            Log.d( TAG, "populateSensorInfo()... found number of sensors=" + sensors.size()) ;
        } else {
            Log.d( TAG, "populateSensorInfo()... found no sensors (strange/unexpected)" ) ;
        }

        for ( Sensor aSensor : sensors ) {
            listSensors.add ( aSensor.getName()) ;
        }
    }


    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

        Log.d (TAG, "onClick() viewHolder="+ viewHolder.getItemId()) ;

    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    private static final String TAG = WListWearActivity.class.toString() ;
}
