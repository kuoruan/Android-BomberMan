package com.kuoruan.bomberman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.kuoruan.bomberman.data.GameData;
import com.kuoruan.bomberman.data.GameLevelTileData;
import com.kuoruan.bomberman.entity.Animation;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Liao on 2016/5/8 0008.
 */
public class SceneManager {

    private static Map<Integer, List<Integer>> mGameTileData = null;
    private static List<GameTile> mGameTiles = new ArrayList<>();
    private static List<Animation> mDynamicTiles = new ArrayList<>();
    private static Scene mScene = null;

    /**
     * 处理地图数据
     *
     * @param context
     * @param levelTileData
     */
    public static Scene parseGameTileData(Context context, String levelTileData) {
        // Clear any existing loaded game tiles.
        mGameTiles.clear();

        int mTileWidth = 0;
        int mTileHeight = 0;
        int mSceneXMax = 0;
        int mSceneYMax = 0;

        // Split level tile data by line.
        String[] tileLines = levelTileData.split(GameLevelTileData.TILE_DATA_LINE_BREAK);

        Bitmap bitmap = null;
        Point tilePoint = new Point(0, 0);
        int tileX = 0;
        int tileY = 0;

        // Loop through each line of the level tile data.
        for (String tileLine : tileLines) {
            tileX = 0;

            // Split tile data line by tile delimiter, producing an array of tile IDs.
            String[] tiles = tileLine.split(",");

            // Loop through the tile IDs, creating a new GameTile instance for each one.
            for (String tile : tiles) {
                // Get tile definition for the current tile ID.
                List<Integer> tileData = mGameTileData.get(Integer.parseInt(tile));

                // Check for valid tile data.
                if ((tileData != null) && (tileData.size() > 0) && (tileData.get(GameData.FIELD_ID_DRAWABLE) > 0)) {
                    // Set tile position.
                    tilePoint.x = tileX;
                    tilePoint.y = tileY;

                    GameTile gameTile = new GameTile(tilePoint);

                    bitmap = BitmapManager.setAndGetBitmap(context, tileData.get(GameData.FIELD_ID_DRAWABLE));
                    gameTile.setBitmap(bitmap);

                    // Set tile type.
                    gameTile.setType(tileData.get(GameData.FIELD_ID_TYPE));

                    // Set tile visibility.
                    if (tileData.get(GameData.FIELD_ID_VISIBLE) == 0) {
                        gameTile.setVisible(false);
                    }

                    // If undefined, set global tile width / height values.
                    if (mTileWidth == 0) {
                        mTileWidth = gameTile.getWidth();
                    }
                    if (mTileHeight == 0) {
                        mTileHeight = gameTile.getHeight();
                    }

                    if (mSceneXMax == 0 && tiles.length > 0) {
                        mSceneXMax = tiles.length * mTileWidth;
                    }
                    if (mSceneYMax == 0 && tileLines.length > 0) {
                        mSceneYMax = tileLines.length * mTileWidth;
                    }

                    // Add new game tile to loaded game tiles.
                    mGameTiles.add(gameTile);
                }

                // Increment next tile X (horizontal) position by tile width.
                tileX += mTileWidth;
            }

            // Increment next tile Y (vertical) position by tile width.
            tileY += mTileHeight;
        }

        Scene scene = new Scene(mSceneXMax, mSceneYMax, mTileWidth, mTileHeight);
        SceneManager.mScene = scene;
        return scene;
    }

    public static void setGameTileData(Map<Integer, List<Integer>> mGameTileData) {
        SceneManager.mGameTileData = mGameTileData;
    }

    public static List<GameTile> getGameTiles() {
        return mGameTiles;
    }

    public static Scene getScene() {
        return mScene;
    }

    public static void addDynamicTiles(List<Animation> gameTiles) {
        mDynamicTiles.addAll(gameTiles);
    }

    public static GameTile getGameTile(int x, int y) {
        for (GameTile gameTile : mGameTiles) {
            if (gameTile.getX() == x && gameTile.getY() == y) {
                return gameTile;
            }
        }

        BombManager.checkBomb(x, y);
        PlayerManager.checkPlayer(x, y);
        return null;
    }

    public static List<Animation> getDynamicTiles() {
        return mDynamicTiles;
    }
}
