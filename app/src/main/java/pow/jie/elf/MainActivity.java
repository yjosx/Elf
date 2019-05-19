package pow.jie.elf;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pow.jie.elf.ImageLoader.ImageLoader;
import pow.jie.elf.api.Api;
import pow.jie.elf.bean.MusicInfo;
import pow.jie.elf.service.PlayService;
import pow.jie.elf.ui.MoodChangerView;
import pow.jie.elf.ui.PlayDiskBitmap;
import pow.jie.elf.util.DBTools;
import pow.jie.elf.util.JsonParser;
import pow.jie.elf.util.NetworkTools;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private PlayService playService;
    private TextView title, author;
    private MoodChangerView moodChanger;
    private ImageView album, btnDetail;
    private RelativeLayout relativeLayout;
    private MediaPlayer mediaPlayer;
    private MyHandler handler;
    private List<MusicInfo> musicInfos;

    private boolean isLyricVisible = false;
    private int mood = MOOD_CLAM;
    Intent MediaServiceIntent;
    ImageLoader imageLoader = new ImageLoader();

    private static int MOOD_CLAM = 0;
    private static int MOOD_EXCITING = 1;
    private static int MOOD_HAPPY = 2;
    private static int MOOD_UNHAPPY = 3;
    private static String[] moods = new String[]{"CLAM", "EXCITING", "HAPPY", "UNHAPPY"};

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

    private void bindServiceConnection() {
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
        bindService(intent, connection, this.BIND_AUTO_CREATE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //取得musicInfo
        Intent intent = getIntent();
        Long id = intent.getLongExtra("listId", -1);
        String jsonData = intent.getStringExtra("jsonData");
        musicInfos = JsonParser.parseMusicInfo(jsonData);
        for (MusicInfo musicInfo : musicInfos) {
            DBTools.saveMusicInfo(musicInfo,this);
        }

        init();
        handler = new MyHandler(this);
        moodChanger.setMenuListener(new MoodChangerView.MenuListener() {
            @Override
            public void click(int i) {
                mood = i;
                String address = Api.getSongList(moods[i]);
                requestList(address);
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMe(v);
            }
        });
        bindServiceConnection();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_recommend) {
            Log.d(TAG, "onNavigationItemSelected: recommends");
        } else if (id == R.id.nav_comments) {
            Log.d(TAG, "onNavigationItemSelected: comments");
        } else if (id == R.id.nav_collection) {
            Log.d(TAG, "onNavigationItemSelected: collection");
        } else if (id == R.id.nav_settings) {
            Log.d(TAG, "onNavigationItemSelected: settings");
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //滑动过程中不断回调 slideOffset:0~1
                View content = drawer.getChildAt(0);
                float scale = 1 - slideOffset;//1~0
                content.setTranslationX(-drawerView.getMeasuredWidth() * (1 - scale));//0~width
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        ImageView menuIcon = findViewById(R.id.title_menu);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });
        moodChanger = findViewById(R.id.mood_changer);
        album = findViewById(R.id.iv_album);
        btnDetail = findViewById(R.id.iv_play_detail);
        title = findViewById(R.id.tv_title);
        author = findViewById(R.id.tv_play_time);
        relativeLayout = findViewById(R.id.rl_lyric);
    }

    public void onClickMe(View v) {
        isLyricVisible = !isLyricVisible;
        if (!isLyricVisible) {
            TranslateAnimation in = new TranslateAnimation(0, 0, relativeLayout.getHeight() - v.getHeight(), 0);
            in.setDuration(700);
            in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    relativeLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }
            });
            relativeLayout.startAnimation(in);
        } else {
            TranslateAnimation out = new TranslateAnimation(0, 0, 0, relativeLayout.getHeight() - v.getHeight());
            out.setDuration(700);
            out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    relativeLayout.setVisibility(View.GONE);
                }
            });
            relativeLayout.startAnimation(out);
        }

    }

    private void play() {
        final MusicInfo musicInfo = musicInfos.get(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageLoader.displayImage(musicInfo.getPicUrl(), album);
                title.setText(musicInfo.getName());
                author.setText(musicInfo.getAuthorName());
                PlayDiskBitmap.AnimatorAction(album);
            }
        });
        playService.play(musicInfo);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = ((PlayService.MyBinder) service).getService();
            play();

            Log.d(TAG, "Service与Activity已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: 不能连接");
        }
    };

    private class MyHandler extends Handler {

        Context mContext;

        MyHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_MUSIC:
                    playService.playOrPause();
                case LOAD_ERROR:
                    Toast.makeText(mContext, "加载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void requestList(String address) {
        NetworkTools.sendHttpRequest(address, new NetworkTools.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                List<MusicInfo> musicInfoList = JsonParser.parseMusicInfo(response);
                if (musicInfoList != null) {
                    for (MusicInfo musicInfo : musicInfoList) {
                        DBTools.saveMusicInfo(musicInfo, MainActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
