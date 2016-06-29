package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import com.kuoruan.bomberman.util.SceneManager;

import java.util.List;

/**
 * 地图单元对象
 */
public class GameTile extends DynamicImage {
    //空白
    public static final int TYPE_EMPTY = 0;
    //外岩
    public static final int TYPE_OBSTACLE = 1;
    //岩石
    public static final int TYPE_ROCK = 2;
    //箱子
    public static final int TYPE_CRATES = 3;
    //炸弹
    public static final int TYPE_BOMB = 4;
    //炸弹火焰
    public static final int TYPE_BOMB_FIRE = 5;
    //道具
    public static final int TYPE_GIFT = 6;

    private int mType = TYPE_EMPTY;
    private boolean mVisible = true;
    private Rect mCollisionRect = null;
    private boolean mIsDynamic = false;

    public GameTile() {
    }

    public GameTile(List<Bitmap> frameBitmap, int type) {
        this.mType = type;
        setFrameBitmap(frameBitmap);
    }

    public GameTile(Bitmap bitmap, Point point, int type) {
        this.mType = type;
        setBitmap(bitmap);
        setPoint(point);
    }

    public GameTile(List<Bitmap> frameBitmap, Point point, int type) {
        super(frameBitmap, point);
        this.mIsDynamic = true;
        this.mType = type;
    }

    public GameTile(Bitmap baseBitmap, List<Bitmap> frameBitmap, Point point, int type) {
        super(baseBitmap, frameBitmap, point);
        this.mIsDynamic = true;
        this.mType = type;
    }

    public GameTile(List<Bitmap> frameBitmap, boolean isLoop, Point point, int type) {
        super(frameBitmap, isLoop, point);
        this.mIsDynamic = true;
        this.mType = type;
    }

    public GameTile(Bitmap baseBitmap, List<Bitmap> frameBitmap, boolean isLoop, Point point, int type) {
        super(baseBitmap, frameBitmap, isLoop, point);
        this.mIsDynamic = true;
        this.mType = type;
    }

    public boolean isCollision(float x, float y, int width, int height) {
        if (this.mCollisionRect == null) {
            this.mCollisionRect = new Rect((int) x, (int) y, ((int) x + width), ((int) y + height));
        } else {
            this.mCollisionRect.set((int) x, (int) y, ((int) x + width), ((int) y + height));
        }

        return (this.mCollisionRect.intersects(mPoint.x, mPoint.y, (mPoint.x + getWidth()), (mPoint.y + getHeight())));
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
    }

    public boolean isDynamic() {
        return mIsDynamic;
    }

    public void setDynamic(boolean dynamic) {
        mIsDynamic = dynamic;
    }

    /**
     * 是否是物理冲撞块
     *
     * @return
     */
    public boolean isCollisionTile() {
        return ((this.mType != GameTile.TYPE_EMPTY) && this.mVisible);
    }

    public boolean isBlockerTile() {
        return (this.mType != GameTile.TYPE_EMPTY);
    }

    public void setMapPoint(int x, int y) {
        mPoint.x = x * SceneManager.tileWidth;
        mPoint.y = y * SceneManager.tileHeight;
    }
}
