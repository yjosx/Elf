package pow.jie.elf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import pow.jie.elf.api.Api;
import pow.jie.elf.util.JsonParser;
import pow.jie.elf.util.NetworkTools;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏以及状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        String address = Api.getSongList();
        NetworkTools.sendHttpRequest(address, new NetworkTools.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final long id = JsonParser.parseListId(response);
                Log.d(TAG, "onFinish: " + id);
                String playListAddress = Api.getMusicDetail(id);
                Log.d(TAG, "onFinish: " + playListAddress);
                NetworkTools.sendHttpRequest(playListAddress, new NetworkTools.HttpCallbackListener() {

                    @Override
                    public void onFinish(String response) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("listId", id);
                        Log.d(TAG, "onFinish: " + id);
                        intent.putExtra("jsonData", response);
                        Log.d(TAG, "onFinish: " + response);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: 歌单请求失败");
                    }
                });

            }


            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: 歌单id请求失败");
            }
        });
    }


}
