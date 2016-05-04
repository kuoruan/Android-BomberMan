package com.kuoruan.bomberman.entity;

import android.content.Context;

/**
 * 玩家角色
 */
public class PlayerUnit extends GameImage {
    //角色状态
    public static final int STATE_MOVING = 1;
    public static final int STATE_STOP = 2;
    //默认移动速度
    public static final int DEFAULT_SPEED = 3;
    //默认炸弹数量
    public static final int DEFAULT_BOMBS = 2;
    //角色移动方向
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_RIGHT = 4;

    //当前玩家数量
    private static int count = 1;

    private int id = 0;
    //角色状态
    private int mState = 0;
    //角色移动速度
    private int mSpeed = DEFAULT_SPEED;
    //炸弹数量
    private int mBombs = DEFAULT_BOMBS;
    //当前分数
    private int mScore = 0;
    //水平方向
    private int mPlayerVerticalDirection = 0;
    //竖直方向
    private int mPlayerHorizontalDirection = 0;



    public PlayerUnit(Context context, int drawable) {
        super(context, drawable);
        this.id = count;
        count++;
    }

    public static int getCount() {
        return count;
    }

    public static void resetCount() {
        count = 1;
    }

    public int getId() {
        return this.id;
    }

    public void addSpeed() {
        this.mSpeed++;
    }

    public int getSpeed() {
        return this.mSpeed;
    }

    public void addBomb() {
        this.mBombs++;
    }

    public void addScore(int score) {
        this.mScore += score;
    }

    public int getScore() {
        return this.mScore;
    }

    public int getPlayerVerticalDirection() {
        return mPlayerVerticalDirection;
    }

    public void setPlayerVerticalDirection(int playerVerticalDirection) {
        this.mPlayerVerticalDirection = playerVerticalDirection;
    }

    public int getPlayerHorizontalDirection() {
        return mPlayerHorizontalDirection;
    }

    public void setPlayerHorizontalDirection(int playerHorizontalDirection) {
        this.mPlayerHorizontalDirection = playerHorizontalDirection;
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

}
