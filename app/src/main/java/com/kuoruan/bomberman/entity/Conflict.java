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
    public static final int SOVEL_LENGTH = 15;
    private static final String TAG = "Conflict";
    private GameTile mCollisionTile;
    private boolean mSovleable;
    private int mNewX;
    private int mNewY;

    private int mDirection;

    public GameTile getCollisionTile() {
        return mCollisionTile;
    }

    public void setCollisionTile(GameTile collisionTile) {
        mCollisionTile = collisionTile;
    }

    public boolean isSovleable() {
        return mSovleable;
    }

    public void setSovleable(boolean sovleable) {
        mSovleable = sovleable;
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
                "mDirection=" + mDirection +
                ", mNewY=" + mNewY +
                ", mNewX=" + mNewX +
                ", mSovleable=" + mSovleable +
                '}';
    }

    public void sovleConfict(GameTile gameTile, int x, int y, int width, int height) {
        Log.i(TAG, "Conflict: gameTileX = " + gameTile.getX() + " gameTileY = " + gameTile.getY()+"    X:"+x+"  Y:"+y);
        int tileX = gameTile.getX();
        int tileY = gameTile.getY();
        switch (mDirection){
            case PlayerUnit.DIRECTION_LEFT:
            case PlayerUnit.DIRECTION_RIGHT:
                if(y>tileY){
                    if(y+SOVEL_LENGTH>tileY+width){
                        this.mSovleable = true;
                        this.mNewY=tileY+width;
                    }else{
                        this.mSovleable = false;
                    }
                }else{
                    if(y+width-SOVEL_LENGTH<tileY){
                        this.mSovleable = true;
                        this.mNewY=tileY-width;
                    }else{
                        this.mSovleable = false;
                    }
                }
                break;
            case PlayerUnit.DIRECTION_UP:
            case PlayerUnit.DIRECTION_DOWN:
                if(x>tileX){
                    if(x+SOVEL_LENGTH>tileX+width){
                        this.mSovleable = true;
                        this.mNewX=tileX+width;
                    }else{
                        this.mSovleable = false;
                    }
                }else{
                    if(x+width-SOVEL_LENGTH<tileX){
                        this.mSovleable = true;
                        this.mNewX=tileX-width;
                    }else{
                        this.mSovleable = false;
                    }
                }
                break;
        }
    }
}
