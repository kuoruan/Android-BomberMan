package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.kuoruan.bomberman.util.SceneManager;

import java.util.List;

/**
 * Created by Liao on 2016/5/8 0008.
 */
public class BombFire extends GameTile {

    public static final int NORMAL = 1;

    public static final int TYPE_UP = 1;
    public static final int TYPE_DOWN = 2;
    public static final int TYPE_LEFT = 3;
    public static final int TYPE_RIGHT = 4;
    public static final int TYPE_VERTICAL = 5;
    public static final int TYPE_HORIZONTAL = 6;
    public static final int TYPE_CENTER = 7;

    public BombFire(List<Bitmap> frameBitmap) {
        super(frameBitmap, GameTile.TYPE_BOMB_FIRE);
        this.mWidth = SceneManager.tileWidth;
        this.mHeight = SceneManager.tileHeight;
    }
}
