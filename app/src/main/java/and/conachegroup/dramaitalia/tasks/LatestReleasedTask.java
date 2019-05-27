package and.conachegroup.dramaitalia.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import and.conachegroup.dramaitalia.app.Documents;
import and.conachegroup.dramaitalia.app.LatestReleased;
import and.conachegroup.dramaitalia.fragments.LatestReleasedFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class LatestReleasedTask extends AsyncTask<Object, Void, List<LatestReleased>>
        implements Constants {

    private static final String TAG = "LatestReleasedTask";

    public Context mContext;
    private LatestReleasedFragment mLatestReleasedFragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public LatestReleasedTask(Context context, LatestReleasedFragment latestReleasedFragment,
                              RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mLatestReleasedFragment = latestReleasedFragment;
        mLoading = loading;
        mError  = error;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<LatestReleased> doInBackground(Object... param) {
        try {
            List<LatestReleased> latestReleased = new ArrayList<>();

            if (Documents.getLatestReleased() == null) {
                Documents.setLatestReleased(Utils.getDocument(URL_LATEST_RELEASED));
            }

            Document latestReleasedDocument = Documents.getLatestReleased();

            List<String> latestReleasedDates = new ArrayList<>();
            List<String> latestReleasedTitles = new ArrayList<>();
            List<String> latestReleasedEpisodes = new ArrayList<>();
            List<String> latestReleasedTypes = new ArrayList<>();
            List<String> latestReleasedCountries = new ArrayList<>();
            List<String> latestReleasedFansubs = new ArrayList<>();

            if (latestReleasedDocument != null) {
                Element table = latestReleasedDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // date
                        String date = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(date)) {
                            latestReleasedDates.add(date);
                        }

                        // title
                        Elements title = row.select("td:eq(1)");
                        if (Utils.isNotEmpty(title.text())) {
                            latestReleasedTitles.add(title.text());
                        }

                        // episode
                        String episode = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(episode)) {
                            latestReleasedEpisodes.add(episode);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            latestReleasedTypes.add(type);
                        }

                        // country
                        String country = row.select("td:eq(4)").text();
                        if (Utils.isNotEmpty(country)) {
                            latestReleasedCountries.add(country);
                        }

                        // fansub
                        String fansub = row.select("td:eq(5)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            latestReleasedFansubs.add(fansub);
                        }

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        latestReleased.add(new LatestReleased(current,
                                latestReleasedDates.get(current),
                                latestReleasedTitles.get(current),
                                latestReleasedEpisodes.get(current),
                                latestReleasedTypes.get(current),
                                latestReleasedCountries.get(current),
                                latestReleasedFansubs.get(current)));
                    }
                }
            }

            return latestReleased;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<LatestReleased> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.setVisibility(View.GONE);
            }
            mLatestReleasedFragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
