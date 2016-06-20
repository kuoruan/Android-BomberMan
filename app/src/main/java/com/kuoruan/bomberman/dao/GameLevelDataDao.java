package com.kuoruan.bomberman.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuoruan.bomberman.entity.data.GameLevelData;

import static android.provider.BaseColumns._ID;

/**
 * Created by Liao on 2016/5/2 0002.
 */
public class GameLevelDataDao {
    public static final String TABLE_NAME = "gameLevelTileData";

    public static final String STAGE = "stage";
    public static final String PLAYER_START_TILE_X = "playerStartX";
    public static final String PLAYER_START_TILE_Y = "playerStartY";
    public static final String TILE_DATA = "tileData";

    public static final int FIELD_ID_ID = 0;
    public static final int FIELD_ID_STAGE = 1;
    public static final int FIELD_ID_PLAYER_START_X = 2;
    public static final int FIELD_ID_PLAYER_START_Y = 3;
    public static final int FIELD_ID_TILE_DATA = 4;

    public static final String TILE_DATA_LINE_BREAK = "//";

    private static GameLevelDataDao mGameLevelTileDao;
    private GameOpenHelper mGameOpenHelper;
    private Context mContext;

    private GameLevelDataDao(Context context) {
        mGameOpenHelper = new GameOpenHelper(context);
        this.mContext = context;
    }

    public static GameLevelDataDao getInstance(Context context) {
        if (mGameLevelTileDao == null) {
            synchronized (GameLevelDataDao.class) {
                if (mGameLevelTileDao == null) {
                    mGameLevelTileDao = new GameLevelDataDao(context);
                }
            }
        }

        return mGameLevelTileDao;
    }

    /**
     * 获取关卡地图数据集合
     *
     * @param stage 关卡
     * @return ArrayList
     */
    public GameLevelData getGameLevelData(int stage) {

        SQLiteDatabase db = mGameOpenHelper.getReadableDatabase();

        String[] from = {_ID, STAGE, PLAYER_START_TILE_X, PLAYER_START_TILE_Y, TILE_DATA};
        String where = STAGE + " = " + stage;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        GameLevelData gameLevelData = null;
        if (cursor.moveToNext()) {
            gameLevelData = new GameLevelData();
            gameLevelData.setId(cursor.getInt(FIELD_ID_ID));
            gameLevelData.setPlayerStartX(cursor.getInt(FIELD_ID_PLAYER_START_X));
            gameLevelData.setPlayerStartY(cursor.getInt(FIELD_ID_PLAYER_START_Y));
            gameLevelData.setStage(cursor.getInt(FIELD_ID_STAGE));
            gameLevelData.setLevelTiles(cursor.getString(FIELD_ID_TILE_DATA));
        }
        cursor.close();
        db.close();
        return gameLevelData;
    }

}
