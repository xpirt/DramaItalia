package and.conachegroup.dramaitalia.app;

public class LatestDramaFilm {

    private int mId;
    private String mDate;
    private String mTitle;

    public LatestDramaFilm(int id, String date, String title) {
        mId = id;
        mDate = date;
        mTitle = title;
    }

    public int getId() { return mId; }

    public String getDate() { return mDate; }

    public String getTitle() { return mTitle; }
}
