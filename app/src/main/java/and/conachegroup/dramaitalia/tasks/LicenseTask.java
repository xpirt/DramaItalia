package and.conachegroup.dramaitalia.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.app.License;
import and.conachegroup.dramaitalia.fragments.LicensesFragment;

public class LicenseTask extends AsyncTask<Object, Void, List<License>> {

    private static final String TAG = "LicenseTask";

    private Context mContext;
    private LicensesFragment mLicensesFragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public LicenseTask(Context context, LicensesFragment licensesFragment,
                       RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mLicensesFragment = licensesFragment;
        mLoading = loading;
        mError = error;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<License> doInBackground(Object... param) {
        try {
            List<License> licenses = new ArrayList<>();

            // set volley
            licenses.add(new License(0,
                    mContext.getString(R.string.volley_title),
                    mContext.getString(R.string.volley_author),
                    mContext.getString(R.string.volley_license),
                    mContext.getString(R.string.volley_link)));

            // set jsoup
            licenses.add(new License(1,
                    mContext.getString(R.string.jsoup_title),
                    mContext.getString(R.string.jsoup_author),
                    mContext.getString(R.string.jsoup_license),
                    mContext.getString(R.string.jsoup_link)));

            // set taptargetview
            licenses.add(new License(2,
                    mContext.getString(R.string.taptargetview_title),
                    mContext.getString(R.string.taptargetview_author),
                    mContext.getString(R.string.taptargetview_license),
                    mContext.getString(R.string.taptargetview_link)));

            // set materialdesignicons
            licenses.add(new License(3,
                    mContext.getString(R.string.materialdesignicons_title),
                    mContext.getString(R.string.materialdesignicons_author),
                    mContext.getString(R.string.materialdesignicons_license),
                    mContext.getString(R.string.materialdesignicons_link)));

            // set materialdesignicons
            licenses.add(new License(4,
                    mContext.getString(R.string.flaticon_title),
                    mContext.getString(R.string.flaticon_author),
                    mContext.getString(R.string.flaticon_license),
                    mContext.getString(R.string.flaticon_link)));

            return licenses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<License> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.setVisibility(View.GONE);
            }
            mLicensesFragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
