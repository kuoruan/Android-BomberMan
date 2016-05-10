package com.kuoruan.bomberman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liao on 2016/5/5 0005.
 */
public class BitmapManager {

    private static Map<Integer, Bitmap> mBitmapMaps = new HashMap<>();

    public static Bitmap setAndGetBitmap(Context context, int resourceId) {
        if (!mBitmapMaps.containsKey(resourceId)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);

            if (bitmap != null) {
                mBitmapMaps.put(resourceId, bitmap);
            }
        }

        return mBitmapMaps.get(resourceId);
    }

    public static Map<Integer, Bitmap> getBitmapMaps() {
        return mBitmapMaps;
    }
}
