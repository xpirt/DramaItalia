package and.conachegroup.dramaitalia.tasks;

import android.os.AsyncTask;
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
import and.conachegroup.dramaitalia.app.OtherDrama;
import and.conachegroup.dramaitalia.fragments.OtherDramasFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class OtherDramaTask extends AsyncTask<Object, Void, List<OtherDrama>>
        implements Constants  {

    private static final String TAG = "OtherDramaTask";

    private WeakReference<OtherDramasFragment> mOtherDramasFragment;
    private WeakReference<RelativeLayout> mLoading;
    private WeakReference<RelativeLayout> mError;

    public OtherDramaTask(OtherDramasFragment otherDramasFragment,
                          RelativeLayout loading, RelativeLayout error) {
        mOtherDramasFragment = new WeakReference<>(otherDramasFragment);
        mLoading = new WeakReference<>(loading);
        mError  = new WeakReference<>(error);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected List<OtherDrama> doInBackground(Object... param) {
        try {
            List<OtherDrama> otherDramas = new ArrayList<>();

            if (Documents.getOtherDrama() == null) {
                Documents.setOtherDrama(Utils.getDocument(URL_DRAMA_OTHER));
            }

            Document otherDocument = Documents.getOtherDrama();

            List<String> otherDramasTitles = new ArrayList<>();
            List<String> otherDramasFansubs = new ArrayList<>();
            List<String> otherDramasStatuses = new ArrayList<>();
            List<String> otherDramasTypes = new ArrayList<>();
            List<String> otherDramasCountries = new ArrayList<>();

            if (otherDocument != null) {
                Element table = otherDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // title
                        String title = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(title)) {
                            otherDramasTitles.add(title);
                        }

                        // fansub
                        String fansub = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            otherDramasFansubs.add(fansub);
                        }

                        // status
                        String status = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(status)) {
                            otherDramasStatuses.add(status);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            otherDramasTypes.add(type);
                        }

                        // country
                        String country = row.select("td:eq(4)").text();
                        if (Utils.isNotEmpty(country)) {
                            otherDramasCountries.add(country);
                        }

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        otherDramas.add(new OtherDrama(current,
                                otherDramasTitles.get(current),
                                otherDramasFansubs.get(current),
                                otherDramasStatuses.get(current),
                                otherDramasTypes.get(current),
                                otherDramasCountries.get(current)));
                    }
                }
            }

            return otherDramas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<OtherDrama> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.get().setVisibility(View.GONE);
            }
            mOtherDramasFragment.get().setupListView(result);
        } else {
            mError.get().setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
