package com.kuoruan.bomberman.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.data.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.BaseColumns._ID;

/**
 * Created by Liao on 2016/5/1 0001.
 */
public class GameDataDao {

    public static final String TABLE_NAME = "gameUnitData";

    public static final String TYPE = "type";
    public static final String SUB_TYPE = "subType";
    public static final String DRAWABLE = "drawable";
    public static final String VISIBLE = "visible";

    //图片类型
    public static final int TYPE_TILE = 1;
    public static final int TYPE_PLAYER = 2;
    public static final int TYPE_BOMB = 3;
    public static final int TYPE_BOMB_FIRE = 4;

    public static final int FIELD_ID_ID = 0;
    public static final int FIELD_ID_TYPE = 1;
    public static final int FIELD_ID_SUB_TYPE = 2;
    public static final int FIELD_ID_DRAWABLE = 3;
    public static final int FIELD_ID_VISIBLE = 4;

    private GameOpenHelper mGameOpenHelper;
    private Context mContext;
    private static GameDataDao mGameData;

    private GameDataDao(Context context) {
        this.mContext = context;
        mGameOpenHelper = new GameOpenHelper(context);
    }

    public static GameDataDao getInstance(Context context) {
        if (mGameData == null) {
            synchronized (GameDataDao.class) {
                if (mGameData == null) {
                    mGameData = new GameDataDao(context);
                }
            }
        }
        return mGameData;
    }

    public Map<Integer, GameData> getGameTileData() {
        SQLiteDatabase db = mGameOpenHelper.getReadableDatabase();

        String[] from = {_ID, TYPE, SUB_TYPE, DRAWABLE, VISIBLE};
        String where = TYPE + " = " + TYPE_TILE;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        Map<Integer, GameData> data = new HashMap<>();

        while (cursor.moveToNext()) {
            GameData tileData = new GameData();
            tileData.setId(cursor.getInt(FIELD_ID_ID));
            tileData.setType(cursor.getInt(FIELD_ID_TYPE));
            int subType = cursor.getInt(FIELD_ID_SUB_TYPE);
            tileData.setSubType(subType);
            tileData.setDrawable(cursor.getInt(FIELD_ID_DRAWABLE));
            int visible = cursor.getInt(FIELD_ID_VISIBLE);
            tileData.setVisible(visible == 1);
            data.put(subType, tileData);
        }
        cursor.close();
        db.close();

        return data;
    }

    public Map<Integer, GameData> getPlayerData() {
        SQLiteDatabase db = mGameOpenHelper.getReadableDatabase();

        String[] from = {_ID, TYPE, SUB_TYPE, DRAWABLE};
        String where = TYPE + " = " + TYPE_PLAYER;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);
        Map<Integer, GameData> data = new HashMap<>();

        while (cursor.moveToNext()) {
            GameData playerData = new GameData();

            playerData.setId(cursor.getInt(FIELD_ID_ID));
            playerData.setType(cursor.getInt(FIELD_ID_TYPE));
            int id = cursor.getInt(FIELD_ID_SUB_TYPE);
            playerData.setSubType(id);
            playerData.setDrawable(cursor.getInt(FIELD_ID_DRAWABLE));

            data.put(id, playerData);
        }

        cursor.close();
        db.close();

        return data;
    }

    public Map<Integer, GameData> getBombData() {
        SQLiteDatabase db = mGameOpenHelper.getReadableDatabase();
        String[] from = {_ID, TYPE, SUB_TYPE, DRAWABLE};
        String where = TYPE + " = " + TYPE_BOMB;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        Map<Integer, GameData> data = new HashMap<>();

        while (cursor.moveToNext()) {
            GameData bombData = new GameData();
            bombData.setId(cursor.getInt(FIELD_ID_ID));
            bombData.setType(cursor.getInt(FIELD_ID_TYPE));
            int subType = cursor.getInt(FIELD_ID_SUB_TYPE);
            bombData.setSubType(subType);
            bombData.setDrawable(cursor.getInt(FIELD_ID_DRAWABLE));

            data.put(subType, bombData);
        }

        cursor.close();
        db.close();

        return data;
    }

    public Map<Integer, GameData> getFireData() {
        SQLiteDatabase db = mGameOpenHelper.getReadableDatabase();
        String[] from = {_ID, TYPE, SUB_TYPE, DRAWABLE};
        String where = TYPE + " = " + TYPE_BOMB_FIRE;
        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        Map<Integer, GameData> data = new HashMap<>();

        while (cursor.moveToNext()) {
            GameData fireData = new GameData();
            fireData.setId(cursor.getInt(FIELD_ID_ID));
            fireData.setType(cursor.getInt(FIELD_ID_TYPE));
            int subType = cursor.getInt(FIELD_ID_SUB_TYPE);
            fireData.setSubType(subType);
            fireData.setDrawable(cursor.getInt(FIELD_ID_DRAWABLE));

            data.put(subType, fireData);
        }
        cursor.close();
        db.close();

        return data;
    }

}
