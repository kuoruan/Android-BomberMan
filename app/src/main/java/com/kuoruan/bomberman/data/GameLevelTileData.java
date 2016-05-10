package com.kuoruan.bomberman.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

/**
 * Created by Liao on 2016/5/2 0002.
 */
public class GameLevelTileData extends GameDao {
    public static final String TABLE_NAME = "gameLevelTileData";

    public static final String STAGE = "stage";
    public static final String PLAYER_START_TILE_X = "playerStartTileX";
    public static final String PLAYER_START_TILE_Y = "playerStartTileY";
    public static final String TILE_DATA = "tileData";

    public static final int FIELD_ID_ID = 0;
    public static final int FIELD_ID_STAGE = 1;
    public static final int FIELD_ID_PLAYER_START_TILE_X = 2;
    public static final int FIELD_ID_PLAYER_START_TILE_Y = 3;
    public static final int FIELD_ID_TILE_DATA = 4;

    public static final String TILE_DATA_LINE_BREAK = "//";

    public GameLevelTileData(Context ctx) {
        super(ctx);
    }

    /**
     * Gets an array of game level data for a given stage and level.
     *
     * @param stage stage - The game stage.
     * @return ArrayList
     */
    public List<String> getGameLevelData(int stage) {

        List<String> levelData = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] from = {_ID, STAGE, PLAYER_START_TILE_X, PLAYER_START_TILE_Y, TILE_DATA};
        String where = STAGE + " = " + stage;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                levelData.add(cursor.getString(FIELD_ID_ID));
                levelData.add(cursor.getString(FIELD_ID_STAGE));
                levelData.add(cursor.getString(FIELD_ID_PLAYER_START_TILE_X));
                levelData.add(cursor.getString(FIELD_ID_PLAYER_START_TILE_Y));
                levelData.add(cursor.getString(FIELD_ID_TILE_DATA));
            }
            cursor.close();
        }

        db.close();
        return levelData;
    }

}
