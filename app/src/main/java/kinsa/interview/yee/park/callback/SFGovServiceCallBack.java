package kinsa.interview.yee.park.callback;

import android.app.ProgressDialog;

import java.io.IOException;
import java.util.ArrayList;

import kinsa.interview.yee.park.data.ParkInfo;

/**
 * Created by Yee on 4/5/16.
 */
public interface SFGovServiceCallBack {

    void onSFGovServiceSuccess(ProgressDialog dialog, ArrayList<ParkInfo> dataSet) throws IOException;

    void onSFGovServiceFailed(ProgressDialog dialog);
}
