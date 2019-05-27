package and.conachegroup.dramaitalia.tasks;

import android.os.AsyncTask;
import android.util.Log;

import and.conachegroup.dramaitalia.app.Documents;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class RetrieveDocuments extends AsyncTask<Void, Void, Boolean>
        implements Constants  {

    private static final String TAG = "RetrieveDocuments";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected Boolean doInBackground(Void... params) {
        try {
            Documents.setLatestReleased(Utils.getDocument(URL_LATEST_RELEASED));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setLatestAnnounced(Utils.getDocument(URL_LATEST_ANNOUNCED));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setLatestCompleted(Utils.getDocument(URL_LATEST_COMPLETED));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setJapanDrama(Utils.getDocument(URL_DRAMA_JAPAN));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setKoreanDrama(Utils.getDocument(URL_DRAMA_KOREA));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setOtherDrama(Utils.getDocument(URL_DRAMA_OTHER));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setJapanFilm(Utils.getDocument(URL_FILM_JAPAN));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setKoreanFilm(Utils.getDocument(URL_FILM_KOREA));
            if (this.isCancelled()) {
                return false;
            }
            Documents.setOtherFilm(Utils.getDocument(URL_FILM_OTHER));

            return !this.isCancelled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.d(TAG, "All documents retrieved successfully!");
        }
    }
}