package and.conachegroup.dramaitalia.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.app.License;
import and.conachegroup.dramaitalia.fragments.LicensesFragment;

public class LicenseTask extends AsyncTask<Object, Void, List<License>> {

    private static final String TAG = "LicenseTask";

    private WeakReference<Context> mContext;
    private WeakReference<LicensesFragment> mLicensesFragment;
    private WeakReference<RelativeLayout> mLoading;
    private WeakReference<RelativeLayout> mError;

    public LicenseTask(Context context, LicensesFragment licensesFragment,
                       RelativeLayout loading, RelativeLayout error) {
        mContext = new WeakReference<>(context);
        mLicensesFragment = new WeakReference<>(licensesFragment);
        mLoading = new WeakReference<>(loading);
        mError = new WeakReference<>(error);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected List<License> doInBackground(Object... param) {
        try {
            List<License> licenses = new ArrayList<>();

            // set volley
            licenses.add(new License(0,
                    mContext.get().getString(R.string.volley_title),
                    mContext.get().getString(R.string.volley_author),
                    mContext.get().getString(R.string.volley_license),
                    mContext.get().getString(R.string.volley_link)));

            // set jsoup
            licenses.add(new License(1,
                    mContext.get().getString(R.string.jsoup_title),
                    mContext.get().getString(R.string.jsoup_author),
                    mContext.get().getString(R.string.jsoup_license),
                    mContext.get().getString(R.string.jsoup_link)));

            // set taptargetview
            licenses.add(new License(2,
                    mContext.get().getString(R.string.taptargetview_title),
                    mContext.get().getString(R.string.taptargetview_author),
                    mContext.get().getString(R.string.taptargetview_license),
                    mContext.get().getString(R.string.taptargetview_link)));

            // set materialdesignicons
            licenses.add(new License(3,
                    mContext.get().getString(R.string.materialdesignicons_title),
                    mContext.get().getString(R.string.materialdesignicons_author),
                    mContext.get().getString(R.string.materialdesignicons_license),
                    mContext.get().getString(R.string.materialdesignicons_link)));

            // set materialdesignicons
            licenses.add(new License(4,
                    mContext.get().getString(R.string.flaticon_title),
                    mContext.get().getString(R.string.flaticon_author),
                    mContext.get().getString(R.string.flaticon_license),
                    mContext.get().getString(R.string.flaticon_link)));

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
                mLoading.get().setVisibility(View.GONE);
            }
            mLicensesFragment.get().setupListView(result);
        } else {
            mError.get().setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
