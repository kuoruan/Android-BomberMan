package com.kuoruan.bomberman.entity;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 玩家角色
 */
public class Player extends GameImage {
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

    private long id = 0;
    //角色状态
    private int mState = 0;
    //角色移动速度
    private int mSpeed = DEFAULT_SPEED;
    //当前分数
    private int mScore = 0;
    //水平方向
    private int mPlayerVerticalDirection = 0;
    //竖直方向
    private int mPlayerHorizontalDirection = 0;

    private int mTemplateId = 0;

    public Player(Bitmap bitmap, int templateId) {
        super(bitmap);
        this.mTemplateId = templateId;
    }

    public Player(Context context, int drawable) {
        super(context, drawable);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isAlive() {
        return (this.mState != STATE_DIE);
    }

    public int getTemplateId() {
        return mTemplateId;
    }

    public void setTemplateId(int templateId) {
        mTemplateId = templateId;
    }
}
