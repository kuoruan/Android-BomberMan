package com.kuoruan.bomberman.entity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import java.util.List;
import java.util.Map;

/**
 * 玩家角色
 */
public class Player extends DynamicImage {

    private static final String TAG = "Player";

    //角色状态
    public static final int STATE_MOVING = 1;
    public static final int STATE_STOP = 2;
    public static final int STATE_DIE = 3;
    //默认移动速度
    public static final int DEFAULT_SPEED = 3;
    //角色移动方向
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_RIGHT = 4;
    //角色死亡
    public static final int PLAYER_DIE = 5;

    private int id = 0;
    //角色状态
    private int mState = 0;
    //角色移动速度
    private int mSpeed = DEFAULT_SPEED;
    //当前分数
    private int mScore = 0;
    //水平方向
    private int mDirection = 0;
    //上次移动方向
    private int mPreDirection = 0;
    //各个方向的图片
    private Map<Integer, List<Bitmap>> mFrameBitmaps;

    public Player(Bitmap bitmap, Map<Integer, List<Bitmap>> frameBitmaps, Point point) {
        super(bitmap, null, true, point);
        this.mFrameBitmaps = frameBitmaps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addSpeed() {
        this.mSpeed++;
    }

    public int getSpeed() {
        return this.mSpeed;
    }

    public void addScore(int score) {
        this.mScore += score;
    }

    public int getScore() {
        return this.mScore;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        this.mDirection = direction;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public boolean isMoving() {
        return (this.mState == STATE_MOVING);
    }

    public boolean isAlive() {
        return (this.mState != STATE_DIE);
    }

    @Override
    public void doAnimation() {
        if (mDirection != 0 && mDirection != mPreDirection) {
            setFrameBitmap(mFrameBitmaps.get(mDirection));
            mPreDirection = mDirection;
        }

        if (mState == STATE_DIE) {
            setFrameBitmap(mFrameBitmaps.get(PLAYER_DIE));
            setLoop(false);
        }

        super.doAnimation();
    }

    //获取左上角的标准位置
    public Point getStandardPoint() {
        Point point = getStandardMapPoint(); //获取标准点
        point.x *= mWidth; //乘以宽高
        point.y *= mHeight;
        return point;
    }

    //获取当前玩家的标准二维点
    public Point getStandardMapPoint() {
        int nowX = getX();
        int nowY = getY();

        Point mapPoint = new Point();

        if (nowX != 0) {
            mapPoint.x = (int) ((float) nowX / mWidth + .5);

        }
        if (nowY != 0) {
            mapPoint.y = (int) ((float) nowY / mHeight + .5);
        }

        return mapPoint;
    }
}
