package com.kuoruan.bomberman.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.kuoruan.bomberman.net.ConnectedService;
import com.kuoruan.bomberman.util.ConnectConstants;
import com.kuoruan.bomberman.util.GameConstants;
import com.kuoruan.bomberman.util.PlayerManager;
import com.kuoruan.bomberman.util.SceneManager;
import com.kuoruan.bomberman.view.GameView;

import org.json.JSONObject;

/**
 * Created by Liao on 2016/6/16 0016.
 */
public class WifiGameActivity extends BaseActivity {

    private GameView mGameView;
    private PlayerManager mPlayerManager;
    private SceneManager mSceneManager;

    private Context mContext;
    private boolean mIsServer;
    private String mDstIp;
    private ProgressDialog mWaitDialog;
    private ConnectedService mConnectedService;

    /**
     * 处理游戏回调信息，刷新界面
     */
    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;
            switch (msg.what) {
                case GameConstants.PLAYER_ADD:
                case GameConstants.PLAYER_MOVE:
                case GameConstants.PLAYER_STOP:
                    mConnectedService.sendTCPData(jsonObject);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 处理网络信息，更新界面
     */
    private Handler mRequestHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;
            switch (msg.what) {
                case ConnectConstants.GAME_CONNECTED:
                    mWaitDialog.dismiss();
                    break;
                case GameConstants.PLAYER_ADD:
                    mPlayerManager.handlePlayerAdd(jsonObject);
                    break;
                case GameConstants.PLAYER_MOVE:
                    mPlayerManager.handlePlayerMove(jsonObject);
                case GameConstants.PLAYER_STOP:
                    mPlayerManager.handlePlayerStop(jsonObject);
                default:
                    break;
            }
        }
    };


    public static void startActivity(Context context, boolean isServer, String dstIp) {
        Intent intent = new Intent(context, WifiGameActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("isServer", isServer);
        b.putString("ip", dstIp);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        if (b == null) {
            Toast.makeText(this, "建立网络失败,请重试", Toast.LENGTH_SHORT).show();
            finish();
        }

        showProgressDialog(null, "建立连接中，请稍后");
        mContext = getApplicationContext();
        mIsServer = b.getBoolean("isServer");
        mDstIp = b.getString("ip");
        initGame();
    }

    private void initGame() {
        Display display = getWindowManager().getDefaultDisplay();
        mConnectedService = new ConnectedService(mRequestHandler, mDstIp, mIsServer); // TCP连接
        int playerId = mIsServer ? 1 : 2;

        mPlayerManager = new PlayerManager(mContext, mRefreshHandler, playerId);
        mSceneManager = new SceneManager(mContext, mRefreshHandler, GameConstants.MULTI_PLAYER_STAGE);

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

    private void showProgressDialog(String title, String message) {
        if (mWaitDialog == null) {
            mWaitDialog = new ProgressDialog(this);
        }
        if (!TextUtils.isEmpty(title)) {
            mWaitDialog.setTitle(title);
        }
        mWaitDialog.setMessage(message);
        mWaitDialog.setIndeterminate(true);
        mWaitDialog.setCancelable(true);
        mWaitDialog.show();
    }
}
