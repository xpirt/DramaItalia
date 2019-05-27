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
import and.conachegroup.dramaitalia.app.Announced;
import and.conachegroup.dramaitalia.fragments.LatestAnnouncedFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class LatestAnnouncedTask extends AsyncTask<Object, Void, List<Announced>>
        implements Constants {

    private static final String TAG = "LatestAnnouncedTask";

    public Context mContext;
    private LatestAnnouncedFragment mLatestAnnouncedFragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public LatestAnnouncedTask(Context context, LatestAnnouncedFragment latestAnnouncedFragment,
                               RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mLatestAnnouncedFragment = latestAnnouncedFragment;
        mLoading = loading;
        mError  = error;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Announced> doInBackground(Object... param) {
        try {
            List<Announced> latestAnnounced = new ArrayList<>();

            if (Documents.getLatestAnnounced() == null) {
                Documents.setLatestAnnounced(Utils.getDocument(URL_LATEST_ANNOUNCED));
            }

            Document latestAnnouncedDocument = Documents.getLatestAnnounced();

            List<String> latestAnnouncedDates = new ArrayList<>();
            List<String> latestAnnouncedTitles = new ArrayList<>();
            List<String> latestAnnouncedGenres = new ArrayList<>();
            List<String> latestAnnouncedTypes = new ArrayList<>();
            List<String> latestAnnouncedFansubs = new ArrayList<>();
            List<String> latestAnnouncedCountries = new ArrayList<>();
            List<String> latestAnnouncedLinks = new ArrayList<>();

            if (latestAnnouncedDocument != null) {
                Element table = latestAnnouncedDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // date
                        String date = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(date)) {
                            latestAnnouncedDates.add(date);
                        }

                        // genre
                        String genre = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(genre)) {
                            latestAnnouncedGenres.add(genre);
                        }

                        // title
                        Elements title = row.select("td:eq(2)");
                        if (Utils.isNotEmpty(title.text())) {
                            latestAnnouncedTitles.add(title.text());
                        }
                        if (Utils.isNotEmpty(title.select("a").attr("abs:href"))) {
                            latestAnnouncedLinks.add(title.select("a").attr("abs:href"));
                        }

                        // fansub
                        String fansub = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            latestAnnouncedFansubs.add(fansub);
                        }

                        // country
                        String country = row.select("td:eq(4)").text();
                        if (Utils.isNotEmpty(country)) {
                            latestAnnouncedCountries.add(country);
                        }

                        // type
                        String type = row.select("td:eq(5)").text();
                        if (Utils.isNotEmpty(type)) {
                            latestAnnouncedTypes.add(type);
                        }

                        // checks
                        Elements th = row.select("th");
                        if (!th.isEmpty()) continue;
                        if (title.isEmpty()) continue;

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        latestAnnounced.add(new Announced(current,
                                latestAnnouncedDates.get(current),
                                latestAnnouncedTitles.get(current),
                                latestAnnouncedGenres.get(current),
                                latestAnnouncedTypes.get(current),
                                latestAnnouncedFansubs.get(current),
                                latestAnnouncedCountries.get(current),
                                latestAnnouncedLinks.get(current)));
                    }
                }
            }

            return latestAnnounced;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Announced> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.setVisibility(View.GONE);
            }
            mLatestAnnouncedFragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
