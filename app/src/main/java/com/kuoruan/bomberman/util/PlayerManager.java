package com.kuoruan.bomberman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;

import com.kuoruan.bomberman.dao.GameDataDao;
import com.kuoruan.bomberman.entity.Player;
import com.kuoruan.bomberman.entity.data.GameData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Window10 on 2016/5/3.
 */
public class PlayerManager {
    private static final String TAG = "PlayerManager";
    public static int mId = 1;
    private GameDataDao mGameDataDao;

    private Map<Integer, Player> mPlayers = new HashMap<>();
    private Player mMyPlayer = null;
    private Map<Integer, GameData> mPlayerData = null;
    private Map<Integer, Map<Integer, List<Bitmap>>> mPlayerTemplates = new HashMap<>();
    private static final Point[] positions = {new Point(1, 1), new Point(15, 11), new Point(1, 11), new Point(15, 1)};

    private Context mContext;
    private Handler mHandler;

    public PlayerManager(Context context, Handler handler, int id) {
        mContext = context;
        mHandler = handler;
        mId = id;
        mGameDataDao = GameDataDao.getInstance(context);
        mPlayerData = mGameDataDao.getPlayerData();
        prepareMyPlayer();
    }

    /**
     * 初始化玩家
     *
     * @return
     */
    public void prepareMyPlayer() {
        Point mapPoint = positions[mId];
        addPlayer(mId, mapPoint);
        mMyPlayer = mPlayers.get(mId);
        noticeMyPlayer();
    }

    /**
     * 添加一个新的玩家
     */
    public void addPlayer(int id, Point mapPoint) {
        Bitmap bitmap = getFirstBitmap(id);
        mapPoint.x *= bitmap.getWidth();
        mapPoint.y = bitmap.getHeight();
        Player player = new Player(bitmap, mPlayerTemplates.get(id), mapPoint);
        player.setId(id);
        mPlayers.put(id, player);
    }

    private Bitmap getFirstBitmap(int id) {
        Map<Integer, List<Bitmap>> playerTemplate = setAndGetPlayerTemplates(id);
        return playerTemplate.get(Player.DIRECTION_DOWN).get(0);
    }

    /**
     * 准备玩家图片模板
     *
     * @param id
     */
    private Map<Integer, List<Bitmap>> setAndGetPlayerTemplates(int id) {
        if (!mPlayerTemplates.containsKey(id)) {
            GameData data = mPlayerData.get(id);

            Bitmap baseBitmap = BitmapManager.setAndGetBitmap(mContext, data.getDrawable());

            int width = baseBitmap.getWidth() / 3;
            int height = baseBitmap.getHeight() / 7;

            Map<Integer, List<Bitmap>> map = new HashMap<>();
            for (int y = 0; y < 4; y++) {
                List<Bitmap> list = new ArrayList<>();
                for (int x = 0; x < 3; x++) {
                    Bitmap newBitmap = Bitmap.createBitmap(baseBitmap, x * width, y * height, width, height);
                    list.add(newBitmap);
                }

                map.put(y + 1, list);
            }

            List<Bitmap> dieBitmaps = new ArrayList<>();
            for (int y = 4; y < 7; y++) {
                for (int x = 0; x < 3; x++) {
                    Bitmap newBitmap = Bitmap.createBitmap(baseBitmap, x * width, y * height, width, height);
                    dieBitmaps.add(newBitmap);
                }
            }

            map.put(Player.PLAYER_DIE, dieBitmaps);
            mPlayerTemplates.put(id, map);
        }

        return mPlayerTemplates.get(id);
    }

    public Map<Integer, Player> getPlayers() {
        return mPlayers;
    }

    public int getPlayerCount() {
        return mPlayers.size();
    }

    public void handlePlayerMove(JSONObject jsonObject) {
        int pid = 0;
        int direction = 0;
        int x = 0;
        int y = 0;
        try {
            pid = jsonObject.getInt(ConnectConstants.PLAYER_ID);
            direction = jsonObject.getInt(ConnectConstants.DIRECTION);
            x = jsonObject.getInt(ConnectConstants.POINT_X);
            y = jsonObject.getInt(ConnectConstants.POINT_Y);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Player netPlayer = mPlayers.get(pid);
        netPlayer.setX(x);
        netPlayer.setY(y);
        netPlayer.setDirection(direction);
        netPlayer.setState(Player.STATE_MOVING);
    }

    public Player getMyPlayer() {
        return mMyPlayer;
    }

    public void handlePlayerAdd(JSONObject jsonObject) {
        int pid = 0;
        int x = 0;
        int y = 0;
        try {
            pid = jsonObject.getInt(ConnectConstants.PLAYER_ID);
            x = jsonObject.getInt(ConnectConstants.POINT_X);
            y = jsonObject.getInt(ConnectConstants.POINT_Y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mPlayers.containsKey(pid)) {
            return;
        }

        addPlayer(pid, new Point(x, y));
    }

    public void handlePlayerStop(JSONObject jsonObject) {
        int pid = 0;
        try {
            pid = jsonObject.getInt(ConnectConstants.PLAYER_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (pid == mId) {
            return;
        }
        Player netPlayer = mPlayers.get(pid);
        netPlayer.setState(Player.STATE_STOP);
        netPlayer.setDirection(0);
    }

    public void handlePlayerDie(JSONObject jsonObject) {
        int pid = 0;
        try {
            pid = jsonObject.getInt(ConnectConstants.PLAYER_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (pid == mId) {
            return;
        }
        Player netPlayer = mPlayers.get(pid);
        netPlayer.setState(Player.STATE_DIE);
        netPlayer.setDirection(0);
    }

    public void noticeMyMove() {
        if (!SceneManager.isMultiStage()) return;

        Message msg = Message.obtain();
        msg.what = GameConstants.PLAYER_MOVE;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.TYPE, GameConstants.PLAYER_MOVE);
            jsonObject.put(ConnectConstants.PLAYER_ID, mId);
            jsonObject.put(ConnectConstants.DIRECTION, mMyPlayer.getDirection());
            jsonObject.put(ConnectConstants.POINT_X, mMyPlayer.getX());
            jsonObject.put(ConnectConstants.POINT_Y, mMyPlayer.getY());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        msg.obj = jsonObject;
        mHandler.sendMessage(msg);
    }


    private void noticeMyPlayer() {
        if (!SceneManager.isMultiStage()) return;

        Message msg = Message.obtain();
        msg.what = GameConstants.PLAYER_ADD;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.TYPE, GameConstants.PLAYER_ADD);
            jsonObject.put(ConnectConstants.PLAYER_ID, mId);
            jsonObject.put(ConnectConstants.POINT_X, mMyPlayer.getX());
            jsonObject.put(ConnectConstants.POINT_Y, mMyPlayer.getY());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        msg.obj = jsonObject;
        mHandler.sendMessage(msg);
    }

    public void noticeMyStop() {
        if (!SceneManager.isMultiStage()) return;

        Message msg = Message.obtain();
        msg.what = GameConstants.PLAYER_STOP;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.TYPE, GameConstants.PLAYER_STOP);
            jsonObject.put(ConnectConstants.PLAYER_ID, mId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.obj = jsonObject;
        mHandler.sendMessage(msg);
    }

    public void noticeMyDie() {
        mMyPlayer.setState(Player.STATE_DIE);

        Message msg = Message.obtain();
        msg.what = GameConstants.PLAYER_DIE;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.TYPE, GameConstants.PLAYER_DIE);
            jsonObject.put(ConnectConstants.PLAYER_ID, mId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.obj = jsonObject;
        mHandler.sendMessage(msg);
    }
}
