package kinsa.interview.yee.park.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kinsa.interview.yee.park.adapter.ParkAdapter;
import kinsa.interview.yee.park.callback.SFGovServiceCallBack;
import kinsa.interview.yee.park.callback.YahooWeatherServiceCallBack;
import kinsa.interview.yee.park.data.Forecast;
import kinsa.interview.yee.park.data.MapState;
import kinsa.interview.yee.park.data.ParkInfo;
import kinsa.interview.yee.park.manager.MapStateManager;
import kinsa.interview.yee.park.manager.ParkInfoManager;
import kinsa.interview.yee.park.R;
import kinsa.interview.yee.park.manager.ScreenAppearanceManager;
import kinsa.interview.yee.park.manager.WeatherManager;
import kinsa.interview.yee.park.service.ImageService;
import kinsa.interview.yee.park.service.SFGovService;
import kinsa.interview.yee.park.service.WeatherService;

/**
 * Created by Yee on 4/4/16.
 */
public class GoogleMapFragment extends ListFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SFGovServiceCallBack, YahooWeatherServiceCallBack {

    public static final int GPS_ERROR_DIALOG_REQUEST = 9001;
    public static final float DEFAULT_ZOOM = 9.5f;
    public static final float PARK_ZOOM = 12.0f;
    public static final String WIFI_CHECKING_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String MAIN_PREFERENCE = "mainPreference";
    public static final String WIFI_DISABLED = "wifiDisabled";
    public static final String ME_POSITION = "myCurrentLocationLatLng";

