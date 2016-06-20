package com.kuoruan.bomberman.entity.data;

/**
 * 游戏地图单元对象，获取自数据库
 */

public class GameData {

    private int id;
    private int mType;
    private int mSubType;
    private int mDrawable;
    private boolean mIsVisible = true;

    public GameData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getSubType() {
        return mSubType;
    }

    public void setSubType(int subType) {
        mSubType = subType;
    }

    public int getDrawable() {
        return mDrawable;
    }

    public void setDrawable(int drawable) {
        mDrawable = drawable;
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    public void setVisible(boolean visible) {
        mIsVisible = visible;
    }
}
