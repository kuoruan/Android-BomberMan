package com.kuoruan.bomberman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

import com.kuoruan.bomberman.dao.GameDataDao;
import com.kuoruan.bomberman.dao.GameLevelDataDao;
import com.kuoruan.bomberman.entity.Bomb;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.data.GameData;
import com.kuoruan.bomberman.entity.data.GameLevelData;

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

    private int mTotalBombCount = GameConstants.DEFAULT_BOMB_COUNT;
    private int mPlayerBombCount = 0;

    private int mGameStage = 0;

    private int mBombType = Bomb.TYPE_NORMAL;

    private Map<Integer, GameData> mTileData = null;
    private Map<Integer, GameData> mBombData = null;
    private Map<Integer, GameData> mFireData = null;

    private Map<Integer, List<Bitmap>> mBombTemplates = new HashMap<>();
    private Map<Integer, List<Bitmap>> mFireTemplates = new HashMap<>();

    private GameTile[][] mGameTiles = null;
    private Context mContext;
    private Handler mHandler;

    private GameDataDao mGameDataDao;
    private GameLevelDataDao mGameLevelDataDao;
    private GameLevelData mGameLevelData;

    public SceneManager(Context context, Handler handler, int stage) {
        mContext = context;
        mHandler = handler;
        mGameStage = stage;
        mGameDataDao = GameDataDao.getInstance(context);
        this.mTileData = mGameDataDao.getGameTileData();
        this.mBombData = mGameDataDao.getBombData();
        mGameLevelDataDao = GameLevelDataDao.getInstance(context);
    }

    /**
     * 处理地图数据
     */
    public GameTile[][] parseGameTileData() {
        mGameLevelData = mGameLevelDataDao.getGameLevelData(mGameStage);
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
     * 炸弹爆炸，创建火焰
     *
     * @param bomb
     */
    public void explosionBomb(Bomb bomb) {

    }

    public void setBomb(Point mapPoint) {
        if (mGameTiles != null) {
            List<Bitmap> bombTemplate = setAndGetBombTemplates(mBombType);
            Point point = new Point();
            point.x = mapPoint.x * tileWidth;
            point.y = mapPoint.y * tileHeight;
            mGameTiles[mapPoint.x][mapPoint.y] = new Bomb(bombTemplate, true, point, PlayerManager.mId);
        }
    }
}
