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
import and.conachegroup.dramaitalia.app.OtherFilm;
import and.conachegroup.dramaitalia.fragments.OtherFilmsFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class OtherFilmTask extends AsyncTask<Object, Void, List<OtherFilm>>
        implements Constants {

    private static final String TAG = "OtherFilmTask";

    public Context mContext;
    private OtherFilmsFragment mOtherFilmsFragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public OtherFilmTask(Context context, OtherFilmsFragment otherFilmsFragment,
                         RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mOtherFilmsFragment = otherFilmsFragment;
        mLoading = loading;
        mError  = error;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<OtherFilm> doInBackground(Object... param) {
        try {
            List<OtherFilm> otherFilms = new ArrayList<>();

            if (Documents.getOtherFilm() == null) {
                Documents.setOtherFilm(Utils.getDocument(URL_FILM_OTHER));
            }

            Document otherDocument = Documents.getOtherFilm();

            List<String> otherFilmsTitles = new ArrayList<>();
            List<String> otherFilmsFansubs = new ArrayList<>();
            List<String> otherFilmsStatuses = new ArrayList<>();
            List<String> otherFilmsTypes = new ArrayList<>();
            List<String> otherFilmsCountries = new ArrayList<>();

            if (otherDocument != null) {
                Element table = otherDocument.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // title
                        String title = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(title)) {
                            otherFilmsTitles.add(title);
                        }

                        // fansub
                        String fansub = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            otherFilmsFansubs.add(fansub);
                        }

                        // status
                        String status = row.select("td:eq(2)").text();
                        if (Utils.isNotEmpty(status)) {
                            otherFilmsStatuses.add(status);
                        }

                        // type
                        String type = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(type)) {
                            otherFilmsTypes.add(type);
                        }

                        // country
                        String country = row.select("td:eq(4)").text();
                        if (Utils.isNotEmpty(country)) {
                            otherFilmsCountries.add(country);
                        }

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        otherFilms.add(new OtherFilm(current,
                                otherFilmsTitles.get(current),
                                otherFilmsFansubs.get(current),
                                otherFilmsStatuses.get(current),
                                otherFilmsTypes.get(current),
                                otherFilmsCountries.get(current)));
                    }
                }
            }

            return otherFilms;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<OtherFilm> result) {
        if (result != null && !result.isEmpty()) {
            Log.d(TAG, "Document retrieved successfully!");
            if (mLoading != null) {
                mLoading.setVisibility(View.GONE);
            }
            mOtherFilmsFragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
