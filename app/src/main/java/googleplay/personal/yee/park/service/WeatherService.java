package googleplay.personal.yee.park.service;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import kinsa.interview.yee.park.R;
import googleplay.personal.yee.park.callback.YahooWeatherServiceCallBack;
import googleplay.personal.yee.park.data.Channel;
import googleplay.personal.yee.park.data.Forecast;

/**
 * Created by Yee on 4/20/16.
 */
public class WeatherService {

    YahooWeatherServiceCallBack mCallback;
    Context mContext;
    String mLocation;

    public WeatherService(Context context, YahooWeatherServiceCallBack callback) {
        this.mCallback = callback;
        this.mContext = context;
    }

    public void getWeatherInfo(String location) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                mLocation = params[0];
                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo" +
                        ".places(1) where text=\"%s\")", params[0]);
                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri
                        .encode(YQL));
                try {
                    URL url = new URL(endpoint);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder data = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) data.append(line);
                    return data.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return mContext.getString(R.string.service_error);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonData = new JSONObject(s);
                    JSONObject parseQuery = jsonData.optJSONObject("query");
                    int count = parseQuery.optInt("count");
                    //Check general input error including empty city and state
                    if (count == 0) {
                        mCallback.onWeatherActionFailed(new Exception());
                    } else {
                        Channel channel = new Channel();
                        channel.parseJSON(parseQuery.optJSONObject("results").optJSONObject("channel"));
                        Forecast forecast = new Forecast();
                        forecast.parseJSON(channel.getForecastArray().optJSONObject(0));
                        forecast.setLocation(mLocation);
                        mCallback.onWeatherActionSuccess(forecast);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(location);
    }
}
