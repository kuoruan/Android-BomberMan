package com.kuoruan.bomberman.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.kuoruan.bomberman.util.GameConstants;
import com.kuoruan.bomberman.util.PlayerManager;
import com.kuoruan.bomberman.util.SceneManager;
import com.kuoruan.bomberman.view.GameView;

/**
 * Created by Liao on 2016/5/1 0001.
 */
public class GameActivity extends BaseActivity {

    private GameView mGameView = null;
    private PlayerManager mPlayerManager;
    private SceneManager mSceneManager;

    private Context mContext;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(mContext, "start...", Toast.LENGTH_LONG);
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mContext = getApplicationContext();

        Display display = getWindowManager().getDefaultDisplay();

        int playerId = 1;
        //mPlayerManager = new PlayerManager(mContext, playerId);
        //mSceneManager = new SceneManager(mContext, GameConstants.MULTI_PLAYER_STAGE);

        mGameView = new GameView(mContext, mPlayerManager, mSceneManager, display);

        setContentView(mGameView);
    }

    @Override
    protected void onPause() {
        mGameView.getGameThread().setState(GameView.STATE_PAUSED);
        super.onPause();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onBeforeSetContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
