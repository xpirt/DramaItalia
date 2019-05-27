package and.conachegroup.dramaitalia;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DramaItalia extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static DramaItalia sInstance;

    // global resources
    public SharedPreferences prefs;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        sInstance = this;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onLowMemory() {
        Runtime.getRuntime().gc();
        super.onLowMemory();
    }

    public synchronized static DramaItalia getInstance() {
        return sInstance;
    }
}
