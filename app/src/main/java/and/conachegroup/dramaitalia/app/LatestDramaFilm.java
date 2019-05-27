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

    public void setId(int input) {
        mId = input;
    }

    public String getDate() { return mDate; }

    public void setDate(String input) { mDate = input; }

    public String getTitle() { return mTitle; }

    public void setTitle(String input) { mTitle = input; }
}
