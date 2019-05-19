package pow.jie.elf.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

import pow.jie.elf.api.Api;
import pow.jie.elf.bean.MusicInfo;
import pow.jie.elf.util.DBTools;

public class PlayService extends Service {
    public static MediaPlayer mediaPlayer;
    private List<MusicInfo> musicList;
    private int songNum = 0;
    public final IBinder binder = new MyBinder();

    public PlayService() {
        super();

        mediaPlayer = new MediaPlayer();//实例化一个多媒体对象
        musicList = DBTools.readMusicInfo();
    }

    public class MyBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * 播放音乐
     */
    public void playOrPause(View view) {
        final ImageView imageButton = (ImageView) view;
        //实例化mediaPlayer
        if (mediaPlayer == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                mediaPlayer = new MediaPlayer();

                //设置音频流的类型
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //设置音源
                try {
                    mediaPlayer.setDataSource
                            (PlayService.this, Uri.parse(Api.getMusicFile(musicList.get(songNum).getId())));
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            //播放图标
            imageButton.setImageResource(android.R.drawable.ic_media_play);
        } else {
            try {
                mediaPlayer.reset(); //重置多媒体
                String dataSource = Api.getMusicFile(musicList.get(songNum).getId());//得到当前播放音乐的路径
                // 指定参数为音频文件
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(dataSource);//为多媒体对象设置播放路径
                mediaPlayer.prepare();//准备播放
                mediaPlayer.start();//开始播放
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer arg0) {
                        next();//如果当前歌曲播放完毕,自动播放下一首.
                    }
                });
                //暂停图标
                imageButton.setImageResource(android.R.drawable.ic_media_pause);
            } catch (Exception e) {
                Log.v("MusicService", e.getMessage());
            }
        }
    }

    public void playOrPause() {
        //实例化mediaPlayer
        if (mediaPlayer == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                mediaPlayer = new MediaPlayer();

                //设置音频流的类型
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //设置音源
                try {
                    mediaPlayer.setDataSource
                            (PlayService.this, Uri.parse(Api.getMusicFile(musicList.get(songNum).getId())));
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            try {
                mediaPlayer.reset(); //重置多媒体
                String dataSource = Api.getMusicFile(musicList.get(songNum).getId());//得到当前播放音乐的路径
                // 指定参数为音频文件
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(dataSource);//为多媒体对象设置播放路径
                mediaPlayer.prepare();//准备播放

                mediaPlayer.start();//开始播放
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer arg0) {
                        next();//如果当前歌曲播放完毕,自动播放下一首.
                    }
                });
            } catch (Exception e) {
                Log.v("MusicService", e.getMessage());
            }
        }
    }

    /**
     * 下一首
     */
    public void nextMusic() {
        if (mediaPlayer != null && songNum >= 0) {
            next();
        }
    }

    /**
     * 上一首
     */
    public void preciousMusic() {
        if (mediaPlayer != null && songNum > 0) {
            last();
        }
    }


    /**
     * 关闭播放器
     */
    public void closeMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void play(MusicInfo musicInfo) {
        try {
            mediaPlayer.reset(); //重置多媒体
            String dataSource = Api.getMusicFile(musicInfo.getId());//得到当前播放音乐的路径
            // 指定参数为音频文件
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(dataSource);//为多媒体对象设置播放路径
            mediaPlayer.prepare();//准备播放
            mediaPlayer.start();//开始播放
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    songNum++;
                    next();//如果当前歌曲播放完毕,自动播放下一首.
                }
            });
        } catch (Exception e) {
            Log.v("MusicService", e.getMessage());
        }
    }

    //继续播放
    public void goPlay() {
        int position = getCurrentProgress();
        mediaPlayer.seekTo(position);//设置当前mediaPlayer的播放位置，单位是毫秒。
        try {
            mediaPlayer.prepare();//  同步的方式装载流媒体文件。
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    // 获取当前进度
    public int getCurrentProgress() {
        if (mediaPlayer != null & mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        } else if (mediaPlayer != null & (!mediaPlayer.isPlaying())) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void next() {
        songNum = songNum == musicList.size() - 1 ? 0 : songNum + 1;
        play(musicList.get(songNum));
    }

    public void last() {
        songNum = songNum == 0 ? musicList.size() - 1 : songNum - 1;
        play(musicList.get(songNum));
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(Api.getMusicFile(musicList.get(songNum).getId()));
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

