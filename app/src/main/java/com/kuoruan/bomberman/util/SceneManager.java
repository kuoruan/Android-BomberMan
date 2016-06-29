package com.kuoruan.bomberman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kuoruan.bomberman.dao.GameDataDao;
import com.kuoruan.bomberman.dao.GameLevelDataDao;
import com.kuoruan.bomberman.entity.Bomb;
import com.kuoruan.bomberman.entity.BombFire;
import com.kuoruan.bomberman.entity.Collision;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.Player;
import com.kuoruan.bomberman.entity.data.GameData;
import com.kuoruan.bomberman.entity.data.GameLevelData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liao on 2016/5/8 0008.
 */
public class SceneManager {

    private final String TAG = "SceneManager";

    public static int sceneXMax = 0;
    public static int sceneYMax = 0;

    public static int tileWidth = 0;
    public static int tileHeight = 0;

    public static int mTotalBombCount = GameConstants.DEFAULT_BOMB_COUNT;
    public static int gameStage = 0;

    private int mPlayerBombCount = 0;
    private int mBombType = Bomb.NORMAL;
    private int mFireType = BombFire.NORMAL;

    private Map<Integer, GameData> mTileData = null;
    private Map<Integer, GameData> mBombData = null;
    private Map<Integer, GameData> mFireData = null;

    private Map<Integer, List<Bitmap>> mBombTemplates = new HashMap<>();
    private Map<Integer, Map<Integer, List<Bitmap>>> mFireTemplates = new HashMap<>();

    //地图上所有的砖块
    private GameTile[][] mGameTiles = null;
    private List<GameTile> mBombFires = new ArrayList<>();
    //玩家周围的砖块
    private List<GameTile> mSurroundTiles = new ArrayList<>();
    private Context mContext;
    private Handler mHandler;

    private GameDataDao mGameDataDao;
    private GameLevelDataDao mGameLevelDataDao;
    private GameLevelData mGameLevelData;

    public SceneManager(Context context, Handler handler, int stage) {
        mContext = context;
        mHandler = handler;
        gameStage = stage;
        mGameDataDao = GameDataDao.getInstance(context);
        this.mTileData = mGameDataDao.getGameTileData();
        this.mBombData = mGameDataDao.getBombData();
        this.mFireData = mGameDataDao.getFireData();
        mGameLevelDataDao = GameLevelDataDao.getInstance(context);
    }

    /**
     * 处理地图数据
     */
    public GameTile[][] parseGameTileData() {
        mGameLevelData = mGameLevelDataDao.getGameLevelData(gameStage);
        String levelTileData = mGameLevelData.getLevelTiles();

        if (levelTileData == null) {
            return null;
        }

        Bitmap bitmap;
        Point tilePoint = new Point();
        int tileX = 0;
        int tileY = 0;

        String[] tileLines = levelTileData.split(GameLevelDataDao.TILE_DATA_LINE_BREAK);
        int rows = tileLines.length;
        int cols = 0;

        for (int i = 0; i < rows; i++) {
            String[] tiles = tileLines[i].split(",");

            //如果没有列数目
            if (cols == 0 && mGameTiles == null) {
                cols = tiles.length;
                mGameTiles = new GameTile[rows][cols];
            }

            for (int j = 0; j < cols; j++) {
                int tileNum = Integer.parseInt(tiles[j]);
                GameData gameTileData = mTileData.get(tileNum);

                if ((mGameTiles.length > 0) && (gameTileData != null)) {
                    tilePoint.x = tileX;
                    tilePoint.y = tileY;

                    bitmap = BitmapManager.setAndGetBitmap(mContext, gameTileData.getDrawable());
                    GameTile gameTile = new GameTile(bitmap, tilePoint, gameTileData.getSubType());
                    gameTile.setVisible(gameTileData.isVisible());

                    if (tileWidth == 0) {
                        tileWidth = gameTile.getWidth();
                    }
                    if (tileHeight == 0) {
                        tileHeight = gameTile.getHeight();
                    }

                    if (sceneXMax == 0 && cols > 0) {
                        sceneXMax = cols * tileWidth;
                    }
                    if (sceneYMax == 0 && rows > 0) {
                        sceneYMax = rows * tileHeight;
                    }

                    mGameTiles[i][j] = gameTile;
                }

                tileX += tileWidth;
            }
            tileX = 0;
            tileY += tileHeight;
        }

        return mGameTiles;
    }

