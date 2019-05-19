package pow.jie.elf.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pow.jie.elf.bean.MusicInfo;
import pow.jie.elf.db.MusicInfoDbHelper;

public class DBTools {

    private static MusicInfoDbHelper dbHelper;

    public static void saveMusicInfo(MusicInfo musicInfo, Context context) {
        dbHelper = new MusicInfoDbHelper(context, "music", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", musicInfo.getName());
        values.put("music_id", musicInfo.getId());
        values.put("author", musicInfo.getAuthorName());
        values.put("album_id", musicInfo.getAlbumId());
        values.put("album_name", musicInfo.getAlbumName());
        values.put("pic_url", musicInfo.getPicUrl());
        db.insert("music", null, values);
        values.clear();
    }

    public static MusicInfo readMusicInfo(long id) {
        String idStr = String.valueOf(id);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("music",
                new String[]{"name", "music_id", "author", "album_id", "album_name", "pic_url"},
                "music_id=?", new String[]{idStr}, null, null, null);
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
        musicInfo.setId(cursor.getLong(cursor.getColumnIndex("music_id")));
        musicInfo.setAuthorName(cursor.getString(cursor.getColumnIndex("author")));
        musicInfo.setAlbumId(cursor.getLong(cursor.getColumnIndex("album_id")));
        musicInfo.setAlbumName(cursor.getString(cursor.getColumnIndex("album_name")));
        musicInfo.setPicUrl(cursor.getString(cursor.getColumnIndex("pic_url")));
        cursor.close();
        return musicInfo;
    }

    public static List<MusicInfo> readMusicInfo() {
        List<MusicInfo> musicInfoList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("music",
                new String[]{"name", "music_id", "author", "album_id", "album_name", "pic_url"},
                null, null, null, null, null);
        cursor.moveToFirst();
        do {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
            musicInfo.setId(cursor.getLong(cursor.getColumnIndex("music_id")));
            musicInfo.setAuthorName(cursor.getString(cursor.getColumnIndex("author")));
            musicInfo.setAlbumId(cursor.getLong(cursor.getColumnIndex("album_id")));
            musicInfo.setAlbumName(cursor.getString(cursor.getColumnIndex("album_name")));
            musicInfo.setPicUrl(cursor.getString(cursor.getColumnIndex("pic_url")));
            musicInfoList.add(musicInfo);
        } while (cursor.moveToNext());
        cursor.close();
        return musicInfoList;
    }
    public static void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

    }
}
