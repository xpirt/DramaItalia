package and.conachegroup.dramaitalia.app;

public class DramaFilm {

    private int mId;
    private String mTitle;
    private String mFansub;

    public DramaFilm(int id, String title, String fansub) {
        mId = id;
        mTitle = title;
        mFansub = fansub;
    }

    public int getId() { return mId; }

    public String getTitle() { return mTitle; }

    public String getFansub() { return mFansub; }
}
