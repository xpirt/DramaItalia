package and.conachegroup.dramaitalia.app;

public class License {

    private int mId;
    private String mTitle;
    private String mAuthor;
    private String mLicense;
    private String mLink;

    public License(int id, String title, String author, String license, String link) {
        mId = id;
        mTitle = title;
        mAuthor = author;
        mLicense = license;
        mLink = link;
    }

    public int getId() { return mId; }

    public String getTitle() { return mTitle; }

    public String getAuthor() { return mAuthor; }

    public String getLicense() { return mLicense; }

    public String getLink() {
        return mLink;
    }
}
