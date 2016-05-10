package com.kuoruan.bomberman.entity;

/**
 * Created by Liao on 2016/5/9 0009.
 */
public class Scene {
    private int mSceneXMax = 0;
    private int mSceneYMax = 0;

    private int mTileWidth = 0;
    private int mTileHeight = 0;

    public Scene(int sceneXMax, int sceneYMax, int tileWidth, int tileHeight) {
        mSceneXMax = sceneXMax;
        mSceneYMax = sceneYMax;
        mTileWidth = tileWidth;
        mTileHeight = tileHeight;
    }

    public int getSceneXMax() {
        return mSceneXMax;
    }

    public int getSceneYMax() {
        return mSceneYMax;
    }

    public int getTileWidth() {
        return mTileWidth;
    }

    public int getTileHeight() {
        return mTileHeight;
    }
}
