package com.kuoruan.bomberman.entity;

import android.graphics.Point;

import com.kuoruan.bomberman.util.BombManager;

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
    private long pid;
    private int mExplosionTime = DEFAULT_EXPLOSION_TIME;
    private boolean mExplosion = false;
    private int mFireLength = DEFAULT_FIRE_LENGTH;

    public Bomb(Point point, long pid) {
        super(point);
        this.id = System.currentTimeMillis();
        this.pid = pid;
        setType(TYPE_BOMB);
        new BombThread().start();
    }

    public Bomb(Point point, long id, long pid) {
        super(point);
        this.id = id;
        this.pid = pid;
        setType(TYPE_BOMB);
        new BombThread().start();
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
        BombManager.explosionBomb(Bomb.this);
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

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
}
