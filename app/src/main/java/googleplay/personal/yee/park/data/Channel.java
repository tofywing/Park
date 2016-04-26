package googleplay.personal.yee.park.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Yee on 4/20/16.
 */
public class Channel implements ParseData, Parcelable {
    JSONArray forecastArray;
    JSONObject Channel;

    public Channel() {

    }

    protected Channel(Parcel in) {
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };

    public JSONObject getChannel() {
        return Channel;
    }

    public JSONArray getForecastArray() {
        return forecastArray;
    }

    @Override
    public void parseJSON(JSONObject object) {
        this.Channel = object;
        forecastArray = object.optJSONObject("item").optJSONArray("forecast");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
