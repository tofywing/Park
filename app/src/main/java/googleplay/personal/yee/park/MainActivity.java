package googleplay.personal.yee.park;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import kinsa.interview.yee.park.R;
import googleplay.personal.yee.park.fragment.GoogleMapFragment;


/**
 * Created by Yee on 4/4/16.
 */
public class MainActivity extends FragmentActivity {


    public static final int REQUEST_CODE_PERMISSION_LOCATION = 0;

    GoogleMapFragment mMapFragment;
    FragmentManager mSupportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest
                        .permission
                        .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                ActivityCompat.requestPermissions(this, new String[]{Manifest
                        .permission.ACCESS_FINE_LOCATION, Manifest
                        .permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
            }
        } else {
            mMapFragment = new GoogleMapFragment();
            mSupportManager = getSupportFragmentManager();
            mSupportManager.beginTransaction().add(R.id.mapContainer, mMapFragment).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMapFragment = new GoogleMapFragment();
                    mSupportManager = getSupportFragmentManager();
                    mSupportManager.beginTransaction().add(R.id.mapContainer, mMapFragment)
                            .commitAllowingStateLoss();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
