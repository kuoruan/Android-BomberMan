package com.kuoruan.bomberman.entity;

/**
 * 角色和地图冲突对象
 */
public class Collision {
    private static final String TAG = "Conflict";
    public static final int SOLVE_LENGTH = 15;
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

    public void solveCollision() {
        int tileX = mCollisionTile.getX();
        int tileY = mCollisionTile.getY();
        int width = mCollisionTile.getWidth();
        int height = mCollisionTile.getHeight();

        switch (mDirection) {
            case Player.DIRECTION_LEFT:
            case Player.DIRECTION_RIGHT:
                if (mNewY > tileY) {
                    if (mNewY + SOLVE_LENGTH > tileY + height) {
                        this.mSolvable = true;
                        this.mNewY = tileY + height;
                    } else {
                        this.mSolvable = false;
                    }
                } else {
                    if (mNewY + height - SOLVE_LENGTH < tileY) {
                        this.mSolvable = true;
                        this.mNewY = tileY - height;
                    } else {
                        this.mSolvable = false;
                    }
                }
                break;
            case Player.DIRECTION_UP:
            case Player.DIRECTION_DOWN:
                if (mNewX > tileX) {
                    if (mNewX + SOLVE_LENGTH > tileX + width) {
                        this.mSolvable = true;
                        this.mNewX = tileX + width;
                    } else {
                        this.mSolvable = false;
                    }
                } else {
                    if (mNewX + width - SOLVE_LENGTH < tileX) {
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
