package com.kuoruan.bomberman.net;

import com.kuoruan.bomberman.entity.PlayerUnit;

import java.net.Socket;

/**
 * Created by Window10 on 2016/5/3.
 */
public class NetPlayer {
    private long id;

    private PlayerUnit mPlayerUnit;





    public PlayerUnit getPlayerUnit() {
        return mPlayerUnit;
    }

    public void setPlayerUnit(PlayerUnit playerUnit) {
        mPlayerUnit = playerUnit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
