package com.kuoruan.bomberman.entity;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 地图单元对象
 */
public class GameTile extends GameImage {
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

    private int mType = TYPE_EMPTY;
    private boolean mVisible = true;
    private Rect mCollisionRect = null;

    public GameTile(){

    }

    public GameTile(Point point) {
        setPoint(point);
    }

    public GameTile(Context context, int drawable, Point point) {
        super(context, drawable);
        setPoint(point);
    }

    public GameTile(Context context, int drawable, Point point, int width, int height) {
        super(context, drawable, width, height);
        setPoint(point);
    }

    public boolean getCollision(float x, float y, int width, int height) {
        if (this.mCollisionRect == null) {
            this.mCollisionRect = new Rect((int) x, (int) y, ((int) x + width), ((int) y + height));
        } else {
            this.mCollisionRect.set((int) x, (int) y, ((int) x + width), ((int) y + height));
        }

        return (this.mCollisionRect.intersects(this.mX, this.mY, (this.mX + getWidth()), (this.mY + getHeight())));
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
}
