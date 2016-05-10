package com.kuoruan.bomberman.net;

/**
 * Created by Window10 on 2016/5/3.
 */
public class NetProtocol {

    public static final String CODE = "code";
    public static final String ID = "id";
    public static final String POINTX = "pointX";
    public static final String POINTY = "pointY";
    public static final String PID = "pid";
    /**
     * {"code":1,"id":"1","pointX":"12","pointY":"13"}
     */
    public static final int ADD_PLAYER = 1;
    /**
     * {"code":2,"id":"1","pointX":"12","pointY":"13"}
     */
    public static final int PLAYER_MOVE = 2;
    /**
     * {"code":3, "pid":"1",id:"1"}
     */
    public static final int BOMB_EXPLOSION = 3;
}
