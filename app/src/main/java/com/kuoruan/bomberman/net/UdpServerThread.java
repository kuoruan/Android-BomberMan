package com.kuoruan.bomberman.net;

import android.content.Context;

import com.kuoruan.bomberman.entity.PlayerUnit;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * Created by Window10 on 2016/5/3.
 */
public class UdpServerThread extends Thread {
    public static final int PORT = 8300;
    private Context mContext;
    private boolean running;

    public UdpServerThread(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        running = true;
        byte[] buffer = new byte[65507];
        DatagramSocket server = null;
        try {
            server = new DatagramSocket(PORT);


            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    server.receive(packet);
                    String s = new String(packet.getData(), 0, packet.getLength(), "utf-8");
                    System.out.println("server:" +   packet.getAddress());
                    System.out.println("receive:" + s);
                    JSONObject jsonObject = new JSONObject(s.trim());
                    switch (jsonObject.getInt("code")) {
                        case NetProtocol.ADD_PLAYER:
                            addNetPlayer(jsonObject);
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
            id = jsonObject.getLong("id");
            int x = jsonObject.getInt("pointX");
            int y = jsonObject.getInt("pointY");
            if (id == NetPlayerManager.getMyPlayer().getId()) {
                return;
            }
            for (NetPlayer netPlayer : NetPlayerManager.getNetPlayerList()) {
                if (netPlayer.getId() == id) {
                    netPlayer.getPlayerUnit().setX(x);
                    netPlayer.getPlayerUnit().setY(y);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void addNetPlayer(JSONObject jsonObject) {

        long id = 0;
        try {
            id = jsonObject.getLong("id");
            int x = jsonObject.getInt("pointX");
            int y = jsonObject.getInt("pointY");
            if(NetPlayerManager.isHaveNetPlayer(id)){
                for(NetPlayer netPlayer:NetPlayerManager.getNetPlayerList()){
                    if(netPlayer.getId()==id){
                       return;
                    }
                }
            }
            NetPlayerManager.addNetPlayer(mContext, id, x, y);
            UdpClient.noticeAddPlayer(NetPlayerManager.getMyPlayer());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
