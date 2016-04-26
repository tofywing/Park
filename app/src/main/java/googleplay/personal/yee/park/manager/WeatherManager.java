package googleplay.personal.yee.park.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import googleplay.personal.yee.park.data.Forecast;

/**
 * Created by Yee on 4/21/16.
 */
public class WeatherManager {

    public static final String PREFS = "sharedPreference";
    public static final String PREFS_HEADER = "weather";

    SharedPreferences mPrefs;

    public WeatherManager(Context context) {
        mPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveWeatherInfo(Forecast forecast) {
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        String json = gson.toJson(forecast);
        editor.putString(PREFS_HEADER + forecast.getLocation(), json);
        editor.apply();
    }

    public Forecast getSavedForecast(String location) {
        String json = mPrefs.getString(PREFS_HEADER + location, "");
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        return gson.fromJson(json, new TypeToken<Forecast>() {
        }.getType());
    }
}
