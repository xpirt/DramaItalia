package and.conachegroup.dramaitalia.app;

public class LatestReleased extends LatestDramaFilm {

    private String mEpisode;
    private String mType;
    private String mCountry;
    private String mFansub;

    public LatestReleased(int id, String date, String title,  String episode, String type,
                          String country, String fansub) {
        super(id, date, title);
        mEpisode = episode;
        mType = type;
        mCountry = country;
        mFansub = fansub;
    }

    public String getEpisode() { return mEpisode; }

    public void setEpisode(String input) {
        mEpisode = input;
    }

    public String getType() {
        return mType;
    }

    public void setType(String input) {
        mType = input;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String input) {
        mCountry = input;
    }

    public String getFansub() { return mFansub; }

    public void setFansub(String input) { mFansub = input; }

}
