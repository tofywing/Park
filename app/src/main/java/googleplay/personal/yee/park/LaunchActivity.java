package googleplay.personal.yee.park;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

import kinsa.interview.yee.park.R;
import googleplay.personal.yee.park.manager.ScreenAppearanceManager;

public class LaunchActivity extends AppCompatActivity {

// It is a little bit too late to add this, suppose to do some pre-loading from here, but since in current version, this
// activity is just a starting page.

    public static final String PREFS = "sharedPreference";
    ImageView mSignImage;
    ImageView mParkImage;
    RelativeLayout mContainer;
    Handler mHandler;
    Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        clearApplicationData();
        getScreenAppearance();
        mSignImage = (ImageView) findViewById(R.id.signImage);
        mParkImage = (ImageView) findViewById(R.id.parkImage);
        mContainer = (RelativeLayout) findViewById(R.id.launchContainer);
        final Handler handler = new Handler();
        (new Thread() {
            @Override
            public void run() {
                for (int i = 255; i >= 0; i--) {
                    final int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            mContainer.setBackgroundColor(Color.argb(255, finalI, finalI, finalI));
                        }
                    });
                    try {
                        sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        if (mParkImage != null) {
            ViewGroup.LayoutParams params = mParkImage.getLayoutParams();
            params.height = (int) (ScreenAppearanceManager.screenWidth * 0.31);
            mParkImage.setLayoutParams(params);
            mParkImage.startAnimation(AnimationUtils.loadAnimation(
                    this, R.anim.park_fade_in));
            mParkImage.setBackgroundResource(ScreenAppearanceManager.imageId);
        }

        mContainer = (RelativeLayout) findViewById(R.id.launchContainer);
        mSignImage.setBackgroundResource(R.drawable.bird);
        final AnimationDrawable animDrawable = (AnimationDrawable) mSignImage.getBackground();
        animDrawable.setExitFadeDuration(10);
        animDrawable.start();
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(LaunchActivity.this, MainActivity.class);
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
                animDrawable.stop();
                overridePendingTransition(R.anim.main_fade_in, R.anim.launch_fade_out);
            }
        };
        mHandler.postDelayed(mRunnable, 3500);
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
        SharedPreferences.Editor editor = this.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void getScreenAppearance() {
        ScreenAppearanceManager screenManager = new ScreenAppearanceManager(this);
        if ((ScreenAppearanceManager.screenWidth = screenManager.getScreenWidth()) == -1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenManager.saveScreenAppearance(display);
            ScreenAppearanceManager.screenWidth = screenManager.getScreenWidth();
        }
        ScreenAppearanceManager.screenLength = screenManager.getScreenLength();
        ScreenAppearanceManager.listWidth = screenManager.getListWidth();
        ScreenAppearanceManager.listTitleHeight = screenManager.getListTitleHeight();
        ScreenAppearanceManager.adapterHeight = screenManager.getAdapterHeight();
        ScreenAppearanceManager.switchBarLength = screenManager.getSwitchBarLength();
        ScreenAppearanceManager.imageId = screenManager.getImageId();
    }

    private void setBackground() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, String.valueOf(requestCode), Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mRunnable);
        finish();
    }
}

