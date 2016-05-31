package googleplay.personal.yee.park.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Locale;

import kinsa.interview.yee.park.R;
import googleplay.personal.yee.park.data.ParkInfo;
import googleplay.personal.yee.park.manager.ScreenAppearanceManager;
import googleplay.personal.yee.park.service.ImageService;

/**
 * Created by Yee on 4/17/16.
 */
public class ParkInfoFragment extends DialogFragment {

    public static final String PARK_INFO = "parkCardInfo";
    public static final String URL_TEMPLATE = "https://maps.googleapis" +
            ".com/maps/api/streetview?size=800x600&location=%s,%s&fov=120";
    public int left = -1;
    public int top = -1;
    public int width = -1;
    public int length = -1;

    ImageView mParkView;
    ImageView mMapSnapshot;
    FloatingActionButton mCloseButton;
    int[][] colorPattern = new int[][]{new int[]{Color.parseColor("#F14B46")}};
    ColorStateList red = new ColorStateList(colorPattern, colorPattern[0]);

    public static ParkInfoFragment newInstance(ParkInfo parkInfo) {
        Bundle args = new Bundle();
        args.putParcelable(PARK_INFO, parkInfo);
        ParkInfoFragment fragment = new ParkInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_park_card);
        final ImageService imageService = new ImageService(getActivity());
        final ParkInfo parkInfo = getArguments().getParcelable(PARK_INFO);
        mParkView = (ImageView) dialog.findViewById(R.id.parkView);
        ScreenAppearanceManager screenManager = new ScreenAppearanceManager(getActivity());
        ScreenAppearanceManager.screenWidth = screenManager.getScreenWidth();
        ScreenAppearanceManager.screenLength = screenManager.getScreenLength();
        if (width == -1 || length == -1 || left == -1 || top == -1) {
            width = (int) Math.abs(Math.round(ScreenAppearanceManager.screenWidth * 0.75));
            length = (int) Math.abs(Math.round(ScreenAppearanceManager.screenLength * 0.2));
            left = width / 2;
            top = length / 2;
        }
        Picasso.with(getActivity()).load(R.drawable.no_image_available).resize(width, length)
                .into(mParkView);
        assert parkInfo != null;
        String url = String.format(Locale.US, URL_TEMPLATE, String.valueOf(parkInfo.getLatitude()), String
                .valueOf(parkInfo.getLongitude()));
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.cardProgressBar);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) progressBar.getLayoutParams();
        params.topMargin += top;
        params.leftMargin += left;
        progressBar.requestLayout();
        final Target target1 = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mParkView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
            }
        };
        Picasso.with(getActivity()).load(url).centerCrop().resize((int) Math.abs(Math.round(ScreenAppearanceManager
                        .screenWidth * 0.75)),
                (int) Math.abs(Math.round(ScreenAppearanceManager.screenLength * 0.2))).into(target1);
        mParkView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.card_fade_in));
        mParkView.setTag(target1);
        mCloseButton = (FloatingActionButton) dialog.findViewById(R.id.cardCloseButton);
        mCloseButton.setBackgroundTintList(red);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mMapSnapshot = (ImageView) dialog.findViewById(R.id.mapSnapshot);
        Bitmap bitmap = imageService.getBitmap(getActivity().getFilesDir() + ImageService.TYPE_MAP + String.valueOf
                (parkInfo.getId()));
        if (bitmap != null)
            mMapSnapshot.setImageBitmap(imageService.getCroppedBitmap(bitmap, ScreenAppearanceManager.screenWidth));
        else {
            final Target target2 = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mMapSnapshot.setImageBitmap(imageService.getCroppedBitmap(bitmap, ScreenAppearanceManager
                            .screenWidth));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.with(getActivity()).load(R.drawable.snapshot_unavilable).resize(ScreenAppearanceManager
                    .screenWidth, ScreenAppearanceManager.screenLength).into
                    (target2);
            mMapSnapshot.setTag(target2);
        }
        TextView name = (TextView) dialog.findViewById(R.id.parkName);
        TextView type = (TextView) dialog.findViewById(R.id.parkType);
        TextView area = (TextView) dialog.findViewById(R.id.parkArea);
        TextView street = (TextView) dialog.findViewById(R.id.parkStreet);
        TextView city = (TextView) dialog.findViewById(R.id.parkCity);
        TextView state = (TextView) dialog.findViewById(R.id.parkState);
        TextView zip = (TextView) dialog.findViewById(R.id.parkZip);
        TextView manager = (TextView) dialog.findViewById(R.id.mgrName);
        TextView phone = (TextView) dialog.findViewById(R.id.mgrPhone);
        TextView email = (TextView) dialog.findViewById(R.id.mgrEmail);
        TextView distance = (TextView) dialog.findViewById(R.id.parkDistance);
        TextView weather = (TextView) dialog.findViewById(R.id.parkWeather);
        ImageView weatherImage = (ImageView) dialog.findViewById(R.id.weatherImage);
        name.setText(parkInfo.getParkName());
        type.setText(parkInfo.getType());
        area.setText(getString(R.string.area_format, String.valueOf(parkInfo.getAcreage())));
        street.setText(parkInfo.getStreet());
        city.setText(parkInfo.getCity());
        state.setText(parkInfo.getState());
        zip.setText(parkInfo.getZip());
        manager.setText(parkInfo.getManager());
        phone.setText(parkInfo.getPhone());
        email.setText(parkInfo.getEmail());
        distance.setText(getString(R.string.distance_format, String.format(Locale.UK, "%.2f", parkInfo
                .getDistance())));
        weather.setText(getString(R.string.temperature_format, parkInfo.getTempLow(), parkInfo.getTempHigh()));
        int weatherCode = parkInfo.getWeatherCode();
        if (weatherCode > -1 && weatherCode < 48) {
            int resourceId = getResources().getIdentifier("drawable/icon_" + parkInfo.getWeatherCode(), null,
                    getActivity().getPackageName());
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), resourceId, null);
            weatherImage.setImageDrawable(drawable);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(null);
        }
    }
}
