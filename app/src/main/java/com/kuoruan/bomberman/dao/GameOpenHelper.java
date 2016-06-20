package com.kuoruan.bomberman.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kuoruan.bomberman.R;
import com.kuoruan.bomberman.activity.GameActivity;
import com.kuoruan.bomberman.entity.Bomb;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.util.GameConstants;
import com.kuoruan.bomberman.view.GameView;

import static android.provider.BaseColumns._ID;

/**
 * Created by Liao on 2016/5/2 0002.
 */
public class GameOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bomberman.db";
    private static final int DB_VERSION = 1;

    public GameOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GAME_DATA);
        db.execSQL(CREATE_TABLE_GAME_LEVEL_TILES);

        for (String query : POPULATE_TABLE_GAME_DATA) {
            db.execSQL(query);
        }

        for (String query : POPULATE_TABLE_GAME_LEVEL_TILES) {
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GameDataDao.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GameLevelDataDao.TABLE_NAME);

        onCreate(db);
    }

    private static final String CREATE_TABLE_GAME_DATA = "CREATE TABLE " + GameDataDao.TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY, "
            + GameDataDao.TYPE + " INTEGER DEFAULT 1,"
            + GameDataDao.SUB_TYPE + " INTEGER DEFAULT 0,"
            + GameDataDao.DRAWABLE + " INTEGER DEFAULT 0,"
            + GameDataDao.VISIBLE + " INTEGER DEFAULT 1"
            + ");";

    private static final String CREATE_TABLE_GAME_LEVEL_TILES = "CREATE TABLE " + GameLevelDataDao.TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + GameLevelDataDao.STAGE + " INTEGER DEFAULT 0,"
            + GameLevelDataDao.PLAYER_START_TILE_X + " INTEGER DEFAULT 0,"
            + GameLevelDataDao.PLAYER_START_TILE_Y + " INTEGER DEFAULT 0,"
            + GameLevelDataDao.TILE_DATA + " TEXT NOT NULL"
            + ");";

    private static final String[] POPULATE_TABLE_GAME_DATA = {
            "INSERT INTO " + GameDataDao.TABLE_NAME + " VALUES "
                    + "(1," + GameDataDao.TYPE_TILE + "," + GameTile.TYPE_OBSTACLE + "," + R.drawable.tile_01 + ",1);",

            "INSERT INTO " + GameDataDao.TABLE_NAME + " VALUES "
                    + "(2," + GameDataDao.TYPE_TILE + "," + GameTile.TYPE_ROCK + "," + R.drawable.tile_02 + ",1);",

            "INSERT INTO " + GameDataDao.TABLE_NAME + " VALUES "
                    + "(3," + GameDataDao.TYPE_TILE + "," + GameTile.TYPE_CRATES + "," + R.drawable.tile_03 + ",1);",

            "INSERT INTO " + GameDataDao.TABLE_NAME + " VALUES "
                    + "(4," + GameDataDao.TYPE_PLAYER + ",1," + R.drawable.player_1 + ",1);",

            "INSERT INTO " + GameDataDao.TABLE_NAME + " VALUES "
                    + "(5," + GameDataDao.TYPE_PLAYER + ",2," + R.drawable.player_1 + ",1);",

            "INSERT INTO " + GameDataDao.TABLE_NAME + " VALUES "
                    + "(6," + GameDataDao.TYPE_BOMB + "," + Bomb.TYPE_NORMAL + "," + R.drawable.bomb_1 + ",1);",

    };

    private static final String[] POPULATE_TABLE_GAME_LEVEL_TILES = {
            "INSERT INTO " + GameLevelDataDao.TABLE_NAME + " VALUES "
                    + "(null," + GameConstants.MULTI_PLAYER_STAGE + ",1,1,\""
                    // 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17
        /* 1  */ + "01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 2  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 3  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 4  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 5  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 6  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 7  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 8  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 9  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 10 */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 11 */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
        /* 12 */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
		/* 13 */ + "01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01" + GameLevelDataDao.TILE_DATA_LINE_BREAK
                    + "\");"
    };
}
