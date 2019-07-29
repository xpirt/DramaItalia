package and.conachegroup.dramaitalia.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import and.conachegroup.dramaitalia.DramaItalia;
import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.app.DramaFilm;
import and.conachegroup.dramaitalia.app.LatestDramaFilm;

public class Utils implements Constants {

    private static final String TAG = "Utils";

    public static boolean isNotEmpty(String input) {
        if (input != null) {
            return !input.isEmpty() || !input.equals("") || !TextUtils.isEmpty(input) ||
                    !input.equals(System.getProperty("line.separator"));
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isConnected(Context context) {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                return activeNetwork.isConnectedOrConnecting();
            }
        }
        return false;
    }

    /* public static Document getDocument(String url) {
        try {
            Connection connection = Jsoup.connect(url)
                    .ignoreHttpErrors(true).timeout(20000);
            Connection.Response response = connection.execute();

            int statusCode = response.statusCode();
            if (statusCode == 200) {
                Log.d(TAG, "Connection established");
                return connection.get();
            } else {
                Log.w(TAG, "Connection refused, status error code: " + statusCode);
            }
        } catch (SocketTimeoutException ste) {
            Log.e(TAG, "Socket timeout exception, message: " + ste.getMessage());
        } catch (InterruptedIOException interruptedIOException) {
            Log.e(TAG, "Thread interrupted, message: " + interruptedIOException.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } */

    private static RequestQueue requestQueue = null;
    public static Document getDocument(String url) throws Exception {
        final Document[] document = new Document[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        if (!isConnected(DramaItalia.getContext())) return null;

        StringRequest documentRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    document[0] = Jsoup.parse(response, BASE_URL);
                    countDownLatch.countDown();
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(DramaItalia.getContext(),
                            DramaItalia.getContext().getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG).show();
                }
                Log.e(TAG, "Error getting document: " + error.getMessage(), error);
            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(DramaItalia.getContext());
        }
        requestQueue.add(documentRequest);

