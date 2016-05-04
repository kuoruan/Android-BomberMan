package com.kuoruan.bomberman.entity;

import android.util.Log;

/**
 * Created by Window10 on 2016/5/3.
 */
public class Conflict {
    public static final int LEFT_UP = 1;
    public static final int LEFT_DOWN = 2;
    public static final int RIGHT_UP = 3;
    public static final int RIGHT_DOWN = 4;
    public static final int SOLVE_LENGTH = 15;
    private static final String TAG = "Conflict";
    private GameTile mCollisionTile;
    private boolean mSolvable;
    private int mNewX;
    private int mNewY;

    private int mDirection;

    public GameTile getCollisionTile() {
        return mCollisionTile;
    }

    public void setCollisionTile(GameTile collisionTile) {
        mCollisionTile = collisionTile;
    }

    public boolean isSolvable() {
        return mSolvable;
    }

    public void setSolvable(boolean solvable) {
        mSolvable = solvable;
    }

    public int getNewX() {
        return mNewX;
    }

    public void setNewX(int newX) {
        mNewX = newX;
    }

    public int getNewY() {
        return mNewY;
    }

    public void setNewY(int newY) {
        mNewY = newY;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    @Override
    public String toString() {
        return "Conflict{" +
                "mCollisionTile=" + mCollisionTile +
                ", mSolvable=" + mSolvable +
                ", mNewX=" + mNewX +
                ", mNewY=" + mNewY +
                ", mDirection=" + mDirection +
                '}';
    }

    public void solveConflict(GameTile gameTile, int x, int y, int width, int height) {
        Log.i(TAG, "Conflict: gameTileX = " + gameTile.getX() + " gameTileY = " + gameTile.getY() + "    X:" + x + " " +
                " Y:" + y);
        int tileX = gameTile.getX();
        int tileY = gameTile.getY();
        switch (mDirection) {
            case PlayerUnit.DIRECTION_LEFT:
            case PlayerUnit.DIRECTION_RIGHT:
                if (y > tileY) {
                    if (y + SOLVE_LENGTH > tileY + width) {
                        this.mSolvable = true;
                        this.mNewY = tileY + width;
                    } else {
                        this.mSolvable = false;
                    }
                } else {
                    if (y + width - SOLVE_LENGTH < tileY) {
                        this.mSolvable = true;
                        this.mNewY = tileY - width;
                    } else {
                        this.mSolvable = false;
                    }
                }
                break;
            case PlayerUnit.DIRECTION_UP:
            case PlayerUnit.DIRECTION_DOWN:
                if (x > tileX) {
                    if (x + SOLVE_LENGTH > tileX + width) {
                        this.mSolvable = true;
                        this.mNewX = tileX + width;
                    } else {
                        this.mSolvable = false;
                    }
                } else {
                    if (x + width - SOLVE_LENGTH < tileX) {
                        this.mSolvable = true;
                        this.mNewX = tileX - width;
                    } else {
                        this.mSolvable = false;
                    }
                }
                break;
        }
    }
}
