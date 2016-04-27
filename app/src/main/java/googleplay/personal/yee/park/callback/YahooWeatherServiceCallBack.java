package googleplay.personal.yee.park.callback;

import org.json.JSONException;

import googleplay.personal.yee.park.data.Forecast;

/**
 * Created by Yee on 4/20/16.
 */
public interface YahooWeatherServiceCallBack {

    void onWeatherActionSuccess(Forecast forecast) throws JSONException;

    void onWeatherActionFailed(Exception e);
}
