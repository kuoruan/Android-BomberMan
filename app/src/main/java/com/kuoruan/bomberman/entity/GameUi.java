package com.kuoruan.bomberman.entity;

import android.content.Context;

/**
 * 游戏控制按钮
 */
public class GameUi extends GameImage {

    //按钮状态
    public static final int STATE_NORMAL = 1;
    public static final int STATE_INACTIVE = 2;
    public static final int STATE_ACTIVE = 3;
    public static final int STATE_READY = 4;

    private int mState = STATE_NORMAL;
    private boolean mVisible = true;

    private Context mContext = null;

    public GameUi(Context context, int drawable) {
        super(context, drawable);
        this.mContext = context;
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

    public boolean isVisible() {
        return mVisible;
    }

    public void setmVisible(boolean visible) {
        this.mVisible = visible;
    }

    public boolean isStateNormal() {
        return (this.mState == STATE_NORMAL);
    }

    //判断点是否在当前对象中
    public boolean getImpact(int x, int y) {
        if ((x >= mX) && (x <= (mX + this.getWidth()))) {
            if ((y >= mY) && (y <= (mY + this.getHeight()))) {
                return true;
            }
        }

        return false;
    }

}
