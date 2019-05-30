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
import and.conachegroup.dramaitalia.app.KoreanDrama;
import and.conachegroup.dramaitalia.fragments.KoreanDramasFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class KoreanDramaTask extends AsyncTask<Object, Void, List<KoreanDrama>>
        implements Constants {

    private static final String TAG = "KoreanDramaTask";

    private WeakReference<KoreanDramasFragment> mKoreanDramasFragment;
    private WeakReference<RelativeLayout> mLoading;
    private WeakReference<RelativeLayout> mError;

    public KoreanDramaTask(KoreanDramasFragment koreanDramasFragment,
                           RelativeLayout loading, RelativeLayout error) {
        mKoreanDramasFragment = new WeakReference<>(koreanDramasFragment);
        mLoading = new WeakReference<>(loading);
        mError  = new WeakReference<>(error);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected List<KoreanDrama> doInBackground(Object... param) {
        try {
            List<KoreanDrama> koreanDramas = new ArrayList<>();

            if (Documents.getKoreanDrama() == null) {
                Documents.setKoreanDrama(Utils.getDocument(URL_DRAMA_KOREA));
            }

            Document koreaDocument = Documents.getKoreanDrama();

            List<String> koreanDramasTitles = new ArrayList<>();
            List<String> koreanDramasFansubs = new ArrayList<>();
            List<String> koreanDramasStatuses = new ArrayList<>();
            List<String> koreanDramasTypes = new ArrayList<>();

            if (koreaDocument != null) {
                Element table = koreaDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // title
                        String title = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(title)) {
                            koreanDramasTitles.add(title);
                        }

                        // fansub
                        String fansub = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            koreanDramasFansubs.add(fansub);
                        }

                        // status
                        String status = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(status)) {
                            koreanDramasStatuses.add(status);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            koreanDramasTypes.add(type);
                        }

                        // checks
                        Elements th = row.select("th");
                        if (!th.isEmpty()) continue;
                        if (title.isEmpty()) continue;

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        koreanDramas.add(new KoreanDrama(current,
                                koreanDramasTitles.get(current),
                                koreanDramasFansubs.get(current),
                                koreanDramasStatuses.get(current),
                                koreanDramasTypes.get(current)));
                    }
                }
            }

            return koreanDramas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<KoreanDrama> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.get().setVisibility(View.GONE);
            }
            mKoreanDramasFragment.get().setupListView(result);
        } else {
            mError.get().setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
