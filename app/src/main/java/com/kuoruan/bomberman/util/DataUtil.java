package com.kuoruan.bomberman.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Window10 on 2016/3/14.
 */
public class DataUtil {
    public static String streamToString(InputStream is) {
        byte[] bytes = new byte[1024];
        int length = 0;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            while ((length = is.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
            os.flush();
            String result = new String(os.toByteArray(), "UTF-8");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] JSONToTCPBytes(JSONObject jsonObject) {
        String data = jsonObject.toString() + "\n";
        return data.getBytes();
    }

    public static JSONObject bundleToJSONObj(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, wrap(bundle.get(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json;
    }

    public static Object wrap(Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return new JSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                return toJSONArray(o);
            }
            if (o instanceof Map) {
                return new JSONObject((Map) o);
            }
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Object toJSONArray(Object array) throws JSONException {
        JSONArray result = new JSONArray();
        if (!array.getClass().isArray()) {
            throw new JSONException("Not a primitive array: " + array.getClass());
        }
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            result.put(wrap(Array.get(array, i)));
        }
        return result;
    }
}
