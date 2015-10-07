package io.wearbook.simplerest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

    private ConnectivityManager connectivityManager;
    private Context context;
    private SimpleWeatherVO simpleWeatherVO ;

    private TextView weatherSummaryTextView ;
    private TextView temperatureRangeTextView ;

    //@TODO refresh on ActionBar ?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherSummaryTextView = ( TextView) findViewById( R.id.textViewSummaryWeather) ;
        temperatureRangeTextView = ( TextView) findViewById( R.id.textViewTemperatureInfo) ;

        context = getApplicationContext() ;

    }

    @Override
    protected void onResume() {
        super.onResume();

        new FetchWeatherAsyncTask().execute(); // this is a recycled resource managed by Android
    }

    private void populateResultsInView () {

        if ( simpleWeatherVO != null ) {
            weatherSummaryTextView.setText( simpleWeatherVO.getSummary());
            temperatureRangeTextView.setText( simpleWeatherVO.getTemperatureRange());
        } else {
            // populate some error element in the UI to tell the user there was a problem @TODO
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
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

    private class FetchWeatherAsyncTask extends AsyncTask<Void, Void, String> {

        private boolean connectivityState = false;



        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onCancelled(String result) {
            super.onCancelled(result);
            //showConnectivityDeficientDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // @TODO show spinner

        }

        @Override
        protected String doInBackground(Void... params) {
            String retVal = null;

            Log.d(TAG, "FetchWeatherAsyncTask doInBackground()" );

            // check connectivity etc.  before attempting network call

            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // proceed
                connectivityState = true ;

            } else {
                connectivityState = false ;
                cancel(true);
            }


            try {
                simpleWeatherVO = WeatherDataFetcher.fetchWeatherByZip("90001") ;


            } catch (IOException ioe) {
                Log.e ( TAG, "FetchWeatherAsyncTask doInBackground() exception ioe=" + ioe ) ;
                // cancel and notify user
            }
            catch ( JSONException jse) {
                Log.e ( TAG, "FetchWeatherAsyncTask doInBackground() exception jse=" + jse ) ;
                // cancel and notify user
            }




            return retVal;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //@TODO STOP SPINNER


            populateResultsInView ();

        }



    }

    private static final String TAG = MainActivity.class.getName() ;

}
