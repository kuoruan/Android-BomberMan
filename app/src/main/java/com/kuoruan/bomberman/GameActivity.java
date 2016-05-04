package com.kuoruan.bomberman;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.kuoruan.bomberman.net.NetPlayer;
import com.kuoruan.bomberman.net.NetPlayerManager;
import com.kuoruan.bomberman.net.UdpClient;
import com.kuoruan.bomberman.net.UdpServerThread;
import com.kuoruan.bomberman.view.GameView;

/**
 * Created by Liao on 2016/5/1 0001.
 */
public class GameActivity extends Activity {

    private GameView mGameView = null;

    private DisplayMetrics mMetrics = new DisplayMetrics();
    private float mScreenDensity;
    private Context mMContext;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(mMContext, "start...", Toast.LENGTH_LONG);
            super.handleMessage(msg);
        }
    };
    private NetPlayer mNetPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMContext = getApplicationContext();

        /**
         * Get the screen density that all pixel values will be based on.
         * This allows scaling of pixel values over different screen sizes.
         *
         * See: http://developer.android.com/reference/android/util/DisplayMetrics.html
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mScreenDensity = mMetrics.density;

        /**
         * There is only one stage / level in this example.
         * In a real game, the user's chosen stage / level should be
         * passed to this activity.
         */
        new UdpServerThread(mMContext).start();
        mNetPlayer = NetPlayerManager.createNetPlayer(mMContext);
        NetPlayerManager.setMyPlayer(mNetPlayer);
        UdpClient.noticeAddPlayer(mNetPlayer);
        int stage = 0;


        Log.d("Tile Game Example", "Starting game at stage: " + stage);
        mGameView = new GameView(mMContext, this, GameView.MULTI_PLAYER_STAGE, mScreenDensity, mNetPlayer);

        setContentView(mGameView);
    }
}
