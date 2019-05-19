package pow.jie.elf.api;

public class Api {
    private static String MOOD_CLAM = "CLAM";
    private static String MOOD_EXCITING = "EXCITING";
    private static String MOOD_HAPPY = "HAPPY";
    private static String MOOD_UNHAPPY = "UNHAPPY";

    private static final String baseUrl = "http://elf.egos.hosigus.com";

    public static String getSongList(String mood) {
        return baseUrl + "/getSongListID.php?type=" + mood;
    }

    public static String getSongList() {
        return baseUrl + "/getSongListID.php?type=" + MOOD_CLAM;
    }

    public static String getRecommend() {
        return baseUrl + "/getRecommendID.php";
    }

    public static String getMusicDetail(Long id) {
        return baseUrl + "/music/playlist/detail?id=" + id;
    }

    public static String getMusicFile(Long id) {
        return "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
    }

    public static String getLyric(Long id) {
        return baseUrl + "/music/lyric?id=" + id;
    }
}
