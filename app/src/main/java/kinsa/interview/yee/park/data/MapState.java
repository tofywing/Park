package kinsa.interview.yee.park.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Yee on 4/14/16.
 */
public class MapState implements Parcelable {

    public static final String MARKER_READY = "mapStateMarkerReady";

    CameraPosition cameraPosition;
    ParkInfo parkSelected;
    double meLatitude = -1;
    double meLongitude = -1;
    String meLocality;
    double parkLatitude = -1;
    double parkLongitude = -1;
    String parkLocality;
    int isMePressed = 0;
    int isParkPressed = 0;
    int isListShown = 1;
    int selectedPosition = -1;

    public MapState() {
    }


    protected MapState(Parcel in) {
        cameraPosition = in.readParcelable(CameraPosition.class.getClassLoader());
        parkSelected = in.readParcelable(ParkInfo.class.getClassLoader());
        meLatitude = in.readDouble();
        meLongitude = in.readDouble();
        meLocality = in.readString();
        parkLatitude = in.readDouble();
        parkLongitude = in.readDouble();
        parkLocality = in.readString();
        isMePressed = in.readInt();
        isParkPressed = in.readInt();
        isListShown = in.readInt();
        selectedPosition = in.readInt();
    }

    public static final Creator<MapState> CREATOR = new Creator<MapState>() {
        @Override
        public MapState createFromParcel(Parcel in) {
            return new MapState(in);
        }

        @Override
        public MapState[] newArray(int size) {
            return new MapState[size];
        }
    };

    public void saveMeMarker(Marker marker) {
        LatLng ll = marker.getPosition();
        setMeLatitude(ll.latitude);
        setMeLongitude(ll.longitude);
        setMeLocality(MARKER_READY);
    }

    public void saveParkMarker(Marker marker) {
        LatLng ll = marker.getPosition();
        setParkLatitude(ll.latitude);
        setParkLongitude(ll.longitude);
        setParkLocality(MARKER_READY);
    }

    public CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    public double getMeLatitude() {
        return meLatitude;
    }

    public double getMeLongitude() {
        return meLongitude;
    }

    public String getMeLocality() {
        return meLocality;
    }

    public double getParkLatitude() {
        return parkLatitude;
    }

    public double getParkLongitude() {
        return parkLongitude;
    }

    public String getParkLocality() {
        return parkLocality;
    }

    public void setMeLatitude(double meLatitude) {
        this.meLatitude = meLatitude;
    }

    public void setMeLongitude(double meLongitude) {
        this.meLongitude = meLongitude;
    }

    public void setParkLatitude(double parkLatitude) {
        this.parkLatitude = parkLatitude;
    }

    public void setParkLongitude(double parkLongitude) {
        this.parkLongitude = parkLongitude;
    }

    public void setMeLocality(String meLocality) {
        this.meLocality = meLocality;
    }

    public void setParkLocality(String parkLocality) {
        this.parkLocality = parkLocality;
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public void setIsMePressed(int isMePressed) {
        this.isMePressed = isMePressed;
    }

    public void setIsParkPressed(int isParkPressed) {
        this.isParkPressed = isParkPressed;
    }

    public void setIsListShown(int isListShown) {
        this.isListShown = isListShown;
    }

    public int getIsMePressed() {
        return isMePressed;
    }

    public int getIsParkPressed() {
        return isParkPressed;
    }

    public int getIsListShown() {
        return isListShown;
    }

    public void setParkSelected(ParkInfo parkSelected) {
        this.parkSelected = parkSelected;
    }

    public ParkInfo getParkSelected() {
        return parkSelected;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(cameraPosition, flags);
        dest.writeParcelable(parkSelected, flags);
        dest.writeDouble(meLatitude);
        dest.writeDouble(meLongitude);
        dest.writeString(meLocality);
        dest.writeDouble(parkLatitude);
        dest.writeDouble(parkLongitude);
        dest.writeString(parkLocality);
        dest.writeInt(isMePressed);
        dest.writeInt(isParkPressed);
        dest.writeInt(isListShown);
        dest.writeInt(selectedPosition);
    }
}
