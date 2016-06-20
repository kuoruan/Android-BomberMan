package com.kuoruan.bomberman.entity.data;

import java.util.Arrays;

/**
 * 游戏关卡相关数据
 */

public class GameLevelData {
    private int id;
    private int stage;
    private String LevelTiles;
    private int playerStartX;
    private int playerStartY;

    public GameLevelData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getLevelTiles() {
        return LevelTiles;
    }

    public void setLevelTiles(String levelTiles) {
        LevelTiles = levelTiles;
    }

    public int getPlayerStartX() {
        return playerStartX;
    }

    public void setPlayerStartX(int playerStartX) {
        this.playerStartX = playerStartX;
    }

    public int getPlayerStartY() {
        return playerStartY;
    }

    public void setPlayerStartY(int playerStartY) {
        this.playerStartY = playerStartY;
    }
}
