package io.wearablebook.voicenoti;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;



/**
 * This example demonstrates  extended notification's remote input in action,
 * and also the base Android platform's Text to Speech feature.
 *
 * @author  Sanjay M. Mishra, Wearable Android - Android Wear and Google Fit / WILEY 2015
 *
 */


public class NotiVoiceActivity extends AppCompatActivity implements
        android.speech.tts.TextToSpeech.OnInitListener {

    private static final int REQUEST_CODE_REMOTE_INPUT= 9 ;
    private static final int NOTIFICATION_ID = 1;
    private static final String REMOTE_INPUT_ACTION = "io.wearablebook.voicenoti.REMOTE_INPUT";
    private PendingIntent remoteInputPendingIntent ;

    private TextView textViewFromWear;
    private TextToSpeech textToSpeech;
    private String fromWear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_voice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewFromWear = (TextView)findViewById( R.id.textViewfromWear) ; // remote input text
        setupRemoteInputReceiver();
        fireNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        fromWear = intent.getStringExtra(MyRemoteInputBroadcastReceiver.TEXT_FROM_WEAR_EXTRA);
        Log.d(TAG, "onResume() fromWearable=" + fromWear);
        if (fromWear != null) {
            textViewFromWear.setText(fromWear);
        }
        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    protected void onStop() {
        textToSpeech.shutdown();
        super.onStop();
    }

    private void setupRemoteInputReceiver() {
        Intent remoteInputIntent = new Intent(this, MyRemoteInputBroadcastReceiver.class) ;
        // intent filter action, declared in manifest
        remoteInputIntent.setAction(REMOTE_INPUT_ACTION) ;
        // l-value representing the voice input
        remoteInputIntent.putExtra(MyRemoteInputBroadcastReceiver.VOICE_INPUT_KEY, "Speak") ;

        remoteInputPendingIntent= PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE_REMOTE_INPUT, remoteInputIntent, PendingIntent.FLAG_ONE_SHOT) ;

    }

    private void fireNotification() {

        RemoteInput remoteInput = new RemoteInput.Builder(MyRemoteInputBroadcastReceiver.VOICE_INPUT_KEY).
                setLabel( getResources().getString( R.string.app_name)).build() ;

        NotificationCompat.Action wearRemoteInputAction =  new NotificationCompat.Action
                .Builder ( R.drawable.ic_launcher, "Speak", remoteInputPendingIntent)
                .addRemoteInput(remoteInput).build() ;


        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        wearableExtender.addAction(  wearRemoteInputAction) ;

        Notification notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.notification_text))
                        .setSmallIcon(R.drawable.ic_notification)
                        .extend(wearableExtender)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_noti_voice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onInit(int status) {

        if (fromWear != null) {
            textToSpeech.setLanguage(Locale.getDefault());
            textToSpeech.speak(fromWear, TextToSpeech.QUEUE_ADD, null);
            textToSpeech.setLanguage(Locale.GERMAN);

        }
    }

    private static final String TAG = NotiVoiceActivity.class.toString();
}
