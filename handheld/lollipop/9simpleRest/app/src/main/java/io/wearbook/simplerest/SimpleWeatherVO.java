package io.wearbook.simplerest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * This is a very very simple VO ( value object ) that encapsulates the weather info.
 * summary
 * temperature
 * <p/>
 * Created by sanjay on 7/23/15.
 */
public class SimpleWeatherVO {

    private String summary; // like cloudy, rainy, windy etc. summary description

    private String temperatureRange; // like low 45 F, high 66 F

    public SimpleWeatherVO() {
    }

    public SimpleWeatherVO(String jsonString) throws JSONException {

    }

    //
    //"weather":[
    // {
    //    "id":804,
    //     "main":"Clouds",
    //    "description":"overcast clouds",
    //    "icon":"04d"
    //}
    //"main":{
    //"temp":293.54,
    // "pressure":1014,
    // "humidity":78,
    // "temp_min":290.93,
    // "temp_max":295.15
    // },

    // kelvin × 9/5 - 459.67

    public SimpleWeatherVO(JSONObject weatherJson) throws JSONException {
        JSONArray jsonArray = null ;
        JSONObject tempJSONObject = null;
        StringBuilder tempData = new StringBuilder();

        double kelvin;
        double farenheit;
        DecimalFormat decimalFormat = null;

        // Value [{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}] at weather of type org.json.JSONArray cannot be converted to JSONObject

        //tempJSONObject = weatherJson.getJSONObject("weather");

        jsonArray = ( JSONArray) weatherJson.getJSONArray("weather") ;

        Log.d( TAG ,  "SimpleWeatherVO jsonArray=" + jsonArray ) ;


        for(int idx=0; idx< jsonArray.length(); idx++) {
            tempJSONObject = jsonArray.getJSONObject( idx) ;
            Log.d (TAG, "SimpleWeatherVO jsonArray/jsonObject=" + tempJSONObject) ;

        }


        tempData.append(tempJSONObject.getString("main"));
        tempData.append(" [ ");
        tempData.append(tempJSONObject.getString("description"));
        tempData.append(" ]");

        this.summary = tempData.toString();
        tempData.setLength(0); // has the effect of clearing the stringBuilder

        tempJSONObject = weatherJson.getJSONObject("main");
        kelvin = tempJSONObject.getDouble("temp_min");
        farenheit = ((kelvin * (9F) / (5F)) - 459.67);
        decimalFormat = new DecimalFormat("000");
        tempData.append(decimalFormat.format(farenheit));
        tempData.append(" ,  ");

        kelvin = tempJSONObject.getDouble("temp_max");
        farenheit = ((kelvin * (9F) / (5F)) - 459.67);
        decimalFormat = new DecimalFormat("000");
        tempData.append(decimalFormat.format(farenheit));

        tempData.append ( " (Farenheit)") ;

        this.temperatureRange = tempData.toString();


    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTemperatureRange() {
        return temperatureRange;
    }

    public void setTemperatureRange(String temperatureRange) {
        this.temperatureRange = temperatureRange;
    }

    private static final String TAG = SimpleWeatherVO.class.getName() ;

}

/*
         {
        "coord":{
        "lon":-118.24,
        "lat":33.97
        },
        "weather":[
        {
        "id":804,
        "main":"Clouds",
        "description":"overcast clouds",
        "icon":"04d"
        }
        ],
        "base":"cmc stations",
        "main":{
        "temp":293.54,
        "pressure":1014,
        "humidity":78,
        "temp_min":290.93,
        "temp_max":295.15
        },
        "wind":{
        "speed":0.92,
        "deg":242.502
        },
        "clouds":{
        "all":90
        },
        "dt":1437663189,
        "sys":{
        "type":1,
        "id":396,
        "message":0.0112,
        "country":"US",
        "sunrise":1437656307,
        "sunset":1437706805
        },
        "id":7261268,
        "name":"Florence-Graham",
        "cod":200
        }
*/
