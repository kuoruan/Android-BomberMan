package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Liao on 2016/5/3 0003.
 */
public class Animation {

    //是否循环播放
    public static boolean LOOP_PLAY = false;
    //动画播放间隙时间
    private static final int ANIM_TIME = 100;

    //上一帧播放时间
    private long mLastPlayTime = 0;
    //播放当前帧的ID
    private int mPlayID = 0;
    //动画frame数量
    private int mFrameCount = 0;
    //动画资源图片
    protected List<Bitmap> mFrameBitmap = null;
    //是否循环播放
    private boolean mIsLoop = LOOP_PLAY;
    //是否结束播放结束
    private boolean mIsEnd = false;

    private GameImage mBaseObj = null;

    public Animation(GameImage BaseObj) {
        this.mBaseObj = BaseObj;
    }

    public Animation(GameImage BaseObj, boolean isLoop) {
        this.mBaseObj = BaseObj;
        this.mIsLoop = isLoop;
    }

    public Animation(GameImage BaseObj, List<Bitmap> frameBitmap) {
        this.mBaseObj = BaseObj;
        this.mFrameBitmap = frameBitmap;
        this.mFrameCount = frameBitmap.size();
    }

    public Animation(GameImage BaseObj, List<Bitmap> frameBitmap, boolean isLoop) {
        this(BaseObj, frameBitmap);
        this.mIsLoop = isLoop;
    }

    public List<Bitmap> getFrameBitmap() {
        return mFrameBitmap;
    }

    public void setFrameBitmap(List<Bitmap> frameBitmap) {
        mFrameBitmap = frameBitmap;
        mFrameCount = frameBitmap.size();
    }


    public GameImage getBaseObj() {
        return mBaseObj;
    }

    public void setBaseObj(GameImage baseObj) {
        mBaseObj = baseObj;
    }

    public void doAnimation() {
        if (mFrameBitmap == null) {
            return;
        }
        //如果没有播放结束则继续播放
        if (!mIsEnd) {
            this.mBaseObj.setBitmap(mFrameBitmap.get(mPlayID));
            long time = System.currentTimeMillis();
            if (time - mLastPlayTime > ANIM_TIME) {
                mPlayID++;
                mLastPlayTime = time;
                if (mPlayID >= mFrameCount) {
                    //标志动画播放结束
                    mIsEnd = true;
                    if (mIsLoop) {
                        //设置循环播放
                        mIsEnd = false;
                        mPlayID = 0;
                    }
                }
            }
        }
    }

    public boolean isEnd() {
        return mIsEnd;
    }

    public void setLoop(boolean loop) {
        mIsLoop = loop;
    }
}