    /**
     * 获取炸弹模版
     *
     * @param bombType
     */
    public List<Bitmap> setAndGetBombTemplates(int bombType) {
        if (!mBombTemplates.containsKey(bombType)) {
            GameData data = mBombData.get(bombType);

            Bitmap baseBitmap = BitmapManager.setAndGetBitmap(mContext, data.getDrawable());
            List<Bitmap> bitmaps = new ArrayList<>();

            int baseWidth = baseBitmap.getWidth();
            int baseHeight = baseBitmap.getHeight();
            int width = baseWidth / 2;

            for (int x = 0; x < baseWidth; x += width) {
                Bitmap bitmap = Bitmap.createBitmap(baseBitmap, x, 0, width, baseHeight);
                bitmaps.add(bitmap);
            }

            mBombTemplates.put(bombType, bitmaps);
        }

        return mBombTemplates.get(bombType);
    }

    /**
     * 根据火焰类型和子类型（上、下、左、右、中）或者图片列表
     *
     * @param fireType
     * @param fireSubType
     * @return
     */
    public List<Bitmap> setAndGetFireTemplates(int fireType, int fireSubType) {
        if (!mFireTemplates.containsKey(fireType)) {
            GameData data = mFireData.get(fireType);
            Bitmap fireMain = BitmapManager.setAndGetBitmap(mContext, data.getDrawable());

            int width = fireMain.getWidth() / 4;
            int height = fireMain.getHeight() / 7;

            Map<Integer, List<Bitmap>> subFires = new HashMap<>();
            for (int y = 0; y < 7; y++) {
                List<Bitmap> list = new ArrayList<>();
                for (int x = 0; x < 4; x++) {
                    Bitmap bitmap = Bitmap.createBitmap(fireMain, x * width, y * height, width, height);
                    list.add(bitmap);
                }

                subFires.put(y + 1, list);
            }

            mFireTemplates.put(fireType, subFires);
        }

        return mFireTemplates.get(fireType).get(fireSubType);
    }

