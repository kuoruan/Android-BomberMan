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

import com.kuoruan.bomberman.entity.Player;
import com.kuoruan.bomberman.util.PlayerManager;
import com.kuoruan.bomberman.net.UdpClient;
import com.kuoruan.bomberman.net.UdpServer;
import com.kuoruan.bomberman.view.GameView;

/**
 * Created by Liao on 2016/5/1 0001.
 */
public class GameActivity extends Activity {

    private GameView mGameView = null;

    private DisplayMetrics mMetrics = new DisplayMetrics();
    private float mScreenDensity;
    private Context mContext;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(mContext, "start...", Toast.LENGTH_LONG);
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();

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
        mGameView = new GameView(mContext, this, GameView.MULTI_PLAYER_STAGE, mScreenDensity);

        new UdpServer(mContext).start();
        int stage = 0;

        Log.d("Tile Game Example", "Starting game at stage: " + stage);
        setContentView(mGameView);
    }

    @Override
    protected void onPause() {
        mGameView.getGameThread().setState(GameView.STATE_PAUSED);
        super.onPause();
    }

}
