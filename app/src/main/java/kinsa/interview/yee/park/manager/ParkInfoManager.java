package kinsa.interview.yee.park.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import kinsa.interview.yee.park.data.ParkInfo;

/**
 * Created by Yee on 4/4/16.
 */
public class ParkInfoManager {

    public static final String PREFS = "parkInfoPrefs";
    public static final String FILTER_TOKEN = "filterSwitchTokenForList";
    public static final String LIST_BY_DISTANCE = "parkListByDistance";
    public static final String LIST_BY_SCALE = "parkListByScale";

    SharedPreferences mParkPrefs;

    public ParkInfoManager(Context context) {
        mParkPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveParkListWithToken(ArrayList<ParkInfo> listByDistance, ArrayList<ParkInfo> listByScale, int token) {
        SharedPreferences.Editor editor = mParkPrefs.edit();
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        String json = gson.toJson(listByDistance);
        editor.putString(LIST_BY_DISTANCE, json);
        json = gson.toJson(listByScale);
        editor.putString(LIST_BY_SCALE, json);
        editor.putInt(FILTER_TOKEN,token);
        editor.apply();
    }

    public ArrayList<ParkInfo> getSavedParkListByDistance() {
        String json = mParkPrefs.getString(LIST_BY_DISTANCE, "");
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        return gson.fromJson(json, new TypeToken<ArrayList<ParkInfo>>() {
        }.getType());
    }

    public ArrayList<ParkInfo> getSavedParkListByScale() {
        String json = mParkPrefs.getString(LIST_BY_SCALE, "");
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        return gson.fromJson(json, new TypeToken<ArrayList<ParkInfo>>() {
        }.getType());
    }

    public int getSavedFilterToken(){
        return mParkPrefs.getInt(FILTER_TOKEN,1);
    }
}
