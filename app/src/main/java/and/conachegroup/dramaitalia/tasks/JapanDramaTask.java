package and.conachegroup.dramaitalia.tasks;

import android.os.AsyncTask;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import and.conachegroup.dramaitalia.app.Documents;
import and.conachegroup.dramaitalia.app.JapanDrama;
import and.conachegroup.dramaitalia.fragments.JapanDramasFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class JapanDramaTask extends AsyncTask<Fragment, Void, List<JapanDrama>>
        implements Constants {

    private static final String TAG = "JapanDramaTask";

    private WeakReference<JapanDramasFragment> mJapanDramasFragment;
    private WeakReference<RelativeLayout> mLoading;
    private WeakReference<RelativeLayout> mError;

    public JapanDramaTask(JapanDramasFragment japanDramasFragment,
                          RelativeLayout loading, RelativeLayout error) {
        mJapanDramasFragment = new WeakReference<>(japanDramasFragment);
        mLoading = new WeakReference<>(loading);
        mError = new WeakReference<>(error);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected List<JapanDrama> doInBackground(Fragment... params) {
        try {
            List<JapanDrama> japanDramas = new ArrayList<>();

            if (Documents.getJapanDrama() == null) {
                Documents.setJapanDrama(Utils.getDocument(URL_DRAMA_JAPAN));
            }

            Document japanDocument = Documents.getJapanDrama();

            List<String> japanDramasTitles = new ArrayList<>();
            List<String> japanDramasFansubs = new ArrayList<>();
            List<String> japanDramasStatuses = new ArrayList<>();
            List<String> japanDramasTypes = new ArrayList<>();

            if (japanDocument != null) {
                Element table = japanDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // title
                        String title = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(title)) {
                            japanDramasTitles.add(title);
                        }

                        // fansub
                        String fansub = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            japanDramasFansubs.add(fansub);
                        }

                        // status
                        String status = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(status)) {
                            japanDramasStatuses.add(status);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            japanDramasTypes.add(type);
                        }

                        // checks
                        Elements th = row.select("th");
                        if (!th.isEmpty()) continue;
                        if (title.isEmpty()) continue;

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        japanDramas.add(new JapanDrama(current,
                                japanDramasTitles.get(current),
                                japanDramasFansubs.get(current),
                                japanDramasStatuses.get(current),
                                japanDramasTypes.get(current)));
                    }
                }
            }

            return japanDramas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<JapanDrama> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.get().setVisibility(View.GONE);
            }
            mJapanDramasFragment.get().setupListView(result);
        } else {
            mError.get().setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
