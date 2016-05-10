package com.kuoruan.bomberman.entity;

import android.graphics.Point;

/**
 * Created by Liao on 2016/5/8 0008.
 */
public class Fire extends GameTile {

    public static final int TYPE_UP = 1;
    public static final int TYPE_DOWN = 2;
    public static final int TYPE_LEFT = 3;
    public static final int TYPE_RIGHT = 4;
    public static final int TYPE_VERTICAL = 5;
    public static final int TYPE_HORIZONTAL = 6;
    public static final int TYPE_CENTER = 7;

    //默认火焰持续时间
    private static final int DEFAULT_DURATION = 300;

    //持续时间
    private int duration = DEFAULT_DURATION;

    public Fire(Point point) {
        super(point);
    }

    public Fire(int x, int y) {
        mX = x;
        mY = y;
    }
}