    /**
     * 炸弹爆炸，创建火焰
     *
     * @param bomb
     */
    public void explodBomb(Bomb bomb, int x, int y) {
        int fireLength = bomb.getFireLength();

        BombFire middleFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_CENTER));
        middleFire.setMapPoint(x, y);
        mBombFires.add(middleFire);

        //火焰是否可以向四个方向延伸
        boolean spreadUp = true;
        boolean spreadDown = true;
        boolean spreadLeft = true;
        boolean spreadRight = true;

        int newX;
        int newY;

        for (int i = 1; i <= fireLength; i++) {
            //是否是最后一次循环
            boolean isLast = (i == fireLength);
            //上面
            newY = y - i;
            if (spreadUp && canSpread(mGameTiles[newY][x])) {
                BombFire upFire;
                if (!isLast) {
                    upFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_VERTICAL));
                } else {
                    upFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_UP));
                }
                upFire.setMapPoint(x, newY);
                mBombFires.add(upFire);
            } else {
                spreadUp = false;
            }

            //下面
            newY = y + i;
            if (spreadDown && canSpread(mGameTiles[newY][x])) {
                BombFire downFire;
                if (!isLast) {
                    downFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_VERTICAL));
                } else {
                    downFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_DOWN));
                }
                downFire.setMapPoint(x, newY);
                mBombFires.add(downFire);
            } else {
                spreadDown = false;
            }

            //左面
            newX = x - i;
            if (spreadLeft && canSpread(mGameTiles[y][newX])) {
                BombFire leftFire;
                if (!isLast) {
                    leftFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_HORIZONTAL));
                } else {
                    leftFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_LEFT));
                }
                leftFire.setMapPoint(newX, y);
                mBombFires.add(leftFire);
            } else {
                spreadLeft = false;
            }

            //右面
            newX = x + i;
            if (spreadRight && canSpread(mGameTiles[y][newX])) {
                BombFire rightFire;
                if (!isLast) {
                    rightFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_HORIZONTAL));
                } else {
                    rightFire = new BombFire(setAndGetFireTemplates(mFireType, BombFire.TYPE_RIGHT));
                }
                rightFire.setMapPoint(newX, y);
                mBombFires.add(rightFire);
            } else {
                spreadRight = false;
            }

        }
    }

    /**
     * 放置炸弹
     *
     * @param mapPoint
     */
    public void setBomb(Point mapPoint) {
        //地图上的相对点
        int mapX = mapPoint.x;
        int mapY = mapPoint.y;

        GameTile gameTile = mGameTiles[mapY][mapX];
        if (gameTile != null && gameTile.isBlockerTile()) {
            Log.i(TAG, "setBomb: 炸弹已存在");
            return;
        }

        addBomb(PlayerManager.mId, mBombType, mapX, mapY);
        //网络通告
        noticeSetBomb(PlayerManager.mId, mapX, mapY);
    }

    private void addBomb(int pid, int bombType, int mapX, int mapY) {
        //计算绝对坐标点
        Point point = new Point();
        point.x = mapX * tileWidth;
        point.y = mapY * tileHeight;

        List<Bitmap> bombTemplate = setAndGetBombTemplates(bombType);
        Bomb bomb = new Bomb(bombTemplate, true, point, pid);
        mGameTiles[mapY][mapX] = bomb;
    }

    private void noticeSetBomb(int pid, int mapX, int mapY) {
        if (!isMultiStage()) return;

        Message msg = Message.obtain();
        msg.what = GameConstants.SET_BOMB;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.TYPE, GameConstants.SET_BOMB);
            jsonObject.put(ConnectConstants.PLAYER_ID, pid);
            jsonObject.put(ConnectConstants.BOMB_TYPE, mBombType);
            jsonObject.put(ConnectConstants.MAP_X, mapX);
            jsonObject.put(ConnectConstants.MAP_Y, mapY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        msg.obj = jsonObject;
        mHandler.sendMessage(msg);
    }

    public void handleCollision(Collision collision, Player player) {
        Point mapPoint = player.getStandardMapPoint();
        int width = player.getWidth();
        int height = player.getHeight();

        mSurroundTiles.clear();

        //首先获取周围8个位置和当前位置砖块，取当前位置是为了判断是否有火焰
        mSurroundTiles.add(mGameTiles[mapPoint.y - 1][mapPoint.x - 1]); //左上
        mSurroundTiles.add(mGameTiles[mapPoint.y - 1][mapPoint.x]); //上
        mSurroundTiles.add(mGameTiles[mapPoint.y - 1][mapPoint.x + 1]); //右上
        mSurroundTiles.add(mGameTiles[mapPoint.y][mapPoint.x - 1]); //左
        mSurroundTiles.add(mGameTiles[mapPoint.y][mapPoint.x]); //当前
        mSurroundTiles.add(mGameTiles[mapPoint.y][mapPoint.x + 1]); //右
        mSurroundTiles.add(mGameTiles[mapPoint.y + 1][mapPoint.x - 1]); //左下
        mSurroundTiles.add(mGameTiles[mapPoint.y + 1][mapPoint.x]); //下
        mSurroundTiles.add(mGameTiles[mapPoint.y + 1][mapPoint.x + 1]); //右下

        GameTile collisionTile = null;
        int newX = collision.getNewX();
        int newY = collision.getNewY();

        for (GameTile gameTile : mSurroundTiles) {
            if (gameTile != null && gameTile.isCollision(newX, newY, width, height)) {
                //如果存在冲突
                collisionTile = gameTile;
                break;
            }
        }
        if (collisionTile == null) {
            return;
        }

        if (collisionTile.getType() == GameTile.TYPE_BOMB) {
            Point nowPoint = player.getPoint();
            if (collisionTile.isCollision(nowPoint.x, nowPoint.y, width, height)) { //如果炸弹和当前角色有交叉
                return; //允许通过
            } else {
                collision.setSolvable(false); //不许通过
                return;
            }
        }

        collision.setCollisionTile(collisionTile);
        collision.solveCollision();
    }

    public List<GameTile> getBombFires() {
        return mBombFires;
    }

    /**
     * 判断砖块是否是空或者可以破环
     *
     * @param gameTile
     * @return
     */
    private boolean canSpread(GameTile gameTile) {
        return gameTile == null || !gameTile.isBlockerTile();
    }

    /**
     * 判断游戏是否为多人游戏
     *
     * @return
     */
    public static boolean isMultiStage() {
        return gameStage == GameConstants.MULTI_PLAYER_STAGE;
    }

    /**
     * 处理网络炸弹放置
     */
    public void handleSetBomb(JSONObject jsonObject) {
        int pid = 0;
        int bombType = 0;
        int mapX = 0;
        int mapY = 0;

        try {
            pid = jsonObject.getInt(ConnectConstants.PLAYER_ID);
            bombType = jsonObject.getInt(ConnectConstants.BOMB_TYPE);
            mapX = jsonObject.getInt(ConnectConstants.MAP_X);
            mapY = jsonObject.getInt(ConnectConstants.MAP_Y);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addBomb(pid, bombType, mapX, mapY);
    }
}
