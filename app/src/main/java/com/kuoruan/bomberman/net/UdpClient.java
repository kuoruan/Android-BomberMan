package com.kuoruan.bomberman.net;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Window10 on 2016/5/4.
 */
public class UdpClient {
    public static void noticeAddPlayer(final NetPlayer player){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket;
                DatagramPacket packet;
                JSONObject jsonObject = new JSONObject();
                //{"code":1,"id":"1","pointX":"12","pointY":"13"}
                try {
                    jsonObject.put("code",NetProtocol.ADD_PLAYER);
                    jsonObject.put("id",player.getId());
                    jsonObject.put("pointX",player.getPlayerUnit().getX());
                    jsonObject.put("pointY",player.getPlayerUnit().getY());
                    String s = jsonObject.toString();
                    byte[] data= s .getBytes("utf-8");
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    packet = new DatagramPacket(data,data.length, InetAddress.getByName("255.255.255.255"),8300);
                    socket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
    public static void noticePlayerMove(final NetPlayer player){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket;
                DatagramPacket packet;
                JSONObject jsonObject = new JSONObject();
                //{"code":2,"id":"1","pointX":"12","pointY":"13"}
                try {
                    jsonObject.put("code",NetProtocol.PLAYER_MOVE);
                    jsonObject.put("id",player.getId());
                    jsonObject.put("pointX",player.getPlayerUnit().getX());
                    jsonObject.put("pointY",player.getPlayerUnit().getY());
                    String s = jsonObject.toString();
                    byte[] data= s .getBytes("utf-8");
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    packet = new DatagramPacket(data,data.length, InetAddress.getByName("255.255.255.255"),8300);
                    socket.send(packet);
                    System.out.println("send....");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
