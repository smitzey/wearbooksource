package io.wearbook.simplerest;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sanjay on 7/23/15.
 *
 *  * pull JSON from a weather api. Use http://openweathermap.org/current
 * for Los angeles zip 90001 city hall, center of city
 *
 * http://api.openweathermap.org/data/2.5/weather?zip=90001,us

 */

public class WeatherDataFetcher {

    // @TODO, separate base url from location, for support for dynamic input city /location of interest
    // private static final BASE_URL_STRING = "http://api.openweathermap.org/data/2.5/weather" ;
    private static final String urlString  = "http://api.openweathermap.org/data/2.5/weather?zip=90001,us" ;
    private static URL url = null ;
    private static HttpURLConnection httpUrlConnection ;
    private static InputStream inputStream ;
    private static BufferedInputStream bufferedInputStream ;

    // timeouts, @TODO optimize timeouts
    private  static  final int TIMEOUT_CONNECTION = 2*60*1000 ;
    private  static  final int TIMEOUT_READ_DATA  = 3*60*1000 ;


    /**
     * @param zipCode
     * @return
     * @throws IOException
     * @throws JSONException
     */

    //@TODO - use parameter in the long run, currently ignored
    public static SimpleWeatherVO fetchWeatherByZip ( String zipCode) throws IOException, JSONException  {
     SimpleWeatherVO retVal = null ;

        retVal = new SimpleWeatherVO( fetchWeatherByZipCodeAsJson(zipCode)) ;

        return retVal ;
    }



    /**
     *
     * @param zipCode  - zip code of interest, for weather data
     * @return
     * @throws IOException
     * @throws JSONException
     */

    //@TODO - use parameter in the long run, currently ignored
    public static JSONObject fetchWeatherByZipCodeAsJson ( String zipCode) throws IOException, JSONException {
        JSONObject retVal = null  ;

        retVal = new JSONObject( fetchWeather( zipCode)) ;

        return retVal ;
    }

    //@TODO - use parameter in the long run, currently ignored
    public static String fetchWeather( String zipCode) throws IOException {
        String retVal = "dummy return";

        int httpStatusCode = -1 ;


        try {
            url = (new URL ( urlString)) ;
            Log.d(TAG, "fetch() url=" + url) ;
            httpUrlConnection = (HttpURLConnection)url.openConnection() ;
            Log.d(TAG, "fetch() httpUrlConnection=" + httpUrlConnection) ;
            httpUrlConnection.setConnectTimeout(TIMEOUT_CONNECTION);
            httpUrlConnection.setReadTimeout( TIMEOUT_READ_DATA);

            httpStatusCode = httpUrlConnection.getResponseCode() ;
            Log.d ( TAG, "fetch() httpStatusCode= " + httpStatusCode) ;

            inputStream = new BufferedInputStream(httpUrlConnection.getInputStream()) ;
            Log.d ( TAG, "fetch() inputStream= " + inputStream) ;
            retVal = new Scanner(inputStream).useDelimiter("\\A").next();





        } catch ( IOException ioe ) {
            Log.e ( TAG ,"fetch()  exception ioe=" + ioe) ;


        } finally {
            // close resources here
            try {

                if ( inputStream != null) {
                    inputStream.close();
                }

                if (httpUrlConnection!=null) {
                    httpUrlConnection.disconnect();
                }
            } catch ( Throwable t ) { ; } // nothing to do here
        }

        Log.d ( TAG, "fetch retVal=" + retVal) ;



        return retVal ;
    }


    private static final String TAG = WeatherDataFetcher.class.getName() ;

}
