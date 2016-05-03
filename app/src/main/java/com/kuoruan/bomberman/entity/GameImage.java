package com.kuoruan.bomberman.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * 基础图片对象，包含图片宽高和在屏幕上的坐标位置
 */
public class GameImage {

    protected Bitmap mImg;
    protected int mX = 0;
    protected int mY = 0;
    protected int mWidth = 0;
    protected int mHeight = 0;

    public GameImage(Context context) {

    }

    public GameImage(Context context, int drawable) {
        setDrawable(context, drawable, 0, 0);
    }

    public GameImage(Context context, int drawable, int width, int height) {
        setDrawable(context, drawable, width, height);
    }

    public void setDrawable(Context context, int drawable, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        this.mImg = BitmapFactory.decodeResource(context.getResources(), drawable);
        setWidthAndHeight(width, height);
    }

    //设置需要的图片宽和高
    private void setWidthAndHeight(int width, int height) {
        if (mImg != null) {
            this.mWidth = (width > 0) ? width : this.mImg.getWidth();
            this.mHeight = (height > 0) ? height : ((width > 0) ? width : this.mImg.getHeight());
        }
    }

    public Bitmap getBitmap() {
        return mImg;
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.mImg = bitmap;
            this.mWidth = bitmap.getWidth();
            this.mHeight = bitmap.getHeight();
        }
    }

    public void setBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            this.mImg = bitmap;
            setWidthAndHeight(width, height);
        }
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        this.mX = x;
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        this.mY = y;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public void setCenterX(int centerX) {
        this.mX = (centerX - (this.getWidth() / 2));
    }

    public int getCenterX() {
        return (mX + (this.getWidth() / 2));
    }

    public void setCenterY(int centerY) {
        this.mY = (centerY - (this.getHeight() / 2));
    }

    public int getCenterY() {
        return (mY + (this.getHeight() / 2));
    }

    public void setPoint(Point point) {
        this.mX = point.x;
        this.mY = point.y;
    }
}
