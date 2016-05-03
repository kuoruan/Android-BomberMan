package com.kuoruan.bomberman.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static android.provider.BaseColumns._ID;

/**
 * Created by Liao on 2016/5/1 0001.
 */
public class GameData extends GameDao {

    public static final String TABLE_NAME = "gameUnitData";

    public static final String TYPE = "type";
    public static final String SUB_TYPE = "subType";
    public static final String DRAWABLE = "drawable";
    public static final String VISIBLE = "visible";

    //图片类型
    public static final int TYPE_TILE = 1;
    public static final int TYPE_PLAYER = 2;

    public static final int FIELD_ID_ID = 0;
    public static final int FIELD_ID_TYPE = 1;
    public static final int FIELD_ID_SUB_TYPE = 2;
    public static final int FIELD_ID_DRAWABLE = 3;
    public static final int FIELD_ID_VISIBLE = 4;


    public GameData(Context context) {
        super(context);
    }

    public HashMap<Integer, ArrayList<Integer>> getGameTileData() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] from = {_ID, TYPE, SUB_TYPE, DRAWABLE, VISIBLE};
        String where = TYPE + " = " + TYPE_TILE;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        HashMap<Integer, ArrayList<Integer>> data = new HashMap<Integer, ArrayList<Integer>>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();

                arrayList.add(cursor.getInt(FIELD_ID_ID));
                arrayList.add(cursor.getInt(FIELD_ID_TYPE));
                arrayList.add(cursor.getInt(FIELD_ID_SUB_TYPE));
                arrayList.add(cursor.getInt(FIELD_ID_DRAWABLE));
                arrayList.add(cursor.getInt(FIELD_ID_VISIBLE));

                data.put(cursor.getInt(FIELD_ID_ID), arrayList);
            }
            cursor.close();
        }

        db.close();

        return data;
    }

    public ArrayList<ArrayList<Integer>> getPlayerUnitData() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] from = {_ID, TYPE, SUB_TYPE, DRAWABLE, VISIBLE};
        String where = TYPE + " = " + TYPE_PLAYER;

        Cursor cursor = db.query(TABLE_NAME, from, where, null, null, null, null);

        ArrayList<ArrayList<Integer>> data = new ArrayList<ArrayList<Integer>>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(cursor.getInt(FIELD_ID_ID));
                arrayList.add(cursor.getInt(FIELD_ID_TYPE));
                arrayList.add(cursor.getInt(FIELD_ID_SUB_TYPE));
                arrayList.add(cursor.getInt(FIELD_ID_DRAWABLE));
                arrayList.add(cursor.getInt(FIELD_ID_VISIBLE));

                data.add(arrayList);
            }

            cursor.close();
        }
        db.close();

        return data;
    }

}
