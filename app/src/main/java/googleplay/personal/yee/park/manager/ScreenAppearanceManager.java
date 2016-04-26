package googleplay.personal.yee.park.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;

import java.util.Random;

/**
 * Created by Yee on 4/24/16.
 */
public class ScreenAppearanceManager {

    public static final String PREFERENCE = "sharedPreference";
    public static final String SCREEN_WIDTH = "screenWidth";
    public static final String SCREEN_LENGTH = "screenLength";
    public static final String LIST_WIDTH = "listWidth";
    public static final String LIST_TITLE_HEIGHT = "listTitleHeight";
    public static final String ADAPTER_HEIGHT = "adapterHeight";
    public static final String SWITCH_LENGTH = "switchBarLength";
    public static final String IMAGE_ID = "theImageIdFromSystemPath";

    public static int screenWidth;
    public static int screenLength;
    public static int listWidth;
    public static int listTitleHeight;
    public static int adapterHeight;
    public static int switchBarLength;
    public static int imageId;

    SharedPreferences mPrefs;
    Context mContext;

    public ScreenAppearanceManager(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        mContext = context;
    }

    public void saveScreenAppearance(Display display) {
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenLength = size.y;
        int listWidth = (int) (screenWidth * 0.40);
        int listTitleHeight = (int) (listWidth * 0.31);
        int adapterHeight = (int) (screenLength * 0.06);
        int switchBarLength = (int) (screenWidth * 0.25);

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(SCREEN_WIDTH, screenWidth);
        editor.putInt(SCREEN_LENGTH, screenLength);
        editor.putInt(LIST_WIDTH, listWidth);
        editor.putInt(LIST_TITLE_HEIGHT, listTitleHeight);
        editor.putInt(ADAPTER_HEIGHT, adapterHeight);
        editor.putInt(SWITCH_LENGTH, switchBarLength);
        editor.apply();
    }

    public int getScreenWidth() {
        return screenWidth = mPrefs.getInt(SCREEN_WIDTH, -1);
    }

    public int getScreenLength() {
        return screenLength = mPrefs.getInt(SCREEN_LENGTH, -1);
    }

    public int getListWidth() {
        return listWidth = mPrefs.getInt(LIST_WIDTH, -1);
    }

    public int getListTitleHeight() {
        return listTitleHeight = mPrefs.getInt(LIST_TITLE_HEIGHT, -1);
    }

    public int getAdapterHeight() {
        return adapterHeight = mPrefs.getInt(ADAPTER_HEIGHT, -1);
    }

    public int getSwitchBarLength() {
        return switchBarLength = mPrefs.getInt(SWITCH_LENGTH, -1);
    }

    public int getImageId() {
        int imageIndex = new Random().nextInt(10);
        int resourceId = mContext.getResources().getIdentifier("drawable/flower_" + imageIndex, null,
                mContext.getPackageName());
        return imageId = resourceId;
    }
}
