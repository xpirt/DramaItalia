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
import and.conachegroup.dramaitalia.app.KoreanFilm;
import and.conachegroup.dramaitalia.fragments.KoreanFilmsFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class KoreanFilmTask extends AsyncTask<Object, Void, List<KoreanFilm>>
        implements Constants  {

    private static final String TAG = "KoreanFilmTask";

    private WeakReference<KoreanFilmsFragment> mKoreanFilmsFragment;
    private WeakReference<RelativeLayout> mLoading;
    private WeakReference<RelativeLayout> mError;

    public KoreanFilmTask(KoreanFilmsFragment koreanFilmsFragment,
                          RelativeLayout loading, RelativeLayout error) {
        mKoreanFilmsFragment = new WeakReference<>(koreanFilmsFragment);
        mLoading = new WeakReference<>(loading);
        mError  = new WeakReference<>(error);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected List<KoreanFilm> doInBackground(Object... param) {
        try {
            List<KoreanFilm> koreanFilms = new ArrayList<>();

            if (Documents.getKoreanFilm() == null) {
                Documents.setKoreanFilm(Utils.getDocument(URL_FILM_KOREA));
            }

            Document koreaDocument = Documents.getKoreanFilm();

            List<String> koreanFilmsTitles = new ArrayList<>();
            List<String> koreanFilmsFansubs = new ArrayList<>();
            List<String> koreanFilmsStatuses = new ArrayList<>();
            List<String> koreanFilmsTypes = new ArrayList<>();

            if (koreaDocument != null) {
                Element table = koreaDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // title
                        String title = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(title)) {
                            koreanFilmsTitles.add(title);
                        }

                        // fansub
                        String fansub = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            koreanFilmsFansubs.add(fansub);
                        }

                        // status
                        String status = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(status)) {
                            koreanFilmsStatuses.add(status);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            koreanFilmsTypes.add(type);
                        }

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        koreanFilms.add(new KoreanFilm(current,
                                koreanFilmsTitles.get(current),
                                koreanFilmsFansubs.get(current),
                                koreanFilmsStatuses.get(current),
                                koreanFilmsTypes.get(current)));
                    }
                }
            }

            return koreanFilms;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<KoreanFilm> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.get().setVisibility(View.GONE);
            }
            mKoreanFilmsFragment.get().setupListView(result);
        } else {
            mError.get().setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
