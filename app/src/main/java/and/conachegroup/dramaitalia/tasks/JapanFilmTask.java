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
import and.conachegroup.dramaitalia.app.JapanFilm;
import and.conachegroup.dramaitalia.fragments.JapanFilmsFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class JapanFilmTask extends AsyncTask<Object, Void, List<JapanFilm>>
        implements Constants {

    private static final String TAG = "JapanFilmTask";

    public Context mContext;
    private JapanFilmsFragment mJapanFilmsFragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public JapanFilmTask(Context context, JapanFilmsFragment japanFilmsFragment,
                         RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mJapanFilmsFragment = japanFilmsFragment;
        mLoading = loading;
        mError  = error;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<JapanFilm> doInBackground(Object... param) {
        try {
            List<JapanFilm> japanFilms = new ArrayList<>();

            if (Documents.getJapanFilm() == null) {
                Documents.setJapanFilm(Utils.getDocument(URL_FILM_JAPAN));
            }

            Document japanDocument = Documents.getJapanFilm();

            List<String> japanFilmsTitles = new ArrayList<>();
            List<String> japanFilmsFansubs = new ArrayList<>();
            List<String> japanFilmsStatuses = new ArrayList<>();
            List<String> japanFilmsTypes = new ArrayList<>();

            if (japanDocument != null) {
                Element table = japanDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // title
                        String title = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(title)) {
                            japanFilmsTitles.add(title);
                        }

                        // fansub
                        String fansub = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            japanFilmsFansubs.add(fansub);
                        }

                        // status
                        String status = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(status)) {
                            japanFilmsStatuses.add(status);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            japanFilmsTypes.add(type);
                        }

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        japanFilms.add(new JapanFilm(current,
                                japanFilmsTitles.get(current),
                                japanFilmsFansubs.get(current),
                                japanFilmsStatuses.get(current),
                                japanFilmsTypes.get(current)));
                    }
                }
            }

            return japanFilms;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<JapanFilm> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.setVisibility(View.GONE);
            }
            mJapanFilmsFragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
