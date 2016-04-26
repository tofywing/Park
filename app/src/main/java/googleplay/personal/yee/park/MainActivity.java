package googleplay.personal.yee.park;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import kinsa.interview.yee.park.R;
import googleplay.personal.yee.park.fragment.GoogleMapFragment;


/**
 * Created by Yee on 4/4/16.
 */
public class MainActivity extends FragmentActivity {

    GoogleMapFragment mMapFragment;
    FragmentManager mSupportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapFragment = new GoogleMapFragment();
        mSupportManager = getSupportFragmentManager();
        mSupportManager.beginTransaction().add(R.id.mapContainer, mMapFragment).commit();

    }
}
