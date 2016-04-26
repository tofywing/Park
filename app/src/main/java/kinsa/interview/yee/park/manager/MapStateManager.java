package kinsa.interview.yee.park.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import kinsa.interview.yee.park.data.MapState;

/**
 * Created by Yee on 4/14/16.
 */
public class MapStateManager {
    public static final String PREFS = "sharedPreference";
    public static final String LOCALITY = "locality";
    public static final String MAP_STATE = "mapState";
    public static final String LATITUDE = "savedLatitude";
    public static final String LONGITUDE = "savedLongitude";
    private SharedPreferences mapStatePrefs;

    public MapStateManager(Context context) {
        mapStatePrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveMapState(MapState mapState, double latitude, double longitude, String locality) {
        SharedPreferences.Editor edit = mapStatePrefs.edit();
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        String json = gson.toJson(mapState);
        edit.putString(MAP_STATE, json);
        edit.putFloat(LATITUDE, (float) latitude);
        edit.putFloat(LONGITUDE, (float) longitude);
        edit.putString(LOCALITY, locality);
        edit.apply();
    }

    public MapState getSavedMapState() {
        String json = mapStatePrefs.getString(MAP_STATE, "");
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        return gson.fromJson(json, new TypeToken<MapState>() {
        }.getType());
    }

    public String getSavedLocality() {
        return mapStatePrefs.getString(LOCALITY, "");
    }

    public float getSavedLatitude() {
        return mapStatePrefs.getFloat(LATITUDE, -1);
    }

    public float getSavedLongitude() {
        return mapStatePrefs.getFloat(LONGITUDE, -1);
    }
}
