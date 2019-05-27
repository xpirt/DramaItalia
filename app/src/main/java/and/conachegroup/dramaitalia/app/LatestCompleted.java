package and.conachegroup.dramaitalia.app;

public class LatestCompleted extends LatestDramaFilm {

    private String mEpisodes;
    private String mType;
    private String mFansub;
    private String mDownload;

    public LatestCompleted(int id, String date, String title, String episodes, String type,
                           String fansub, String download) {
        super(id, date, title);
        mEpisodes = episodes;
        mType = type;
        mFansub = fansub;
        mDownload = download;
    }

    public String getEpisodes() { return mEpisodes; }

    public void setEpisodes(String input) {
        mEpisodes = input;
    }

    public String getType() {
        return mType;
    }

    public void setType(String input) {
        mType = input;
    }

    public String getFansub() { return mFansub; }

    public void setFansub(String input) { mFansub = input; }

    public String getDownload() {
        return mDownload;
    }

    public void setDownload(String input) {
        mDownload = input;
    }
}
