package googleplay.personal.yee.park.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.widget.TextView;

import googleplay.personal.yee.park.data.ParkInfo;
import kinsa.interview.yee.park.R;
import googleplay.personal.yee.park.manager.ScreenAppearanceManager;

/**
 * Created by Yee on 4/11/16.
 */
public class ParkAdapter extends ArrayAdapter<ParkInfo> {

    Context mContext;
    ArrayList<ParkInfo> mDataSet;
    int height;

    public ParkAdapter(Context context, int resource, ArrayList<ParkInfo> dataSet, int height) {
        super(context, resource, dataSet);
        mContext = context;
        mDataSet = dataSet;
        this.height = height;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.adapter_park, null);
        TextView textView = (TextView) view.findViewById(R.id.parkListCell);
        ViewGroup.LayoutParams viewParams = textView.getLayoutParams();
        viewParams.height = ScreenAppearanceManager.adapterHeight;
        textView.setLayoutParams(viewParams);
        ParkInfo parkInfo = mDataSet.get(position);
        textView.setText(parkInfo.getParkName());
        if (parkInfo.getIsSelected() == 1) {
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparentYellow));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.purple));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparentAzureBlue));
        }
        return view;
    }
}
