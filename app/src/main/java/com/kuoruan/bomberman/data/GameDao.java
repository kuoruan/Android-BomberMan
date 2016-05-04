package com.kuoruan.bomberman.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kuoruan.bomberman.R;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.view.GameView;

import static android.provider.BaseColumns._ID;

/**
 * Created by Liao on 2016/5/2 0002.
 */
public class GameDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "bomberman.db";
    private static final int DB_VERSION = 1;

    public GameDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GAME_DATA);
        db.execSQL(CREATE_TABLE_GAME_LEVEL_TILES);

        for (String query : POPULATE_TABLE_GAME_TILES) {
            db.execSQL(query);
        }

        for (String query : POPULATE_TABLE_GAME_LEVEL_TILES) {
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GameData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GameLevelTileData.TABLE_NAME);

        onCreate(db);
    }

    private static final String CREATE_TABLE_GAME_DATA = "CREATE TABLE " + GameData.TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY, "
            + GameData.TYPE + " INTEGER DEFAULT 1,"
            + GameData.SUB_TYPE + " INTEGER DEFAULT 0,"
            + GameData.DRAWABLE + " INTEGER DEFAULT 0,"
            + GameData.VISIBLE + " INTEGER DEFAULT 1"
            + ");";

    private static final String CREATE_TABLE_GAME_LEVEL_TILES = "CREATE TABLE " + GameLevelTileData.TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + GameLevelTileData.STAGE + " INTEGER DEFAULT 0,"
            + GameLevelTileData.PLAYER_START_TILE_X + " INTEGER DEFAULT 0,"
            + GameLevelTileData.PLAYER_START_TILE_Y + " INTEGER DEFAULT 0,"
            + GameLevelTileData.TILE_DATA + " TEXT NOT NULL"
            + ");";

    private static final String[] POPULATE_TABLE_GAME_TILES = {
            "INSERT INTO " + GameData.TABLE_NAME + " VALUES "
                    + "(1," + GameData.TYPE_TILE + "," + GameTile.TYPE_OBSTACLE + "," + R.drawable.tile_01 + ",1);",

            "INSERT INTO " + GameData.TABLE_NAME + " VALUES "
                    + "(2," + GameData.TYPE_TILE + "," + GameTile.TYPE_ROCK + "," + R.drawable.tile_02 + ",1);",

            "INSERT INTO " + GameData.TABLE_NAME + " VALUES "
                    + "(3," + GameData.TYPE_TILE + "," + GameTile.TYPE_CRATES + "," + R.drawable.tile_03 + ",1);",

            "INSERT INTO " + GameData.TABLE_NAME + " VALUES "
                    + "(4," + GameData.TYPE_PLAYER + ",0," + R.drawable.player_unit + ",1);",

    };

    private static final String[] POPULATE_TABLE_GAME_LEVEL_TILES = {
            "INSERT INTO " + GameLevelTileData.TABLE_NAME + " VALUES "
                    + "(null," + GameView.MULTI_PLAYER_STAGE + ",1,1,\""
                  // 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17
        /* 1  */ + "01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
        /* 2  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
        /* 3  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
        /* 4  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
        /* 5  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 6  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 7  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 8  */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 9  */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 10 */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 11 */ + "01,00,02,00,02,00,02,00,02,00,02,00,02,00,02,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 12 */ + "01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
		/* 13 */ + "01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01,01" + GameLevelTileData.TILE_DATA_LINE_BREAK
                    + "\");"
    };
}
