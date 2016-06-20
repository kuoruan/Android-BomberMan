package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * 游戏控制按钮
 */
public class GameUi extends BaseImage {

    //按钮状态
    public static final int STATE_NORMAL = 1;
    public static final int STATE_INACTIVE = 2;
    public static final int STATE_ACTIVE = 3;
    public static final int STATE_READY = 4;

    private int mState = STATE_NORMAL;

    public GameUi(Bitmap bitmap){
        super(bitmap);
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public void setStateReady() {
        this.mState = STATE_READY;
    }

    public boolean isStateNormal() {
        return (this.mState == STATE_NORMAL);
    }

    //判断点是否在当前对象中
    public boolean getImpact(int x, int y) {
        if ((x >= mPoint.x) && (x <= (mPoint.x + mWidth))) {
            if ((y >= mPoint.y) && (y <= (mPoint.y + mWidth))) {
                return true;
            }
        }

        return false;
    }

}
