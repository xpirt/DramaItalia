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

import and.conachegroup.dramaitalia.app.Announced;
import and.conachegroup.dramaitalia.fragments.Announced2017Fragment;
import and.conachegroup.dramaitalia.app.Documents;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class Announced2017Task extends AsyncTask<Object, Void, List<Announced>>
        implements Constants {

    private static final String TAG = "Announced2017Task";

    private WeakReference<Announced2017Fragment> mAnnounced2017Fragment;
    private WeakReference<RelativeLayout> mLoading;
    private WeakReference<RelativeLayout> mError;

    public Announced2017Task(Announced2017Fragment announced2017Fragment,
                             RelativeLayout loading, RelativeLayout error) {
        mAnnounced2017Fragment = new WeakReference<>(announced2017Fragment);
        mLoading = new WeakReference<>(loading);
        mError  = new WeakReference<>(error);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Announced> doInBackground(Object... param) {
        try {
            List<Announced> announced2017 = new ArrayList<>();

            if (Documents.getAnnounced2017() == null) {
                Documents.setAnnounced2017(Utils.getDocument(URL_ANNOUNCED_2017));
            }

            Document announced2017Document = Documents.getAnnounced2017();

            List<String> announced2017Dates = new ArrayList<>();
            List<String> announced2017Titles = new ArrayList<>();
            List<String> announced2017Genres = new ArrayList<>();
            List<String> announced2017Types = new ArrayList<>();
            List<String> announced2017Fansubs = new ArrayList<>();
            List<String> announced2017Countries = new ArrayList<>();
            List<String> announced2017Links = new ArrayList<>();

            if (announced2017Document != null) {
                Element table = announced2017Document.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // date
                        String date = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(date)) {
                            announced2017Dates.add(date);
                        }

                        // genre
                        String genre = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(genre)) {
                            announced2017Genres.add(genre);
                        }

                        // title
                        Elements title = row.select("td:eq(2)");
                        if (Utils.isNotEmpty(title.text())) {
                            announced2017Titles.add(title.text());
                        }
                        if (Utils.isNotEmpty(title.select("a").attr("abs:href"))) {
                            announced2017Links.add(title.select("a").attr("abs:href"));
                        }

                        // fansub
                        String fansub = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            announced2017Fansubs.add(fansub);
                        }

                        // country
                        String country = row.select("td:eq(4)").text();
                        if (Utils.isNotEmpty(country)) {
                            announced2017Countries.add(country);
                        }

                        // type
                        String type = row.select("td:eq(5)").text();
                        if (Utils.isNotEmpty(type)) {
                            announced2017Types.add(type);
                        }

                        // checks
                        Elements th = row.select("th");
                        if (!th.isEmpty()) continue;
                        if (title.isEmpty()) continue;

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        announced2017.add(new Announced(current,
                                announced2017Dates.get(current),
                                announced2017Titles.get(current),
                                announced2017Genres.get(current),
                                announced2017Types.get(current),
                                announced2017Fansubs.get(current),
                                announced2017Countries.get(current),
                                announced2017Links.get(current)));
                    }
                }
            }

            return announced2017;
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
                mLoading.get().setVisibility(View.GONE);
            }
            mAnnounced2017Fragment.get().setupListView(result);
        } else {
            mError.get().setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
