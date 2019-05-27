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
import and.conachegroup.dramaitalia.app.KoreanDrama;
import and.conachegroup.dramaitalia.fragments.KoreanDramasFragment;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class KoreanDramaTask extends AsyncTask<Object, Void, List<KoreanDrama>>
        implements Constants {

    private static final String TAG = "KoreanDramaTask";

    public Context mContext;
    private KoreanDramasFragment mKoreanDramasFragment;
    private RelativeLayout mLoading;
    private RelativeLayout mError;

    public KoreanDramaTask(Context context, KoreanDramasFragment koreanDramasFragment,
                           RelativeLayout loading, RelativeLayout error) {
        mContext = context;
        mKoreanDramasFragment = koreanDramasFragment;
        mLoading = loading;
        mError  = error;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoading.setVisibility(View.VISIBLE);
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
                mLoading.setVisibility(View.GONE);
            }
            mKoreanDramasFragment.setupListView(result);
        } else {
            mError.setVisibility(View.VISIBLE);
        }

        super.onPostExecute(result);
    }
}
