package googleplay.personal.yee.park.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import googleplay.personal.yee.park.callback.SFGovServiceCallBack;
import googleplay.personal.yee.park.data.ParkInfo;
import kinsa.interview.yee.park.R;

/**
 * Created by Yee on 4/4/16.
 */
public class SFGovService {

    public static final String HTTP_ADDRESS = "https://data.sfgov.org/resource/z76i-7s65.json";

    Context mContext;
    SFGovServiceCallBack mCallback;
    ProgressDialog mDialog;
    AsyncTask mAsyncTask;

    public SFGovService(Context context, SFGovServiceCallBack callback) {
        mContext = context;
        mCallback = callback;
    }

    public void getParkInfo() {
        showProgressDialog(mContext);
        mAsyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) builder.append(line);
                    return builder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return mContext.getString(R.string.server_error);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equals(mContext.getString(R.string.server_error))) mCallback.onSFGovServiceFailed(mDialog);
                else {
                    ArrayList<ParkInfo> dataSet = new ArrayList<>();
                    try {
                        JSONArray jsonData = new JSONArray(s);
                        for (int i = 1; i < jsonData.length(); i++) {
                            dataSet.add(new ParkInfo(jsonData.getJSONObject(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mCallback.onSFGovServiceSuccess(mDialog, dataSet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(HTTP_ADDRESS);
    }

    void showProgressDialog(Context context) {
        mDialog = new ProgressDialog(context);
        mDialog.setMessage(mContext.getString(R.string.park_loading));
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Reload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopService();
                getParkInfo();
            }
        });
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopService();
            }
        });
        mDialog.show();
    }

    public void stopService() {
        if (mAsyncTask != null) mAsyncTask.cancel(true);
        if (mDialog != null) mDialog.dismiss();
    }

    public double getDistance(LatLng StartP, LatLng EndP) {
        // radius of earth in Km
        int Radius = 6371;
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Math.abs(Radius * c);
    }

    public ArrayList<ParkInfo> getParksByDistance(ArrayList<ParkInfo> dataSet, LatLng current, int count) {
        int size = Math.min(count, dataSet.size());
        Map<Double, Integer> mCloseStationMap = new TreeMap<>();
        ArrayList<ParkInfo> mResultStations = new ArrayList<>();
        LatLng end;
        for (int i = 0; i < size; i++) {
            ParkInfo temp = dataSet.get(i);
            end = new LatLng(temp.getLatitude(), temp.getLongitude());
            mCloseStationMap.put(getDistance(current, end), i);
        }
        for (Map.Entry<Double, Integer> map : mCloseStationMap.entrySet()) {
            if (count == 0) break;
            ParkInfo park = dataSet.get(map.getValue());
            park.setDistance(map.getKey());
            mResultStations.add(park);
            count--;
        }
        return mResultStations;
    }

    public ArrayList<ParkInfo> getParksByScale(ArrayList<ParkInfo> dataSet, int count) {
        int size = Math.min(count, dataSet.size());
        Map<Float, Integer> mCloseStationMap = new TreeMap<>(new Comparator<Float>() {
            @Override
            public int compare(Float lhs, Float rhs) {
                return rhs.compareTo(lhs);
            }
        });
        ArrayList<ParkInfo> mResultStations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ParkInfo temp = dataSet.get(i);
            mCloseStationMap.put(temp.getAcreage(), i);
        }
        for (Map.Entry<Float, Integer> map : mCloseStationMap.entrySet()) {
            if (count == 0) break;
            ParkInfo park = dataSet.get(map.getValue());
            park.setAcreage(map.getKey());
            mResultStations.add(park);
            count--;
        }
        return mResultStations;
    }
}

