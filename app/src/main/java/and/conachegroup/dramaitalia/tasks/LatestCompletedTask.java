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
import and.conachegroup.dramaitalia.app.LatestCompleted;
import and.conachegroup.dramaitalia.fragments.LatestCompletedFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class LatestCompletedTask extends AsyncTask<Object, Void, List<LatestCompleted>>
        implements Constants {

    private static final String TAG = "LatestCompletedTask";

    public Context mContext;
    private LatestCompletedFragment mLatestCompletedFragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public LatestCompletedTask(Context context, LatestCompletedFragment latestCompletedFragment,
                               RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mLatestCompletedFragment = latestCompletedFragment;
        mLoading = loading;
        mError  = error;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<LatestCompleted> doInBackground(Object... param) {
        try {
            List<LatestCompleted> latestCompleted = new ArrayList<>();

            if (Documents.getLatestCompleted() == null) {
                Documents.setLatestCompleted(Utils.getDocument(URL_LATEST_COMPLETED));
            }

            Document latestCompletedDocument = Documents.getLatestCompleted();

            List<String> latestCompletedDates = new ArrayList<>();
            List<String> latestCompletedTitles = new ArrayList<>();
            List<String> latestCompletedEpisodes = new ArrayList<>();
            List<String> latestCompletedTypes = new ArrayList<>();
            List<String> latestCompletedFansubs = new ArrayList<>();
            List<String> latestCompletedDownloads = new ArrayList<>();

            if (latestCompletedDocument != null) {
                Element table = latestCompletedDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // date
                        String date = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(date)) {
                            latestCompletedDates.add(date);
                        }

                        // title
                        String title = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(title)) {
                            latestCompletedTitles.add(title);
                        }

                        // episode
                        String episode = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(episode)) {
                            latestCompletedEpisodes.add(episode);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            latestCompletedTypes.add(type);
                        }

                        // fansub
                        String fansub = row.select("td:eq(4)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            latestCompletedFansubs.add(fansub);
                        }

                        // download
                        Elements download = row.select("td:eq(5)");
                        if (Utils.isNotEmpty(download.select("a").attr("abs:href"))) {
                            latestCompletedDownloads.add(download.select("a").attr("abs:href"));
                        }

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        latestCompleted.add(new LatestCompleted(current,
                                latestCompletedDates.get(current),
                                latestCompletedTitles.get(current),
                                latestCompletedEpisodes.get(current),
                                latestCompletedTypes.get(current),
                                latestCompletedFansubs.get(current),
                                latestCompletedDownloads.get(current)));
                    }
                }
            }

            return latestCompleted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<LatestCompleted> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.setVisibility(View.GONE);
            }
            mLatestCompletedFragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
