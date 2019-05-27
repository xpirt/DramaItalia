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

    public void setId(int input) {
        mId = input;
    }

    public String getTitle() { return mTitle; }

    public void setTitle(String input) { mTitle = input; }

    public String getFansub() { return mFansub; }

    public void setFansub(String input) { mFansub = input; }

}
