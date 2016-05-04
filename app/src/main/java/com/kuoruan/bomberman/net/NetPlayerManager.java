package com.kuoruan.bomberman.net;

import android.content.Context;
import android.graphics.Point;

import com.kuoruan.bomberman.data.GameData;
import com.kuoruan.bomberman.data.GameLevelTileData;
import com.kuoruan.bomberman.entity.PlayerUnit;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Window10 on 2016/5/3.
 */
public class NetPlayerManager {
    private static List<NetPlayer> playerList = new ArrayList<>();
    private static final Point[] positions = {new Point(1,1),new Point(13,13),new Point(1,13),new Point(13,1)};
    public static List<NetPlayer> getNetPlayerList(){
        return playerList;
    }
    public static int getPlayerNumber(){
        return  playerList.size();
    }
    public static NetPlayer my = null;

    /**
     * 添加一个新的网络玩家
     * @param context
     * @return
     */
    public static NetPlayer addNetPlayer(Context context,long id,int x,int y){

        GameData mGameData = new GameData(context);
        GameLevelTileData  mGameLevelTileData = new GameLevelTileData(context);
        HashMap<Integer, ArrayList<Integer>> mGameTileTemplates = mGameData.getGameTileData();
        ArrayList<ArrayList<Integer>> mPlayerUnitTemplates = mGameData.getPlayerUnitData();
        PlayerUnit playerUnit = new PlayerUnit(context, mPlayerUnitTemplates.get(0).get(GameData.FIELD_ID_DRAWABLE));
        playerUnit.setX(x);
        playerUnit.setY(y);
        NetPlayer netPlayer = new NetPlayer();
        netPlayer.setPlayerUnit(playerUnit);
        netPlayer.setId(id);
        playerList.add(netPlayer);
        return netPlayer;
    }
    public static void putNetPlayer(NetPlayer netPlayer){
        playerList.add(netPlayer);
    }
    public static NetPlayer createNetPlayer(Context context){
        GameData mGameData = new GameData(context);
        GameLevelTileData  mGameLevelTileData = new GameLevelTileData(context);
        HashMap<Integer, ArrayList<Integer>> mGameTileTemplates = mGameData.getGameTileData();
        ArrayList<ArrayList<Integer>> mPlayerUnitTemplates = mGameData.getPlayerUnitData();
        PlayerUnit playerUnit = new PlayerUnit(context, mPlayerUnitTemplates.get(0).get(GameData.FIELD_ID_DRAWABLE));
        Random random = new Random();
        int i = random.nextInt(4);
        Point point = positions[0];
        playerUnit.setX(point.x* playerUnit.getWidth());
        playerUnit.setY(point.y* playerUnit.getHeight());
        NetPlayer netPlayer = new NetPlayer();
        netPlayer.setPlayerUnit(playerUnit);
        netPlayer.setId(System.currentTimeMillis());
        playerList.add(netPlayer);
        return netPlayer;
    }
    public static  boolean isHaveNetPlayer(long id){
        boolean flag = false;
        for(NetPlayer netPlayer:playerList){
            if(netPlayer.getId()==id){
                flag = true;
                break;
            }
        }
        return flag;
    }
    public static void setMyPlayer(NetPlayer netPlayer){
        my = netPlayer;
    }
    public static NetPlayer getMyPlayer(){
        return my;
    }
}
