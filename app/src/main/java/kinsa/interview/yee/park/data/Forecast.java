package kinsa.interview.yee.park.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Yee on 4/20/16.
 */
public class Forecast implements ParseData, Parcelable {

    private String day;
    private String date;
    private int high = 0;
    private int low = 0;
    private String inGeneral;
    //"city,state"
    private String location;

    private int code = -1;

    public Forecast() {

    }

    protected Forecast(Parcel in) {
        day = in.readString();
        date = in.readString();
        high = in.readInt();
        low = in.readInt();
        inGeneral = in.readString();
        code = in.readInt();
        location = in.readString();
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public int getHigh() {
        return high;
    }

    public int getLow() {
        return low;
    }

    public String getInGeneral() {
        return inGeneral;
    }

    public int getCode() {
        return code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void parseJSON(JSONObject object) {
        this.day = object.optString("day");
        this.date = object.optString("date");
        this.high = object.optInt("high");
        this.low = object.optInt("low");
        this.inGeneral = object.optString("text");
        this.code = object.optInt("code");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(day);
        dest.writeString(date);
        dest.writeInt(high);
        dest.writeInt(low);
        dest.writeString(inGeneral);
        dest.writeInt(code);
    }
}