        countDownLatch.await();
        return document[0];
    }

    // STATUS
    public static int getStatusId(String status) {
        if (status.contains(ANNOUNCED)) {
            return ANNOUNCED_ID;
        } else if (status.contains(COMPLETED_1) ||
                status.contains(COMPLETED_2) ||
                status.contains(COMPLETED_3) ||
                status.contains(COMPLETED_4) ||
                status.contains(COMPLETED_5)) {
            return COMPLETED_ID;
        } else if (status.contains(WIP_1) ||
                status.contains(WIP_2)) {
            return WIP_ID;
        } else if (status.contains(ABANDONED) ||
                status.contains(DROPPED)) {
            return ABANDONED_ID;
        } else {
            return -1;
        }
    }

    public static int getStatusColor(Context context, int statusId) {
        int color = Color.BLACK;
        switch (statusId) {
            case ANNOUNCED_ID:
                color = ContextCompat.getColor(context, R.color.status_announced);
                break;
            case COMPLETED_ID:
                color = ContextCompat.getColor(context, R.color.status_completed);
                break;
            case WIP_ID:
                color = ContextCompat.getColor(context, R.color.status_wip);
                break;
            case ABANDONED_ID:
                color = ContextCompat.getColor(context, R.color.status_abandoned);
                break;
            default:
                break;
        }
        return color;
    }

    // COUNTRY
    public static int getCountryId(String country) {
        if (country.contains(CHINA_1) ||
                country.contains(CHINA_2) ||
                country.contains(CHINA_3)) {
            return CHINA_ID;
        } else if (country.contains(TAIWAN_1) ||
                country.contains(TAIWAN_2)){
            return TAIWAN_ID;
        } else if (country.contains(THAILAND_1) ||
                country.contains(THAILAND_2) ||
                country.contains(THAILAND_3)) {
            return THAILAND_ID;
        } else if (country.contains(HONG_KONG)) {
            return HONG_KONG_ID;
        } else if (country.contains(KOREA_1) ||
                country.contains(KOREA_2) ||
                country.contains(KOREA_3) ||
                country.contains(KOREA_4) ||
                country.contains(KOREA_5) ||
                country.contains(KOREA_6)) {
            return KOREA_ID;
        } else if (country.contains(JAPAN_1) ||
                country.contains(JAPAN_2) ||
                country.contains(JAPAN_3) ||
                country.contains(JAPAN_4) ||
                country.contains(JAPAN_5)) {
            return JAPAN_ID;
        } else if (country.contains(INDIA_1)) {
            return INDIA_ID;
        } else if (country.contains(PHILIPPINES_1) ||
                country.contains(PHILIPPINES_2) ||
                country.contains(PHILIPPINES_3)) {
            return PHILIPPINES_ID;
        } else if (country.contains(SINGAPORE_1)) {
            return SINGAPORE_ID;
        } else {
            return -1;
        }
    }

    public static Drawable getDrawableFromCountryId(Context context, int countryId) {
        Drawable countryDrawable;
        switch (countryId) {
            case CHINA_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.china_flag);
                return countryDrawable;
            case TAIWAN_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.taiwan_flag);
                return countryDrawable;
            case THAILAND_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.thailand_flag);
                return countryDrawable;
            case HONG_KONG_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.hong_kong_flag);
                return countryDrawable;
            case KOREA_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.korea_flag);
                return countryDrawable;
            case JAPAN_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.japan_flag);
                return countryDrawable;
            case INDIA_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.india_flag);
                return countryDrawable;
            case PHILIPPINES_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.philippines_flag);
                return countryDrawable;
            case SINGAPORE_ID:
                countryDrawable = ContextCompat.getDrawable(context, R.drawable.singapore_flag);
                return countryDrawable;
            default:
                break;
        }
        return null;
    }

    public static boolean isValidUrl(String url) {
        return URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url);
    }

    public static void setDownloadDrawable(Context context, AppCompatImageView imageView,
                                           String url) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.download);
        if (isValidUrl(url) && drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context,
                    R.color.download_available));
        }
        imageView.setImageDrawable(drawable);
    }

    /*public static int getActionBarHeight(AppCompatActivity activity) {
        int actionBarHeight = 0;
        if (activity.getSupportActionBar() != null) {
            actionBarHeight = activity.getSupportActionBar().getHeight();
            final TypedValue typedValue = new TypedValue();
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue,
                    true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,
                        activity.getResources().getDisplayMetrics());
            }
        }
        return actionBarHeight;
    }*/

    public static List<? extends DramaFilm> sortById(List<? extends DramaFilm> oldList){
        Collections.sort(oldList, new Comparator<DramaFilm>() {
            @Override
            public int compare(DramaFilm first, DramaFilm second) {
                if (first.getId() > second.getId()) {
                    return second.getId() - first.getId();
                } else {
                    return first.getId() - second.getId();
                }
            }
        });
        return oldList;
    }

    public static List<? extends LatestDramaFilm> sortLatestById(
            List<? extends LatestDramaFilm> oldList){
        Collections.sort(oldList, new Comparator<LatestDramaFilm>() {
            @Override
            public int compare(LatestDramaFilm first, LatestDramaFilm second) {
                if (first.getId() > second.getId()) {
                    return second.getId() - first.getId();
                } else {
                    return first.getId() - second.getId();
                }
            }
        });
        return oldList;
    }

    /* public static Drawable getSeason(Context context) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // correct month
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat f = new SimpleDateFormat("MM-dd", Locale.ITALY);
        f.setLenient(false);

        try
        {
            Date date = f.parse(month + "-" + day);
            if (date.after(f.parse("03-19")) && date.before(f.parse("06-21"))) {
                // spring (20-03 -> 20-06)
                return ContextCompat.getDrawable(context, R.drawable.dramaitalia_cherry);
            } else if (date.after(f.parse("06-20")) && (date.before(f.parse("09-23")))) {
                // summer (21-06 -> 22-09)
                return ContextCompat.getDrawable(context, R.drawable.dramaitalia_summer);
            } else if (date.after(f.parse("09-22")) && date.before(f.parse("12-21"))) {
                if (date.equals(f.parse("10-31"))) {
                    // halloween (31-10)
                    return ContextCompat.getDrawable(context, R.drawable.dramaitalia_halloween);
                }
                // fall (23-09 -> 20-12)
                return ContextCompat.getDrawable(context, R.drawable.dramaitalia_autunno);
            } else {
                if (date.after(f.parse("12-23")) && date.before(f.parse("12-31"))) {
                    // christmas (24-12 -> 30-12)
                    return ContextCompat.getDrawable(context, R.drawable.dramaitalia_natale);
                }
                // winter (21-12 -> 19-03)
                return ContextCompat.getDrawable(context, R.drawable.dramaitalia_inverno2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } */
}
