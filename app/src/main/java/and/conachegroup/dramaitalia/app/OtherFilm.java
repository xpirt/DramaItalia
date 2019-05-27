package and.conachegroup.dramaitalia.app;

public class OtherFilm extends DramaFilm {

    private String mStatus;
    private String mType;
    private String mCountry;

    public OtherFilm(int id, String title, String fansub, String status, String type, String country) {
        super(id, title, fansub);
        mStatus = status;
        mType = type;
        mCountry = country;
    }

    public String getStatus() { return mStatus; }

    public void setStatus(String input) {
        mStatus = input;
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
}
