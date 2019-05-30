package and.conachegroup.dramaitalia.app;

public class JapanFilm extends DramaFilm {

    private String mStatus;
    private String mType;

    public JapanFilm(int id, String title, String fansub, String status, String type) {
        super(id, title, fansub);
        mStatus = status;
        mType = type;
    }

    public String getStatus() { return mStatus; }

    public String getType() {
        return mType;
    }
}
