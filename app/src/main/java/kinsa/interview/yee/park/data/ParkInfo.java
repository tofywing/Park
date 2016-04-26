package kinsa.interview.yee.park.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yee on 4/4/16.
 */
public class ParkInfo implements Parcelable, ParseData {

    String parkName;
    String street;
    String city;
    String state;
    String zip;
    double latitude;
    double longitude;
    String type;
    float acreage = 0;
    String manager;
    String phone;
    String email;
    int id = 0;
    int position;
    int isSelected = 0;
    double distance;
    int tempHigh;
    int tempLow;
    int weatherCode;

    public ParkInfo(JSONObject jsonObject) {
        parseJSON(jsonObject);
    }

    protected ParkInfo(Parcel in) {
        parkName = in.readString();
        street = in.readString();
        city = in.readString();
        state = in.readString();
        zip = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        type = in.readString();
        acreage = in.readFloat();
        manager = in.readString();
        phone = in.readString();
        email = in.readString();
        id = in.readInt();
        position = in.readInt();
        isSelected = in.readInt();
        tempHigh = in.readInt();
        tempLow = in.readInt();
        weatherCode = in.readInt();
        distance = in.readDouble();
    }

    public static final Creator<ParkInfo> CREATOR = new Creator<ParkInfo>() {
        @Override
        public ParkInfo createFromParcel(Parcel in) {
            return new ParkInfo(in);
        }

        @Override
        public ParkInfo[] newArray(int size) {
            return new ParkInfo[size];
        }
    };

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip() {
        return zip;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAcreage() {
        return acreage;
    }

    public void setAcreage(float acreage) {
        this.acreage = acreage;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manger) {
        this.manager = manger;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setTempLow(int tempLow) {
        this.tempLow = tempLow;
    }

    public int getTempLow() {
        return tempLow;
    }

    public void setTempHigh(int tempHigh) {
        this.tempHigh = tempHigh;
    }

    public int getTempHigh() {
        return tempHigh;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parkName);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zip);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(type);
        dest.writeFloat(acreage);
        dest.writeString(manager);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeInt(id);
        dest.writeInt(isSelected);
        dest.writeInt(position);
        dest.writeInt(tempLow);
        dest.writeInt(tempHigh);
        dest.writeInt(weatherCode);
        dest.writeDouble(distance);
    }

    @Override
    public void parseJSON(JSONObject object) {
        parkName = object.optString("parkname").toUpperCase();
        street = "N/A";
        city = "N/A";
        state = "N/A";
        JSONObject locationObject = object.optJSONObject("location_1");
        if (locationObject != null) {
            latitude = locationObject.optDouble("latitude");
            longitude = locationObject.optDouble("longitude");
            String addressTemp = locationObject.optString("human_address");
            addressTemp = addressTemp.replace("\\", "");
            try {
                JSONObject addressJson = new JSONObject(addressTemp);
                street = addressJson.optString("address");
                city = addressJson.optString("city");
                state = addressJson.optString("state");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        zip = object.optString("zipcode");
        type = object.optString("parktype");
        acreage = (float) object.optDouble("acreage");
        manager = object.optString("psamanager");
        phone = object.optString("number");
        email = object.optString("email");
        id = object.optInt("parkid");
    }
}
