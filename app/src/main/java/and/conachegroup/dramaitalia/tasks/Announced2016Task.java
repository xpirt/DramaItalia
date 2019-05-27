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

import and.conachegroup.dramaitalia.app.Announced;
import and.conachegroup.dramaitalia.app.Documents;
import and.conachegroup.dramaitalia.fragments.Announced2016Fragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class Announced2016Task extends AsyncTask<Object, Void, List<Announced>>
        implements Constants {

    private static final String TAG = "Announced2016Task";

    public Context mContext;
    private Announced2016Fragment mAnnounced2016Fragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public Announced2016Task(Context context, Announced2016Fragment announced2016Fragment,
                             RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mAnnounced2016Fragment = announced2016Fragment;
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
            List<Announced> announced2016 = new ArrayList<>();

            if (Documents.getAnnounced2016() == null) {
                Documents.setAnnounced2016(Utils.getDocument(URL_ANNOUNCED_2016));
            }

            Document announced2016Document = Documents.getAnnounced2016();

            List<String> announced2016Dates = new ArrayList<>();
            List<String> announced2016Titles = new ArrayList<>();
            List<String> announced2016Genres = new ArrayList<>();
            List<String> announced2016Types = new ArrayList<>();
            List<String> announced2016Fansubs = new ArrayList<>();
            List<String> announced2016Countries = new ArrayList<>();
            List<String> announced2016Links = new ArrayList<>();

            if (announced2016Document != null) {
                Element table = announced2016Document.body().select("table.wikitable").first();
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    if (rows.indexOf(row) > 0) {

                        // date
                        String date = row.select("td:eq(0)").text();
                        if (Utils.isNotEmpty(date)) {
                            announced2016Dates.add(date);
                        }

                        // genre
                        String genre = row.select("td:eq(1)").text();
                        if (Utils.isNotEmpty(genre)) {
                            announced2016Genres.add(genre);
                        }

                        // title
                        Elements title = row.select("td:eq(2)");
                        if (Utils.isNotEmpty(title.text())) {
                            announced2016Titles.add(title.text());
                        }
                        if (Utils.isNotEmpty(title.select("a").attr("abs:href"))) {
                            announced2016Links.add(title.select("a").attr("abs:href"));
                        }

                        // fansub
                        String fansub = row.select("td:eq(3)").text();
                        if (Utils.isNotEmpty(fansub)) {
                            announced2016Fansubs.add(fansub);
                        }

                        // country
                        String country = row.select("td:eq(4)").text();
                        if (Utils.isNotEmpty(country)) {
                            announced2016Countries.add(country);
                        }

                        // type
                        String type = row.select("td:eq(5)").text();
                        if (Utils.isNotEmpty(type)) {
                            announced2016Types.add(type);
                        }

                        // checks
                        Elements th = row.select("th");
                        if (!th.isEmpty()) continue;
                        if (title.isEmpty()) continue;

                        // skip empty rows
                        if (!row.hasText()) continue;

                        int current = rows.indexOf(row) - 1;
                        announced2016.add(new Announced(current,
                                announced2016Dates.get(current),
                                announced2016Titles.get(current),
                                announced2016Genres.get(current),
                                announced2016Types.get(current),
                                announced2016Fansubs.get(current),
                                announced2016Countries.get(current),
                                announced2016Links.get(current)));
                    }
                }
            }

            return announced2016;
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
            mAnnounced2016Fragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
