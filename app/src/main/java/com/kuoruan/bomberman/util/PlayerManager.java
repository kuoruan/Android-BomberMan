package com.kuoruan.bomberman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import com.kuoruan.bomberman.data.GameData;
import com.kuoruan.bomberman.entity.Animation;
import com.kuoruan.bomberman.entity.Player;
import com.kuoruan.bomberman.net.UdpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Window10 on 2016/5/3.
 */
public class PlayerManager {

    private static Animation mDynamicPlayer = null;
    private static Map<Long, Animation> mDynamicPlayerMap = new HashMap<>();
    private static final Point[] positions = {new Point(1, 1), new Point(15, 11), new Point(1, 11), new Point(15, 1)};
    private static Map<Integer, Map<Integer, List<Bitmap>>> mPlayerTemplates = new HashMap<>();
    private static List<List<Integer>> mPlayerData = null;

    /**
     * 添加一个新的玩家
     */
    public static Animation addPlayer(Context context, long id, int x, int y) {
        int templateId = getPlayerCount();
        Bitmap bitmap = getFirstBitmap(context, templateId);
        Player player = new Player(bitmap, templateId);
        player.setX(x);
        player.setY(y);
        player.setId(id);
        Animation dynamicPlayer = new Animation(player);
        mDynamicPlayerMap.put(id, dynamicPlayer);
        return dynamicPlayer;
    }

    public static Bitmap getFirstBitmap(Context context, int id) {
        Map<Integer, List<Bitmap>> playerTemplate = setAndGetPlayerTemplates(context, id);
        return playerTemplate.get(Player.DIRECTION_DOWN).get(0);
    }

    /**
     * 准备玩家图片模板
     *
     * @param context
     * @param id
     */
    public static Map<Integer, List<Bitmap>> setAndGetPlayerTemplates(Context context, int id) {
        if (!mPlayerTemplates.containsKey(id)) {
            List<Integer> data = mPlayerData.get(id);

            int drawable = data.get(GameData.FIELD_ID_DRAWABLE);
            Bitmap bitmap = BitmapManager.setAndGetBitmap(context, drawable);
            int width = bitmap.getWidth() / 3;
            int height = bitmap.getHeight() / 7;

            Map<Integer, List<Bitmap>> map = new HashMap<>();
            for (int y = 0; y < 4; y++) {
                List<Bitmap> list = new ArrayList<>();
                for (int x = 0; x < 3; x++) {
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, x * width, y * height, width, height);
                    list.add(newBitmap);
                }

                map.put(y + 1, list);
            }

            List<Bitmap> dieBitmaps = new ArrayList<>();
            for (int y = 4; y < 7; y++) {
                for (int x = 0; x < 3; x++) {
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, x * width, y * height, width, height);
                    dieBitmaps.add(newBitmap);
                }
            }

            map.put(Player.PLAYER_DIE, dieBitmaps);

            mPlayerTemplates.put(id, map);
        }

        return mPlayerTemplates.get(id);
    }

    /**
     * 初始化玩家
     *
     * @param context
     * @return
     */
    public static Animation createDynamicPlayer(Context context) {
        Player player = new Player(getFirstBitmap(context, 0), 0);
        Random random = new Random();
        int i = random.nextInt(4);
        long id = System.currentTimeMillis();
        Point point = positions[i];
        player.setX(point.x * player.getWidth());
        player.setY(point.y * player.getHeight());
        player.setId(id);
        Animation dynamicPlayer = new Animation(player, true);
        mDynamicPlayerMap.put(id, dynamicPlayer);
        mDynamicPlayer = dynamicPlayer;
        return dynamicPlayer;
    }

    public static Map<Long, Animation> getPlayerMap() {
        return mDynamicPlayerMap;
    }

    public static int getPlayerCount() {
        return mDynamicPlayerMap.size();
    }

    public static void handlePlayerMove(long playerId, int newX, int newY) {
        Player myPlayer = getMyPlayer();
        if (playerId == myPlayer.getId()) {
            return;
        }

        Player netPlayer = getPlayer(playerId);
        netPlayer.setX(newX);
        netPlayer.setY(newY);
    }

    public static Player getPlayer(long id) {
        return (Player) mDynamicPlayerMap.get(id).getBaseObj();
    }

    public static Player getMyPlayer() {
        return (Player) mDynamicPlayer.getBaseObj();
    }

    public static Animation getDynamicPlayer() {
        return mDynamicPlayer;
    }

    public static void handlePlayerAdd(Context context, long playerId, int pointX, int pointY) {
        if (mDynamicPlayerMap.containsKey(playerId)) {
            return;
        }
        addPlayer(context, playerId, pointX, pointY);
        //通知新加入用户我已存在
        //UdpClient.noticeAddPlayer(getMyPlayer());
    }

    public static List<Bitmap> getMyPlayerBitmaps(int direction) {
        return mPlayerTemplates.get(getMyPlayer().getTemplateId()).get(direction);
    }

    public static List<Bitmap> getPlayerBitmaps(int templateId, int direction) {
        return mPlayerTemplates.get(templateId).get(direction);
    }

    public static void setPlayerData(List<List<Integer>> playerData) {
        PlayerManager.mPlayerData = playerData;
    }

    public static void checkPlayer(int x, int y) {
        Iterator<Map.Entry<Long, Animation>> it = mDynamicPlayerMap.entrySet().iterator();

        while (it.hasNext()) {
            Animation dynamicPlayer = it.next().getValue();
            Player player = (Player) dynamicPlayer.getBaseObj();
            int offsetX = Math.abs(player.getX() - x);
            int offsetY = Math.abs(player.getY() - y);
            double d = Math.sqrt(offsetX * offsetX + offsetY * offsetY);

            if (d < player.getWidth()) {
                if (player.isAlive()) {
                    player.setState(Player.STATE_DIE);
                    player.setPlayerVerticalDirection(0);
                    player.setPlayerHorizontalDirection(0);
                    dynamicPlayer.setFrameBitmap(getPlayerBitmaps(player.getTemplateId(), Player.PLAYER_DIE));
                    dynamicPlayer.setLoop(false);
                }
            }

        }

    }
}
