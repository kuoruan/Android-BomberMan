package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.List;

/**
 * Created by Liao on 2016/5/3 0003.
 */
public class Bomb extends GameTile {

    /**
     * 炸弹类型
     */
    public static final int TYPE_NORMAL = 1;
    /**
     * 默认爆炸时间,毫秒
     */
    public static final int DEFAULT_EXPLOSION_TIME = 3000;
    /**
     * 默认火焰长度
     */
    public static final int DEFAULT_FIRE_LENGTH = 3;

    private long id;
    private int pid;
    private int mExplosionTime = DEFAULT_EXPLOSION_TIME;
    private boolean mExplosion = false;
    private int mFireLength = DEFAULT_FIRE_LENGTH;

    public Bomb(List<Bitmap> frameBitmap, boolean isLoop, Point point, int pid) {
        super(frameBitmap, isLoop, point, GameTile.TYPE_BOMB);
        this.id = System.currentTimeMillis();
        this.pid = pid;
        new BombThread().start();
    }

    public Bomb(List<Bitmap> frameBitmap, boolean isLoop, Point point, long id, int pid) {
        this(frameBitmap, isLoop, point, pid);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    class BombThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(mExplosionTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!mExplosion) {
                setExplosion(true);
            }
        }
    }

    public int getExplosionTime() {
        return mExplosionTime;
    }

    public void setExplosionTime(int explosionTime) {
        this.mExplosionTime = explosionTime;
    }

    public void setExplosion(boolean explosion) {
        mExplosion = explosion;
    }

    public boolean isExplosion() {
        return mExplosion;
    }

    public int getFireLength() {
        return mFireLength;
    }

    public void setFireLength(int fireLength) {
        mFireLength = fireLength;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
