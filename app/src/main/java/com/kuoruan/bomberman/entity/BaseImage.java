package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * 基本图片类，为所有游戏对象基类
 */

public class BaseImage {
    protected Bitmap mBitmap;
    protected int mWidth = 0;
    protected int mHeight = 0;
    protected Point mPoint = new Point();

    public BaseImage() {

    }

    public BaseImage(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mWidth = bitmap.getWidth();
        this.mHeight = bitmap.getHeight();
    }

    public BaseImage(Bitmap bitmap, Point point) {
        this(bitmap);
        this.mPoint = point;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.mBitmap = bitmap;
            this.mWidth = bitmap.getWidth();
            this.mHeight = bitmap.getHeight();
        }
    }

    public void setBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            this.mBitmap = bitmap;
            setWidthAndHeight(width, height);
        }
    }

    //设置需要的图片宽和高
    private void setWidthAndHeight(int width, int height) {
        if (mBitmap != null) {
            this.mWidth = (width > 0) ? width : mBitmap.getWidth();
            this.mHeight = (height > 0) ? height : ((width > 0) ? width : mBitmap.getHeight());
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getX() {
        return mPoint.x;
    }

    public void setX(int x) {
        mPoint.x = x;
    }

    public int getY() {
        return mPoint.y;
    }

    public void setY(int y) {
        mPoint.y = y;
    }

    public void setCenterX(int centerX) {
        mPoint.x = (centerX - (this.getWidth() / 2));
    }

    public int getCenterX() {
        return (mPoint.x + (this.getWidth() / 2));
    }

    public void setCenterY(int centerY) {
        mPoint.y = (centerY - (this.getHeight() / 2));
    }

    public int getCenterY() {
        return (mPoint.y + (this.getHeight() / 2));
    }

    public void setPoint(Point point) {
        mPoint.x = point.x;
        mPoint.y = point.y;
    }

    public Point getPoint() {
        return mPoint;
    }
}
