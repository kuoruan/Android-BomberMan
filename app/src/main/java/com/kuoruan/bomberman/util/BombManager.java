package com.kuoruan.bomberman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import com.kuoruan.bomberman.R;
import com.kuoruan.bomberman.entity.Animation;
import com.kuoruan.bomberman.entity.Bomb;
import com.kuoruan.bomberman.entity.Fire;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.Player;
import com.kuoruan.bomberman.entity.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Liao on 2016/5/4 0004.
 */
public class BombManager {

    private static final String TAG = "BombManager";
    /**
     * 默认炸弹数量
     */
    public static final int DEFAULT_BOMB_COUNT = 2;

    private static int mTotalBombCount = DEFAULT_BOMB_COUNT;
    private static int mPlayerBombCount = 0;

    private static List<Animation> mDynamicBombList = new CopyOnWriteArrayList<>();
    private static List<Animation> mFireList = new CopyOnWriteArrayList<>();
    private static Map<Integer, List<Bitmap>> mBombTemplates = new HashMap<>();
    private static Map<Integer, List<Integer>> mBombData = null;
    private static Map<Integer, List<Bitmap>> mFireTemplates = new HashMap<>();

    public static void setBomb(Context context) {
        if (mPlayerBombCount < mTotalBombCount) {
            Player mPlayer = PlayerManager.getMyPlayer();
            Point bombPoint = mPlayer.getStandardPoint();
            if (isHaveBomb(bombPoint)) {
                return;
            }

            Bomb bomb = new Bomb(bombPoint, mPlayer.getId());
            Animation dynamicBomb = new Animation(bomb, setAndGetBombTemplates(context, Bomb.TYPE_NORMAL), true);
            mDynamicBombList.add(dynamicBomb);
            mPlayerBombCount++;
        }

        if (mFireTemplates.size() == 0) {
            Bitmap fireMain = BitmapManager.setAndGetBitmap(context, R.drawable.fire);
            int width = fireMain.getWidth() / 4;
            int height = fireMain.getHeight() / 7;
            for (int y = 0; y < 7; y++) {
                List<Bitmap> list = new ArrayList<>();
                for (int x = 0; x < 4; x++) {
                    Bitmap bitmap = Bitmap.createBitmap(fireMain, x * width, y * height, width, height);
                    list.add(bitmap);
                }

                mFireTemplates.put(y + 1, list);
            }
        }
    }

    public static List<Animation> getBombList() {
        return mDynamicBombList;
    }

    public static void decreaseBombCount() {
        mPlayerBombCount--;
    }

    /**
     * 判断位置是否已存在炸弹
     *
     * @param point
     * @return
     */
    public static boolean isHaveBomb(Point point) {
        for (Animation dynamicBomb : mDynamicBombList) {
            Bomb bomb = (Bomb) dynamicBomb.getBaseObj();

            if (point.equals(bomb.getPoint())) {
                Log.i(TAG, "getRealPoint: 已存在炸弹");
                return true;
            }

        }
        return false;
    }

    /**
     * 获取炸弹模版
     *
     * @param context
     * @param bombType
     */
    public static List<Bitmap> setAndGetBombTemplates(Context context, int bombType) {
        if (!mBombTemplates.containsKey(bombType)) {
            List<Integer> data = mBombData.get(bombType);
            List<Bitmap> bitmaps = new ArrayList<>();
            for (int drawable : data) {
                bitmaps.add(BitmapManager.setAndGetBitmap(context, drawable));
            }
            mBombTemplates.put(bombType, bitmaps);
        }

        return mBombTemplates.get(bombType);
    }

    /**
     * 设置炸弹模版信息
     *
     * @param bombData
     */
    public static void setBombData(Map<Integer, List<Integer>> bombData) {
        BombManager.mBombData = bombData;
    }


    /**
     * 炸弹爆炸，创建火焰
     *
     * @param bomb
     */
    public static void explosionBomb(Bomb bomb) {
        int x = bomb.getX();
        int y = bomb.getY();

        List<Animation> fires = new ArrayList<>();
        //创建中间火焰
        Animation center = new Animation(new Fire(x, y), mFireTemplates.get(Fire.TYPE_CENTER));
        fires.add(center);

        int newX = 0;
        int newY = 0;
        boolean crossUp = true;
        boolean crossDown = true;
        boolean crossLeft = true;
        boolean crossRight = true;
        int fireLength = bomb.getFireLength();
        int tileWidth = SceneManager.getScene().getTileWidth();
        int tileHeight = SceneManager.getScene().getTileHeight();
        for (int i = 1; i <= fireLength; i++) {

            //上面火焰
            if (crossUp) {
                newX = x;
                newY = y - i * tileHeight;
                Animation up = createFire(newX, newY);
                if (up != null) {
                    if (i == fireLength) {
                        up.setFrameBitmap(mFireTemplates.get(Fire.TYPE_UP));
                    } else {
                        up.setFrameBitmap(mFireTemplates.get(Fire.TYPE_VERTICAL));
                    }
                    fires.add(up);
                } else {
                    crossUp = false;
                }
            }

            //下面火焰
            if (crossDown) {
                newX = x;
                newY = y + i * tileHeight;
                Animation down = createFire(newX, newY);
                if (down != null) {
                    if (i == fireLength) {
                        down.setFrameBitmap(mFireTemplates.get(Fire.TYPE_DOWN));
                    } else {
                        down.setFrameBitmap(mFireTemplates.get(Fire.TYPE_VERTICAL));
                    }
                    fires.add(down);
                } else {
                    crossDown = false;
                }
            }

            //左边火焰
            if (crossLeft) {
                newX = x - i * tileWidth;
                newY = y;
                Animation left = createFire(newX, newY);
                if (left != null) {
                    if (i == fireLength) {
                        left.setFrameBitmap(mFireTemplates.get(Fire.TYPE_LEFT));
                    } else {
                        left.setFrameBitmap(mFireTemplates.get(Fire.TYPE_HORIZONTAL));
                    }
                    fires.add(left);
                } else {
                    crossLeft = false;
                }
            }

            //右边火焰
            if (crossRight) {
                newX = x + i * tileWidth;
                newY = y;
                Animation right = createFire(newX, newY);
                if (right != null) {
                    if (i == fireLength) {
                        right.setFrameBitmap(mFireTemplates.get(Fire.TYPE_RIGHT));
                    } else {
                        right.setFrameBitmap(mFireTemplates.get(Fire.TYPE_HORIZONTAL));
                    }
                    fires.add(right);
                } else {
                    crossRight = false;
                }
            }
        }

        mFireList.addAll(fires);
    }

    private static Animation createFire(int x, int y) {
        GameTile gameTile = SceneManager.getGameTile(x, y);
        if (gameTile == null) {
            Fire fire = new Fire(x, y);
            return new Animation(fire);
        }
        return null;
    }

    public static List<Animation> getFireList() {
        return mFireList;
    }

    public static void checkBomb(int x, int y) {
        for (Animation dynamicBomb : mDynamicBombList) {
            Bomb bomb = (Bomb) dynamicBomb.getBaseObj();

            if (bomb.getX() == x && bomb.getY() == y) {
                if (!bomb.isExplosion()) {
                    bomb.setExplosion(true);
                }
                break;
            }
        }
    }
}
