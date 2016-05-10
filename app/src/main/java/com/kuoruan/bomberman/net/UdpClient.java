package com.kuoruan.bomberman.net;

import com.kuoruan.bomberman.entity.Bomb;
import com.kuoruan.bomberman.entity.Player;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Window10 on 2016/5/4.
 */
public class UdpClient {

    public static final String SERVER_IP = "255.255.255.255";
    public static final int SERVER_PORT = 8300;

    public static void noticeAddPlayer(final Player player) {
        new Thread() {
            @Override
            public void run() {
                DatagramSocket socket;
                DatagramPacket packet;
                JSONObject jsonObject = new JSONObject();
                //{"code":1,"id":"1","pointX":"12","pointY":"13"}
                try {
                    jsonObject.put(NetProtocol.CODE, NetProtocol.ADD_PLAYER);
                    jsonObject.put(NetProtocol.ID, player.getId());
                    jsonObject.put(NetProtocol.POINTX, player.getX());
                    jsonObject.put(NetProtocol.POINTY, player.getY());
                    String s = jsonObject.toString();
                    byte[] data = s.getBytes("utf-8");
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    packet = new DatagramPacket(data, data.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
                    socket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void noticePlayerMove(final Player player) {
        new Thread() {
            @Override
            public void run() {
                DatagramSocket socket;
                DatagramPacket packet;
                JSONObject jsonObject = new JSONObject();
                //{"code":2,"id":"1","pointX":"12","pointY":"13"}
                try {
                    jsonObject.put(NetProtocol.CODE, NetProtocol.PLAYER_MOVE);
                    jsonObject.put(NetProtocol.ID, player.getId());
                    jsonObject.put(NetProtocol.POINTX, player.getX());
                    jsonObject.put(NetProtocol.POINTY, player.getY());
                    String s = jsonObject.toString();
                    byte[] data = s.getBytes("utf-8");
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    packet = new DatagramPacket(data, data.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
                    socket.send(packet);
                    System.out.println("send....");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void noticeBombExplosion(Bomb bomb) {
        new Thread() {

            @Override
            public void run() {
                DatagramSocket socket;
                DatagramPacket packet;
                JSONObject jsonObject = new JSONObject();

                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
