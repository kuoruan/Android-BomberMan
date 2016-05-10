package com.kuoruan.bomberman.net;

import android.content.Context;

import com.kuoruan.bomberman.util.PlayerManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * Created by Window10 on 2016/5/3.
 */
public class UdpServer extends Thread {
    public static final int PORT = 8300;
    private Context mGameContext;
    private boolean mRunning = false;

    public UdpServer(Context context) {
        mGameContext = context;
    }

    @Override
    public void run() {
        mRunning = true;
        byte[] buffer = new byte[65507];
        DatagramSocket server = null;
        try {
            server = new DatagramSocket(PORT);

            while (mRunning) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    server.receive(packet);
                    String s = new String(packet.getData(), 0, packet.getLength(), "utf-8");
                    System.out.println("server:" + packet.getAddress());
                    System.out.println("receive:" + s);
                    JSONObject jsonObject = new JSONObject(s.trim());
                    switch (jsonObject.getInt(NetProtocol.CODE)) {
                        case NetProtocol.ADD_PLAYER:
                            addPlayer(jsonObject);
                            break;
                        case NetProtocol.PLAYER_MOVE:
                            playerMove(jsonObject);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void playerMove(JSONObject jsonObject) {
        long id = 0;
        try {
            id = jsonObject.getLong(NetProtocol.ID);
            int x = jsonObject.getInt(NetProtocol.POINTX);
            int y = jsonObject.getInt(NetProtocol.POINTY);
            PlayerManager.handlePlayerMove(id, x, y);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addPlayer(JSONObject jsonObject) {

        try {
            long id = jsonObject.getLong(NetProtocol.ID);
            int x = jsonObject.getInt(NetProtocol.POINTX);
            int y = jsonObject.getInt(NetProtocol.POINTY);
            PlayerManager.handlePlayerAdd(mGameContext, id, x, y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
