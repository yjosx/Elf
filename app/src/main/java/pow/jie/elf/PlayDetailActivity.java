package pow.jie.elf;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pow.jie.elf.ImageLoader.DoubleCache;
import pow.jie.elf.ImageLoader.ImageLoader;
import pow.jie.elf.api.Api;
import pow.jie.elf.bean.MusicInfo;
import pow.jie.elf.service.PlayService;
import pow.jie.elf.ui.MoodChangerView;
import pow.jie.elf.ui.PlayDiskBitmap;
import pow.jie.elf.util.DBTools;
import pow.jie.elf.util.JsonParser;
import pow.jie.elf.util.NetworkTools;

public class PlayDetailActivity extends AppCompatActivity implements View.OnClickListener {

    MoodChangerView back;
    ImageView stop;
    ImageView last;
    ImageView next;
    SeekBar progress;
    TextView playingTime;
    TextView totalTime;
    ImageView collect;
    PlayService playService;
    MusicInfo musicInfo;
    private TextView songName;
    private TextView singer;
    private ImageView face;
    private int songNum = 0;
    int mood = MOOD_CLAM;
    private static int MOOD_CLAM = 0;
    private static int MOOD_EXCITING = 1;
    private static int MOOD_HAPPY = 2;
    private static int MOOD_UNHAPPY = 3;
    private static String[] moods = new String[]{"CLAM", "EXCITING", "HAPPY", "UNHAPPY"};
    ImageLoader imageLoader = new ImageLoader();
    DoubleCache mDoubleCache = new DoubleCache();
    private static final int PLAY_MUSIC = 0;
    private static final int LOAD_ERROR = 1;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = ((PlayService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private List<MusicInfo> musicList = new ArrayList<>();

    private void bindServiceConnection() {
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
        bindService(intent, connection, this.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        musicList = JsonParser.parseMusicInfo(intent.getStringExtra("detail"));
        songNum = intent.getIntExtra("songNum", 0);
        musicInfo = musicList.get(songNum);
        init();
        imageLoader.setmImageCache(mDoubleCache);
        bindServiceConnection();
    }

    private void init() {
        back = findViewById(R.id.mood_changer);
        back.setMenuListener(new MoodChangerView.MenuListener() {
            @Override
            public void click(int i) {
                mood = i;
                String address = Api.getSongList(moods[i]);
                requestList(address);
            }
        });
        stop = findViewById(R.id.iv_play);
        stop.setOnClickListener(this);
        last = findViewById(R.id.iv_move_back);
        last.setOnClickListener(this);
        next = findViewById(R.id.iv_move_forward);
        next.setOnClickListener(this);
        collect = findViewById(R.id.iv_like);
        collect.setOnClickListener(this);
        progress = findViewById(R.id.seekBar);
        progress.setProgress(PlayService.mediaPlayer.getCurrentPosition());
        progress.setMax(PlayService.mediaPlayer.getDuration());
        totalTime = findViewById(R.id.tv_end_time);
        playingTime = findViewById(R.id.tv_play_time);
        songName = findViewById(R.id.tv_title);
        singer = findViewById(R.id.tv_author);
        face = findViewById(R.id.iv_album);
        handler.post(runnable);
        if (!PlayService.mediaPlayer.isPlaying()) {
            stop.setImageResource(R.drawable.ic_play_pause);
        }

    }

    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            collect.setImageResource(R.drawable.ic_star_off);
            if (PlayService.mediaPlayer.isPlaying()) {
                songName.setText(musicInfo.getName());
                singer.setText(musicInfo.getAuthorName());
                imageLoader.displayImage(musicInfo.getPicUrl(), face);
                playingTime.setText(time.format(PlayService.mediaPlayer.getCurrentPosition()));
                totalTime.setText(time.format(PlayService.mediaPlayer.getDuration()));
                progress.setProgress(PlayService.mediaPlayer.getCurrentPosition());
                progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            playService.mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                handler.postDelayed(runnable, 100);
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                if (PlayService.mediaPlayer.isPlaying()) {
                    PlayService.mediaPlayer.pause();
                    stop.setImageResource(R.drawable.ic_play_pause);
                } else {
                    PlayService.mediaPlayer.start();
                    stop.setImageResource(R.drawable.ic_play_running);
                }
                break;
            case R.id.iv_move_back:
                if (songNum == 0) {
                    songNum = musicList.size();
                }
                songNum = songNum - 1;
                musicInfo = musicList.get(songNum);
                playService.play(musicInfo);
                break;
            case R.id.iv_move_forward:
                if (songNum == musicList.size()) {
                    songNum = -1;
                }
                songNum = songNum + 1;
                musicInfo = musicList.get(songNum);
                playService.play(musicInfo);
                break;
            case R.id.iv_like:
                collect.setImageResource(R.drawable.ic_star_on_blue);
                break;
        }
    }

    public void requestList(String address) {
        NetworkTools.sendHttpRequest(address, new NetworkTools.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                List<MusicInfo> musicInfoList = JsonParser.parseMusicInfo(response);
                if (musicInfoList != null) {
                    for (MusicInfo musicInfo : musicInfoList) {
                        DBTools.saveMusicInfo(musicInfo, PlayDetailActivity.this);
                    }
                }
                Message message = new Message();
                message.what = PLAY_MUSIC;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Message message = new Message();
                message.what = LOAD_ERROR;
                handler.sendMessage(message);
            }
        });
    }

    public void onBackPressed() {
        finish();
    }
}

