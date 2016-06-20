package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import java.util.List;

/**
 * 动态图片类，为所有动态游戏对象基类
 */
public class DynamicImage extends BaseImage {

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

    public DynamicImage() {
    }

    public DynamicImage(List<Bitmap> frameBitmap, Point point) {
        super(frameBitmap.get(0), point);
        this.mFrameBitmap = frameBitmap;
        this.mFrameCount = frameBitmap.size();
    }

    public DynamicImage(List<Bitmap> frameBitmap, boolean isLoop, Point point) {
        this(frameBitmap, point);
        this.mIsLoop = isLoop;
    }

    public DynamicImage(Bitmap baseImage, List<Bitmap> frameBitmap, Point point) {
        super(baseImage, point);
        setFrameBitmap(frameBitmap);
    }

    public DynamicImage(Bitmap baseImage, List<Bitmap> frameBitmap, boolean isLoop, Point point) {
        this(baseImage, frameBitmap, point);
        this.mIsLoop = isLoop;
    }

    public List<Bitmap> getFrameBitmap() {
        return mFrameBitmap;
    }

    public void setFrameBitmap(List<Bitmap> frameBitmap) {
        if (frameBitmap != null) {
            mFrameBitmap = frameBitmap;
            mFrameCount = frameBitmap.size();
        }
    }

    public void doAnimation() {
        if (mFrameBitmap == null) {
            return;
        }

        //如果没有播放结束则继续播放
        if (!mIsEnd) {
            //设置当前游戏图片
            mBitmap = mFrameBitmap.get(mPlayID);
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

    public void setLoop(boolean isLoop) {
        mIsLoop = isLoop;
    }
}
