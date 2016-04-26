package kinsa.interview.yee.park.callback;

import org.json.JSONException;

import kinsa.interview.yee.park.data.Forecast;

/**
 * Created by Yee on 4/20/16.
 */
public interface YahooWeatherServiceCallBack {
    void onWeatherActionSuccess(Forecast forecast) throws JSONException;

    void onWeatherActionFailed(Exception e);
}
