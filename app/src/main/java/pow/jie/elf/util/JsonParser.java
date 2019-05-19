package pow.jie.elf.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pow.jie.elf.bean.MusicInfo;


public class JsonParser {
    public static List<MusicInfo> parseMusicInfo(String jsonData) {
        List<MusicInfo> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String code = jsonObject.getString("code");
            if (!code.equals("200"))
                return null;
            else {
                JSONObject playList = jsonObject.getJSONObject("playlist");
                JSONArray musicList = playList.getJSONArray("tracks");
                for (int i = 0; i < musicList.length(); i++) {
                    JSONObject musicObj = musicList.getJSONObject(i);
                    String name = musicObj.getString("name");
                    long id = musicObj.getLong("id");
                    JSONArray authorBean = musicObj.getJSONArray("ar");
                    String author = authorBean.getJSONObject(0).getString("name");
                    JSONObject albumBean = musicObj.getJSONObject("al");
                    long albumId = albumBean.getLong("id");
                    String albumName = albumBean.getString("name");
                    String picUrl = albumBean.getString("picUrl");

                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAuthorName(author);
                    musicInfo.setPicUrl(picUrl);
                    list.add(musicInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static final String TAG = "JsonParser";

    public static long parseListId(String jsonData) {
        Log.d(TAG, "parseListId: "+jsonData);
        JSONObject jsonObject;
        long id = -1;
        try {
            jsonObject = new JSONObject(jsonData);
            int code = jsonObject.getInt("status");
            if (code != 200)
                return id;
            else {
                JSONObject data = jsonObject.getJSONObject("data");
                Log.d(TAG, "parseListId: " + data);
                id = data.getLong("id");
                Log.d(TAG, "parseListId: " + id);
                return id;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
}
