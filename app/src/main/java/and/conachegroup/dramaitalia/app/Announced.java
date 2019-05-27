package and.conachegroup.dramaitalia.app;

public class Announced extends LatestDramaFilm {

    private String mGenre;
    private String mType;
    private String mFansub;
    private String mCountry;
    private String mLink;

    public Announced(int id, String date, String title, String genre, String type,
                     String fansub, String country, String link) {
        super(id, date, title);
        mGenre = genre;
        mType = type;
        mFansub = fansub;
        mCountry = country;
        mLink = link;
    }

    public String getGenre() { return mGenre; }

    public void setGenre(String input) {
        mGenre = input;
    }

    public String getType() {
        return mType;
    }

    public void setType(String input) {
        mType = input;
    }

    public String getFansub() { return mFansub; }

    public void setFansub(String input) { mFansub = input; }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String input) {
        mCountry = input;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String input) {
        mLink = input;
    }
}