    GoogleApiClient mClient;
    LocationRequest mRequest;
    ProgressBar mProgressBar;
    SupportMapFragment mMapFragment;
    GoogleMap mMap;
    Marker mMeMarker;
    Marker mParkMarker;
    FloatingActionButton mReloadButton;
    FloatingActionButton mListButton;
    FloatingActionButton mMeButton;
    FloatingActionButton mParkButton;
    Switch mFilterSwitch;
    TextView mFilterTitle;
    BroadcastReceiver mReceiver;
    SharedPreferences mPrefs;
    SFGovService mSFGovService;
    ImageService mImageService;
    WeatherService mWeatherService;
    ScreenAppearanceManager mScreenManager;
    boolean wifiDisabled = false;
    ArrayList<ParkInfo> mParkListSelect;
    ArrayList<ParkInfo> mParkListByDistance;
    ArrayList<ParkInfo> mParkListByAlphabet;
    FragmentManager mFragmentManager;
    RelativeLayout mListContainer;
    RelativeLayout mListUpContainer;
    View mAdapterContainer;
    MapStateManager mMapStateManager;
    ParkInfoManager mParkManager;
    WeatherManager mWeatherManager;
    boolean isListShown;
    boolean isMePressed;
    boolean isParkPressed;
    int byDistanceToken;
    MapState mMapState;
    ParkInfo mParkSelected;
    int mParkSelectedPosition;
    double mCurrentLatitude;
    double mCurrentLongitude;
    String mCurrentLocality;
    CameraPosition mCurrentCameraPosition;
    ParkAdapter mAdapter;
    boolean snapShotIsReady = false;
    LatLng meLatLng;
    ParkInfoFragment mParkInfoFragment;
    int time;
    int[][] colorPattern = new int[][]{new int[]{Color.parseColor("#80FF0000")}, new int[]{Color.parseColor
            ("#80FFFFFF")}, new int[]{Color.parseColor
            ("#8000ff00")}, new int[]{Color.parseColor
            ("#80007fff")}, new int[]{Color.parseColor
            ("#80000000")}, new int[]{Color.parseColor
            ("#90FF4B00")}};
    ColorStateList red = new ColorStateList(colorPattern, colorPattern[0]);
    ColorStateList white = new ColorStateList(colorPattern, colorPattern[1]);
    ColorStateList green = new ColorStateList(colorPattern, colorPattern[2]);
    ColorStateList blue = new ColorStateList(colorPattern, colorPattern[3]);
    ColorStateList black = new ColorStateList(colorPattern, colorPattern[4]);
    ColorStateList orange = new ColorStateList(colorPattern, colorPattern[5]);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mListContainer = (RelativeLayout) view.findViewById(R.id.listContainer);
        ViewGroup.LayoutParams params1 = mListContainer.getLayoutParams();
        params1.width = ScreenAppearanceManager.listWidth;
        mListContainer.setLayoutParams(params1);
        mAdapterContainer = inflater.inflate(R.layout.adapter_park, container, false);
        mListUpContainer = (RelativeLayout) view.findViewById(R.id.listUpContainer);
        ViewGroup.LayoutParams params2 = mListUpContainer.getLayoutParams();
        params2.height = ScreenAppearanceManager.listTitleHeight;
        mListUpContainer.setLayoutParams(params2);
        mListUpContainer.setBackgroundResource(ScreenAppearanceManager.imageId);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mapProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        if (serviceAvailable())
            mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks
                    (this).addOnConnectionFailedListener(this).build();
        mMapStateManager = new MapStateManager(getContext());
        mParkManager = new ParkInfoManager(getContext());
        mFragmentManager = getActivity().getFragmentManager();
        mWeatherManager = new WeatherManager(getActivity());
        mScreenManager = new ScreenAppearanceManager(getActivity());
        mSFGovService = new SFGovService(getContext(), this);
        mImageService = new ImageService(getActivity());
        mWeatherService = new WeatherService(getActivity(), this);
        mPrefs = getContext().getSharedPreferences(MAIN_PREFERENCE, Context.MODE_PRIVATE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context
                        .CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo == null || networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                    showAlert(getString(R.string.alert_wifi_disabled), getString(R.string.confirm));
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean(WIFI_DISABLED, wifiDisabled = true);
                    editor.apply();
                    FloatingActionButton container = initWifiSign();
                    container.startAnimation(AnimationUtils.loadAnimation(
                            getActivity(), android.R.anim.fade_in));
                    container.setVisibility(View.VISIBLE);
                } else {
                    if (mPrefs.getBoolean(WIFI_DISABLED, false)) {
                        Snackbar.make(getView(), getString(R.string.wifi_enable), Snackbar.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean(WIFI_DISABLED, wifiDisabled = false);
                        editor.apply();
                        FloatingActionButton container = initWifiSign();
                        container.startAnimation(AnimationUtils.loadAnimation(
                                getActivity(), android.R.anim.fade_out));
                        container.setVisibility(View.GONE);
                        mSFGovService.getParkInfo();
                        if (mParkMarker != null) mParkMarker.remove();
                        try {
                            getCurrentLocation();
                            cameraUpdate(meLatLng.latitude, meLatLng.longitude, DEFAULT_ZOOM);
                            setMeMarker("", meLatLng.latitude, meLatLng.longitude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        mReloadButton = (FloatingActionButton) view.findViewById(R.id.reloadButton);
        mReloadButton.setBackgroundTintList(black);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSFGovService.getParkInfo();
                if (mParkMarker != null) mParkMarker.remove();
                if (meLatLng != null) cameraUpdate(meLatLng.latitude, meLatLng.longitude, DEFAULT_ZOOM);
            }
        });
        mListButton = (FloatingActionButton) view.findViewById(R.id.listButton);
        mListButton.setBackgroundTintList(orange);
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setListShown(isListShown = !isListShown);
            }
        });
        mMeButton = (FloatingActionButton) view.findViewById(R.id.meButton);
        mMeButton.setBackgroundTintList(blue);
        mMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getCurrentLocation() != null) {
                        mCurrentLatitude = meLatLng.latitude;
                        mCurrentLongitude = meLatLng.longitude;
                        cameraUpdate(mCurrentLatitude, mCurrentLongitude, DEFAULT_ZOOM);
                        setMeMarker("", meLatLng.latitude, meLatLng.longitude);
                        mCurrentCameraPosition = mMap.getCameraPosition();
                        isMePressed = true;
                        isParkPressed = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mParkButton = (FloatingActionButton) view.findViewById(R.id.parkButton);
        mParkButton.setBackgroundTintList(green);
        mParkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParkSelected != null) {
                    cameraUpdate(mParkSelected.getLatitude(), mParkSelected.getLongitude(), PARK_ZOOM);
                    mCurrentCameraPosition = mMap.getCameraPosition();
                    isParkPressed = true;
                    isMePressed = false;
                } else {
                    showAlert(getString(R.string.alert_park_button), getString(R.string.confirm));
                }
            }
        });
        mFilterTitle = (TextView) view.findViewById(R.id.filterTile);
        mFilterSwitch = (Switch) view.findViewById(R.id.filterSwitch);
        mFilterSwitch.setSwitchMinWidth(ScreenAppearanceManager.switchBarLength);
        mFilterSwitch.getTrackDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), android.R.color
                .transparent, null), PorterDuff.Mode.OVERLAY);
        mFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mParkListSelect != null && mParkSelectedPosition != -1) {
                    ParkInfo parkInfo = mParkListSelect.get(mParkSelectedPosition);
                    parkInfo.setIsSelected(0);
                    mParkListSelect.set(mParkSelectedPosition, parkInfo);
                }
                if (!isChecked) {
                    byDistanceToken = 1;
                    mParkListSelect = mParkListByDistance;
                    mAdapter = new ParkAdapter(getActivity(), R.layout.fragment_map, mParkListSelect,
                            ScreenAppearanceManager.adapterHeight);
                    setListAdapter(mAdapter);
                    mFilterTitle.setText(getString(R.string.filter_title_by_distance));
                } else {
                    byDistanceToken = 0;
                    mParkListSelect = mParkListByAlphabet;
                    mAdapter = new ParkAdapter(getActivity(), R.layout.fragment_map, mParkListSelect,
                            ScreenAppearanceManager.adapterHeight);
                    setListAdapter(mAdapter);
                    mFilterTitle.setText(getString(R.string.filter_tile_by_alphabet));
                }
                if (mParkMarker != null) mParkMarker.remove();
                if (meLatLng != null) cameraUpdate(meLatLng.latitude, meLatLng.longitude, DEFAULT_ZOOM);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);
        setGPSInterval();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest
                .permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMapFetch();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        dataRestore();
        mClient.connect();
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(WIFI_CHECKING_ACTION);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        mClient.disconnect();
        super.onStop();
        mMapState.setIsMePressed(isMePressed ? 1 : 0);
        mMapState.setIsParkPressed(isParkPressed ? 1 : 0);
        mMapState.setIsListShown(isListShown ? 1 : 0);
        mMapState.setCameraPosition(mMap.getCameraPosition());
        mMapState.setParkSelected(mParkSelected);
        mMapState.setSelectedPosition(mParkSelectedPosition);
        mMapStateManager.saveMapState(mMapState, mCurrentLatitude, mCurrentLongitude, mCurrentLocality);
        mParkManager.saveParkListWithToken(mParkListByDistance, mParkListByAlphabet, byDistanceToken);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        String json = gson.toJson(meLatLng);
        editor.putString(ME_POSITION, json);
        editor.apply();
        getActivity().unregisterReceiver(mReceiver);
    }

    private Address getCurrentLocation() throws IOException {
        if (!wifiDisabled) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest
                    .permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
            if (currentLocation != null) {
                meLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                Geocoder gc = new Geocoder(getContext());
                List<Address> addresses = gc.getFromLocation(meLatLng.latitude, meLatLng.longitude, 1);
                Address address = addresses.get(0);
                mCurrentLocality = address.getLocality();
                return address;
            } else {
                Snackbar.make(getView(), getText(R.string.last_location_failed), Snackbar.LENGTH_LONG).show();
                return null;
            }
        }
        return null;
    }

    @Override
    public void onSFGovServiceSuccess(ProgressDialog dialog, ArrayList<ParkInfo> dataSet) throws IOException {
        getCurrentLocation();
        if (meLatLng != null) mParkListByDistance = mSFGovService.getParksByDistance(dataSet, meLatLng, dataSet.size());
        mParkListByAlphabet = dataSet;
        if (byDistanceToken == 1) mParkListSelect = mParkListByDistance;
        else mParkListSelect = mParkListByAlphabet;
        mAdapter = new ParkAdapter(getActivity(), R.layout.fragment_map, mParkListSelect, ScreenAppearanceManager
                .adapterHeight);
        setListAdapter(mAdapter);
        setListShown(isListShown);
        removeSavedParkMarker();
        Snackbar.make(getView(), getContext().getText(R.string.park_ready), Snackbar.LENGTH_LONG).show();
        dialog.dismiss();
    }

    @Override
    public void onSFGovServiceFailed(ProgressDialog dialog) {
        if (mParkListSelect != null) {
            mAdapter = new ParkAdapter(getActivity(), R.layout.fragment_map, mParkListSelect, ScreenAppearanceManager
                    .adapterHeight);
            mAdapter.notifyDataSetChanged();
            setListAdapter(mAdapter);
            setListShown(isListShown);
            removeSavedParkMarker();
            showAlert(getString(R.string.alert_server_error_saved), getString(R.string.confirm));
        } else {
            showAlert(getString(R.string.alert_server_error_unsaved), getString(R.string.confirm));
        }
        dialog.dismiss();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParkInfo parkInfo;
        if (mParkSelectedPosition != -1) {
            parkInfo = mParkListSelect.get(mParkSelectedPosition);
            parkInfo.setIsSelected(0);
            mParkListSelect.set(mParkSelectedPosition, parkInfo);
        }
        parkInfo = mParkListSelect.get(position);
        parkInfo.setIsSelected(1);
        mParkListSelect.set(position, parkInfo);
        mParkSelectedPosition = position;
        mAdapter.notifyDataSetChanged();
        mParkSelected = mParkListSelect.get(position);
        double distance = mParkSelected.getDistance();
        if (distance > 200) {
            AlertDialog.Builder builder = new AlertDialog.Builder
                    (getActivity());
            builder.setCancelable(false);
            builder.setMessage(getString(R.string
                    .alert_server_date_incorrect, distance));
            builder.setNegativeButton(getString(R.string.cancel), new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int
                                which) {
                            dialog.dismiss();
                        }
                    });
            builder.setPositiveButton(getString(R.string.confirm), new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int
                                which) {
                            listClickAction();
                        }
                    });
            builder.show();
        } else {
            listClickAction();
        }
    }

    void setMeMarker(String locality, double lat, double lng) {
        if (mMeMarker != null) {
            mMeMarker.remove();
        }
        MarkerOptions options = new MarkerOptions().position(new LatLng(lat, lng)).icon
                (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMeMarker = mMap.addMarker(options);
        mMapState.saveMeMarker(mMeMarker);
    }

    void setParkMarker(String locality, double lat, double lng) {
        if (mParkMarker != null) mParkMarker.remove();
        MarkerOptions options = new MarkerOptions().position(new LatLng(lat, lng)).icon
                (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mParkMarker = mMap.addMarker(options);
        mMapState.saveParkMarker(mParkMarker);
    }

    void showAlert(String message, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void cameraUpdate(double latitude, double longitude, float zoom) {
        LatLng ll = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(update);
    }

    public void setListShown(boolean shown, boolean animate) {
        if (!shown) {
            if (animate) {
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mAdapterContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            }
            mListContainer.setVisibility(View.GONE);
            mAdapterContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mAdapterContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            }
            mListContainer.setVisibility(View.VISIBLE);
            mAdapterContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown, Address address) {
        setListShown(shown, false);
    }

    private void initMap() {
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
                                     @Override
                                     public void onMapReady(GoogleMap googleMap) {
                                         if (googleMap != null) {
                                             Snackbar.make(getView(), getText(R.string.service_ready), Snackbar
                                                     .LENGTH_SHORT).show();
                                             mMap = googleMap;
                                         }
                                     }
                                 }
        );
    }

    private boolean serviceAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getActivity());
        if (isAvailable == ConnectionResult.SUCCESS) return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(getActivity(), isAvailable, GPS_ERROR_DIALOG_REQUEST);
            onDestroy();
            dialog.show();
        } else {
            Snackbar.make(getView(), getText(R.string.service_unavailable), Snackbar.LENGTH_SHORT).show();
        }
        return false;
    }

    private void dataRestore() {
        mParkListByDistance = mParkManager.getSavedParkListByDistance();
        mParkListByAlphabet = mParkManager.getSavedParkListByAlphabet();
        byDistanceToken = mParkManager.getSavedFilterToken();
        mFilterTitle.setText(byDistanceToken == 1 ? getString(R.string.filter_title_by_distance) : getString(R.string
                .filter_tile_by_alphabet));
        if (byDistanceToken == 1) {
            mFilterTitle.setText(getString(R.string.filter_title_by_distance));
            mFilterSwitch.setChecked(false);
        } else {
            mFilterTitle.setText(getString(R.string.filter_tile_by_alphabet));
            mFilterSwitch.setChecked(true);
        }
        if (mParkListByAlphabet != null && mParkListByDistance != null) {
            mParkListSelect = byDistanceToken == 1 ? mParkListByDistance : mParkListByAlphabet;
        }
        mMapState = mMapStateManager.getSavedMapState();
        if (mMapState != null) {
            isMePressed = mMapState.getIsMePressed() == 1;
            isParkPressed = mMapState.getIsParkPressed() == 1;
            isListShown = mMapState.getIsListShown() == 1;
            mCurrentCameraPosition = mMapState.getCameraPosition();
            mCurrentLatitude = mMapStateManager.getSavedLatitude();
            mCurrentLongitude = mMapStateManager.getSavedLongitude();
            mCurrentLocality = mMapStateManager.getSavedLocality();
            mParkSelected = mMapState.getParkSelected();
            mParkSelectedPosition = mMapState.getSelectedPosition();
        } else {
            mMapState = new MapState();
            isListShown = true;
            mParkSelectedPosition = -1;
        }
        if (mParkListSelect == null) mSFGovService.getParkInfo();
        else {
            mAdapter = new ParkAdapter(getActivity(), R.layout.fragment_map, mParkListSelect, ScreenAppearanceManager
                    .adapterHeight);
            setListAdapter(mAdapter);
            setListShown(isListShown);
        }
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        meLatLng = gson.fromJson(mPrefs.getString(ME_POSITION, ""), new TypeToken<LatLng>() {
        }.getType());
    }

    private void googleMapFetch() {
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
                                     @Override
                                     public void onMapReady(GoogleMap googleMap) {
                                         if (googleMap != null) {
                                             mMap = googleMap;
                                             if (meLatLng == null) {
                                                 try {
                                                     getCurrentLocation();
                                                 } catch (IOException e) {
                                                     e.printStackTrace();
                                                 }
                                             }
                                             if (meLatLng != null) {
                                                 setMeMarker("", meLatLng.latitude, meLatLng
                                                         .longitude);
                                                 mCurrentLatitude = meLatLng.latitude;
                                                 mCurrentLongitude = meLatLng.longitude;
                                             }
                                             mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                 @Override
                                                 public boolean onMarkerClick(Marker marker) {
                                                     markerClickAction();
                                                     return false;
                                                 }
                                             });
                                             if (mCurrentCameraPosition == null) {
                                                 try {
                                                     if (getCurrentLocation() != null) {
                                                         cameraUpdate(mCurrentLatitude, mCurrentLongitude,
                                                                 DEFAULT_ZOOM);
                                                     }
                                                 } catch (IOException e) {
                                                     e.printStackTrace();
                                                 }
                                             } else {
                                                 if (mMapState != null) {
                                                     if (mMapState.getMeLocality() != null)
                                                         setMeMarker(mMapState.getMeLocality(), mMapState
                                                                 .getMeLatitude(), mMapState
                                                                 .getMeLongitude());
                                                     if (mMapState.getParkLocality() != null)
                                                         setParkMarker(mMapState.getParkLocality(), mMapState
                                                                 .getParkLatitude(), mMapState
                                                                 .getParkLongitude());
                                                 }
                                                 if (mCurrentCameraPosition != null) {
                                                     CameraUpdate update = CameraUpdateFactory.newCameraPosition
                                                             (mCurrentCameraPosition);
                                                     mMap.moveCamera(update);
                                                 }
                                             }
                                             Snackbar.make(getView(), getText(R.string.service_ready), Snackbar
                                                     .LENGTH_SHORT).show();
                                         } else {
                                             Handler handler = new Handler();
                                             handler.postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     if (mMap == null) {
                                                         Snackbar.make(getView(), getText(R.string
                                                                 .map_loading_failed), Snackbar
                                                                 .LENGTH_SHORT)
                                                                 .show();
                                                         mProgressBar.setVisibility(View.VISIBLE);
                                                         initMap();
                                                     }
                                                 }
                                             }, 1000);
                                             if (mMap == null)
                                                 Snackbar.make(getView(), getText(R.string.service_not_ready),
                                                         Snackbar.LENGTH_SHORT).show();
                                         }
                                         mProgressBar.setVisibility(View.GONE);
                                     }
                                 }
        );
    }

    private void setGPSInterval() {
        mRequest = LocationRequest.create();
        mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mRequest.setInterval(50000);
        mRequest.setFastestInterval(10000);
    }

    private void removeSavedMeMarker() {
        mMapState.setMeLocality("");
        mMapState.setMeLatitude(-1);
        mMapState.setMeLongitude(-1);
    }

    private void removeSavedParkMarker() {
        mMapState.setParkLocality("");
        mMapState.setParkLatitude(-1);
        mMapState.setParkLongitude(-1);
    }

    private void markerClickAction() {
        if (mParkListSelect != null) {
            final String location = mParkSelected.getCity() + "," +
                    mParkSelected.getState();
            Forecast forecast;
            if ((forecast = weatherLoadingAction(location)) == null) {
                mWeatherService.getWeatherInfo(location);
                final Timer timer3 = new Timer();
                time = 0;
                timer3.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mWeatherManager.getSavedForecast(location) != null || time == 10) {
                                    timer3.cancel();
                                } else {
                                    mWeatherService.getWeatherInfo(location);
                                }
                                time++;
                            }
                        });
                    }
                }, 0, 500);
            } else {
                mParkSelected.setTempHigh(forecast.getHigh());
                mParkSelected.setTempLow(forecast.getLow());
                mParkSelected.setWeatherCode(forecast.getCode());
            }
            if (snapShotIsReady) {
                if (mParkInfoFragment != null && mParkInfoFragment.isAdded()) mParkInfoFragment.dismiss();
                mParkInfoFragment = ParkInfoFragment.newInstance(mParkSelected);
                mParkInfoFragment.show(mFragmentManager, "ParkInfo");
            } else {
                final Timer timer = new Timer();
                time = 0;
                mProgressBar.setVisibility(View.VISIBLE);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (snapShotIsReady || time == 10) {
                                    if (mParkInfoFragment != null && mParkInfoFragment.isAdded())
                                        mParkInfoFragment.dismiss();
                                    mParkInfoFragment = ParkInfoFragment.newInstance(mParkSelected);
                                    mParkInfoFragment.show(mFragmentManager, "ParkInfo");
                                    if (time == 10)
                                        Toast.makeText(getActivity(), getString(R.string.map_loading_overtime), Toast
                                                .LENGTH_SHORT)
                                                .show();
                                    mProgressBar.setVisibility(View.GONE);
                                    timer.cancel();
                                }
                                time++;
                            }
                        });
                    }
                }, 0, 300);
            }
        }
    }

    private void listClickAction() {
        mCurrentLatitude = mParkSelected.getLatitude();
        mCurrentLongitude = mParkSelected.getLongitude();
        mCurrentLocality = mParkSelected.getParkName();
        setParkMarker(mCurrentLocality, mCurrentLatitude, mCurrentLongitude);
        LatLng ll = new LatLng(mCurrentLatitude, mCurrentLongitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, PARK_ZOOM);
        snapShotIsReady = false;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        snapShotIsReady = true;
                        try {
                            mImageService.saveImage(bitmap, ImageService.TYPE_MAP, mParkSelected.getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mMap.animateCamera(update);
        mCurrentCameraPosition = mMap.getCameraPosition();
    }

    private Forecast weatherLoadingAction(final String location) {
        if (mWeatherManager.getSavedForecast(location) == null) {
            mWeatherService.getWeatherInfo(location);
            final Timer timer1 = new Timer();
            time = 0;
            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mWeatherManager.getSavedForecast(location) != null || time == 10) {
                                timer1.cancel();
                            }
                            time++;
                        }
                    });
                }
            }, 0, 300);
        }
        return mWeatherManager.getSavedForecast(location);
    }

    @Override
    public void onWeatherActionSuccess(Forecast forecast) throws JSONException {
        mWeatherManager.saveWeatherInfo(forecast);
    }

    @Override
    public void onWeatherActionFailed(Exception e) {
        showAlert(getString(R.string.alert_weather_load_unsuccessful), getString(R.string.confirm));
    }

    private FloatingActionButton initWifiSign() {
        FloatingActionButton signContainer = (FloatingActionButton) getActivity().findViewById(R.id
                .noNetWorkSignContainer);
        signContainer.setBackgroundTintList(white);
        return signContainer;
    }
}
