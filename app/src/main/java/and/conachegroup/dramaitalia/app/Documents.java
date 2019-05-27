package and.conachegroup.dramaitalia.app;

import org.jsoup.nodes.Document;

public class Documents {

    private static Document mLatestReleased = null;
    private static Document mLatestAnnounced = null;
    private static Document mLatestCompleted = null;

    private static Document mJapanDrama = null;
    private static Document mKoreanDrama = null;
    private static Document mOtherDrama = null;

    private static Document mKoreanFilm = null;
    private static Document mJapanFilm = null;
    private static Document mOtherFilm = null;

    private static Document mAnnounced2017 = null;
    private static Document mAnnounced2016 = null;

    public static Document getLatestReleased() {
        return mLatestReleased;
    }

    public static void setLatestReleased(Document latestReleased) {
        mLatestReleased = latestReleased;
    }

    public static Document getLatestAnnounced() {
        return mLatestAnnounced;
    }

    public static void setLatestAnnounced(Document latestAnnounced) {
        mLatestAnnounced = latestAnnounced;
    }

    public static Document getLatestCompleted() {
        return mLatestCompleted;
    }

    public static void setLatestCompleted(Document latestCompleted) {
        mLatestCompleted = latestCompleted;
    }

    public static Document getJapanDrama() {
        return mJapanDrama;
    }

    public static void setJapanDrama(Document japanDrama) {
        mJapanDrama = japanDrama;
    }

    public static Document getKoreanDrama() {
        return mKoreanDrama;
    }

    public static void setKoreanDrama(Document koreanDrama) {
        mKoreanDrama = koreanDrama;
    }

    public static Document getOtherDrama() {
        return mOtherDrama;
    }

    public static void setOtherDrama(Document otherDrama) {
        mOtherDrama = otherDrama;
    }

    public static Document getKoreanFilm() {
        return mKoreanFilm;
    }

    public static void setKoreanFilm(Document koreanFilm) {
        mKoreanFilm = koreanFilm;
    }

    public static Document getJapanFilm() {
        return mJapanFilm;
    }

    public static void setJapanFilm(Document japanFilm) {
        mJapanFilm =  japanFilm;
    }

    public static Document getOtherFilm() {
        return mOtherFilm;
    }

    public static void setOtherFilm(Document otherFilm) {
        mOtherFilm = otherFilm;
    }

    public static Document getAnnounced2017() {
        return mAnnounced2017;
    }

    public static void setAnnounced2017(Document announced2017) {
        mAnnounced2017 = announced2017;
    }

    public static Document getAnnounced2016() {
        return mAnnounced2016;
    }

    public static void setAnnounced2016(Document announced2016) {
        mAnnounced2016 = announced2016;
    }
}
