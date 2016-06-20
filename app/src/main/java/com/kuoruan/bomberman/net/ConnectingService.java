package com.kuoruan.bomberman.net;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kuoruan.bomberman.util.ConnectConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * UDP联机管理<br>
 * 初始化这个对象后，调用{@link #start()}方法就会搜索局域网 内的可连接手机，同时自己也会成为别人可见的对象。<br>
 * 当搜索到可联机对象后会返回可连对象的机器名和IP地址。
 */
public class ConnectingService {
    public static final String TAG = "ConnectManager";
    private static final boolean DEBUG = true;

    private String mIp;

    // UPD接收程序
    private DatagramSocket mDataSocket;
    // 点对多广播
    private MulticastSocket mMulticastSocket;
    private InetAddress mCastAddress;

    // 广播组地址
    private static final String MUL_IP = "230.0.2.2";
    //多人游戏端口
    private static final int MUL_PORT = 1688;
    //UDP端口
    private static final int UDP_PORT = 2599;

    // 接收UPD消息
    private UDPReceiver mUDPReceiver;
    // 接收广播消息
    private MulticastReceiver mMulticastReceiver;
    // udp消息发送模块
    private UDPSendHandler mUDPSender;
    // 广播消息发送模块
    private MulticastSendHandler mBroadcastSender;

    private Handler mRequestHandler;

    public ConnectingService(String ip, Handler request) {
        this.mRequestHandler = request;
        this.mIp = ip;
        this.mUDPReceiver = new UDPReceiver();
        this.mMulticastReceiver = new MulticastReceiver();
    }

    /**
     * 启动连接程序
     */
    public void start() {
        mUDPReceiver.start();
        mMulticastReceiver.start();

        HandlerThread udpThread = new HandlerThread("udpSender");
        udpThread.start();
        mUDPSender = new UDPSendHandler(udpThread.getLooper());

        HandlerThread broadcastThread = new HandlerThread("broadcastSender");
        broadcastThread.start();
        mBroadcastSender = new MulticastSendHandler(broadcastThread.getLooper());
    }

    public void stop() {
        mUDPReceiver.quit();
        mMulticastReceiver.quit();
        mUDPSender.getLooper().quit();
        mBroadcastSender.getLooper().quit();
    }

    /**
     * 发送一个查询广播消息，查询当前可连接对象
     */
    public void sendScanMsg() {
        Message msg = Message.obtain();
        byte[] buf = packageBroadcast(ConnectConstants.BROADCAST_JOIN);
        msg.obj = buf;
        mBroadcastSender.sendMessage(msg);
    }

    /**
     * 发送一个查询广播消息，退出可联机
     */
    public void sendExitMsg() {
        // 起一个线程发送一个局域网广播(android主线程不能有网络操作)
        // 不用mMultiCastSocket对象发送时因为退出的时候涉及跨线程操作
        // 可能mMultiCastSocket已经close状态，不可控
        new Thread() {
            @Override
            public void run() {
                MulticastSocket multicastSocket;
                try {
                    InetAddress address = InetAddress.getByName(MUL_IP);
                    multicastSocket = new MulticastSocket();
                    multicastSocket.setTimeToLive(1);
                    byte[] buf = packageBroadcast(ConnectConstants.BROADCAST_EXIT);
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    // 接收地址和group的标识相同
                    datagramPacket.setAddress(address);
                    // 发送至的端口号
                    datagramPacket.setPort(MUL_PORT);
                    multicastSocket.send(datagramPacket);
                    multicastSocket.close();
                } catch (IOException e) {
                    Log.d(TAG, "send exit multiCast fail:" + e.getMessage());
                }
            }
        }.start();
    }

    /**
     * 发送请求连接消息
     *
     * @param ipDst
     */
    public void sendAskConnect(String ipDst) {
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putString("ipDst", ipDst);
        byte[] data = createAskConnect();
        b.putByteArray("data", data);
        msg.setData(b);
        mUDPSender.sendMessage(msg);
    }


    /**
     * 同意联机
     */
    public void accept(String ipDst) {
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putString("ipDst", ipDst);
        byte[] data = createConnectResponse(ConnectConstants.CONNECT_AGREE);
        b.putByteArray("data", data);
        msg.setData(b);
        mUDPSender.sendMessage(msg);
    }

    /**
     * 拒绝请求
     */
    public void reject(String ipDst) {
        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putString("ipDst", ipDst);
        byte[] data = createConnectResponse(ConnectConstants.CONNECT_REJECT);
        b.putByteArray("data", data);
        msg.setData(b);
        mUDPSender.sendMessage(msg);
    }

    /**
     * 接收UDP消息，未建立TCP连接之前，都通过udp接收消息
     *
     * @author qingc
     */
    class UDPReceiver extends Thread {

        byte[] buf = new byte[1024];
        boolean isRunning = true;

        private DatagramSocket dataSocket;
        private DatagramPacket dataPacket;

        public UDPReceiver() {
            try {
                dataSocket = new DatagramSocket(UDP_PORT);
                mDataSocket = dataSocket;
                dataPacket = new DatagramPacket(buf, buf.length);
            } catch (SocketException e) {
                isRunning = false;
                Log.d(TAG, "Socket Exception:" + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                while (isRunning) {
                    mDataSocket.receive(dataPacket);
                    String data = new String(dataPacket.getData(), 0, dataPacket.getLength());
                    Log.d(TAG, "udp receiver: " + data);
                    processUDPReceive(data);
                }
            } catch (SocketException e) {
                isRunning = false;
                Log.d(TAG, "Socket Exception:" + e.getMessage());
            } catch (IOException e) {
                isRunning = false;
                Log.d(TAG, "IOException: an error occurs while receiving the packet");
            }
        }

        public void quit() {
            dataSocket.close();
            isRunning = false;
        }

    }

    /**
     * 处理UDP接受数据
     *
     * @param data 接收到的Json字符串
     */
    private void processUDPReceive(String data) {
        JSONObject jsonObject = null;
        int type = 0;
        try {
            jsonObject = new JSONObject(data);
            type = jsonObject.getInt(ConnectConstants.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (type) {
            case ConnectConstants.UDP_JOIN:
                processUDPJoin(jsonObject);
                break;
            case ConnectConstants.CONNECT_ASK:
                processConnectAsk(jsonObject);
                break;
            case ConnectConstants.CONNECT_AGREE:
            case ConnectConstants.CONNECT_REJECT:
                processConnectResponse(jsonObject);
            default:
                break;
        }
    }

    /**
     * 发送UDP消息，未建立TCP连接之前，都通过UDP发送指令到制定的ip
     */
    class UDPSendHandler extends Handler {

        public UDPSendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.peekData();
            String ipDst = b.getString("ipDst");
            byte[] data = b.getByteArray("data");
            Log.d(TAG, "udp send destination ip:" + ipDst);
            if (DEBUG) {
                Log.d(TAG, "udp send data:" + Arrays.toString(data));
            }
            if (data == null) {
                onError(ConnectConstants.UDP_DATA_ERROR);
            }
            try {
                DatagramSocket ds;
                ds = mDataSocket;
                if (ds == null) {
                    onError(ConnectConstants.SOCKET_NULL);
                    return;
                }
                InetAddress dstAddress = InetAddress.getByName(ipDst);

                // 创建发送数据包
                DatagramPacket dataPacket = new DatagramPacket(data, data.length, dstAddress, UDP_PORT);
                ds.send(dataPacket);
            } catch (UnknownHostException e1) {
                Log.d(TAG, "ip is not corrected");
                onError(ConnectConstants.UDP_IP_ERROR);
            } catch (IOException e) {
                Log.d(TAG, "udp socket error:" + e.getMessage());
            }

        }

        public void quit() {
            getLooper().quit();
        }

    }

    /**
     * 接收广播消息线程，监听其他手机的扫描或加入广播
     */
    class MulticastReceiver extends Thread {

        byte[] buffer = new byte[1024];
        private boolean isRunning = true;

        private MulticastSocket multiSocket;
        private DatagramPacket dataPacket;

        public MulticastReceiver() {
            try {
                InetAddress address = InetAddress.getByName(MUL_IP);
                multiSocket = new MulticastSocket();
                // 接收数据时需要指定监听的端口号
                multiSocket = new MulticastSocket(MUL_PORT);
                // 加入广播组
                mCastAddress = address;
                multiSocket.joinGroup(address);
                multiSocket.setTimeToLive(1);
                dataPacket = new DatagramPacket(buffer, buffer.length);
                // 全局引用指向这里的广播socket,用于发送广播消息
                mMulticastSocket = multiSocket;
            } catch (IOException e) {
                isRunning = false;
                Log.d(TAG, "Init multiCast fail by IOException=" + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                while (isRunning) {
                    // 接收数据，会进入阻塞状态
                    mMulticastSocket.receive(dataPacket);
                    // 从buffer中截取收到的数据
                    byte[] message = new byte[dataPacket.getLength()];
                    System.arraycopy(buffer, 0, message, 0,
                            dataPacket.getLength());
                    Log.d(TAG, "multiCast receive:" + Arrays.toString(message));
                    String ip = processBroadcast(message);
                    // Check ip address and send ip address myself to it.
                    if (ip != null && !ip.equals(mIp)) {
                        Message msg = Message.obtain();
                        Bundle b = new Bundle();
                        b.putString("ipDst", ip);
                        byte[] data = packageUDPJoin();
                        b.putByteArray("data", data);
                        msg.setData(b);
                        mUDPSender.sendMessage(msg);
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "IOException=" + e.getMessage());
            }
        }

        public void quit() {
            // close socket
            multiSocket.close();
            isRunning = false;
        }

    }

    /**
     * 发送广播消息
     */
    class MulticastSendHandler extends Handler {

        public MulticastSendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            byte[] buf = (byte[]) msg.obj;
            if (DEBUG) {
                Log.d(TAG, "BroadcastSendHandler:data=" + buf);
            }
            MulticastSocket s = mMulticastSocket;
            if (s == null) {
                onError(ConnectConstants.SOCKET_NULL);
                return;
            }
            InetAddress address = mCastAddress;
            if (address == null || !address.isMulticastAddress()) {
                onError(ConnectConstants.MULTICAST_ERROR);
                return;
            }
            try {
                // s.setTimeToLive(1); is it nessary?
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                // 设置发送group地址
                datagramPacket.setAddress(address);
                // 发送至的端口号
                datagramPacket.setPort(MUL_PORT);
                s.send(datagramPacket);
            } catch (IOException e) {
                Log.d(TAG, "send multicast fail:" + e.getMessage());
                onError(ConnectConstants.SOCKET_NULL);
            }
        }

        public void quit() {
            getLooper().quit();
        }
    }

    /**
     * 错误信息
     *
     * @param error
     */
    private void onError(int error) {
        Log.d(TAG, "error:" + error);
        Message msg = Message.obtain();
        msg.what = error;
        mRequestHandler.sendMessage(msg);
    }

    /**
     * 有新的可联机对象加入
     *
     * @param name 机器名
     * @param ip   地址
     */
    private void onJoin(String name, String ip) {
        Message msg = Message.obtain();
        msg.what = ConnectConstants.ON_JOIN;
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("ip", ip);
        msg.setData(b);
        mRequestHandler.sendMessage(msg);
    }

    /**
     * 有可联机对象退出
     *
     * @param name 机器名
     * @param ip   地址
     */
    private void onExit(String name, String ip) {
        Message msg = Message.obtain();
        msg.what = ConnectConstants.ON_EXIT;
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("ip", ip);
        msg.setData(b);
        mRequestHandler.sendMessage(msg);
    }

    /**
     * 处理加入可连接对象消息
     *
     * @param jsonObject 接收到Json对象
     */
    private void processUDPJoin(JSONObject jsonObject) {
        String name = null;
        String ip = null;
        try {
            name = jsonObject.getString(ConnectConstants.HOST_NAME);
            ip = jsonObject.getString(ConnectConstants.IP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //处理加入用户
        onJoin(name, ip);
    }

    /**
     * 将本机名称和ip地址封装成Json发送
     *
     * @return
     */
    private byte[] packageUDPJoin() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.HOST_NAME, Build.BRAND + "-" + Build.MODEL);
            jsonObject.put(ConnectConstants.IP, mIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString().getBytes();
    }

    /**
     * 处理广播消息
     *
     * @param data 接收到的消息体
     * @return 返回解析到的ip地址
     */
    private String processBroadcast(byte[] data) {
        String json = new String(data);
        String ip = null;
        String name = null;
        int type = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            ip = jsonObject.getString(ConnectConstants.IP);
            name = jsonObject.getString(ConnectConstants.HOST_NAME);
            type = jsonObject.getInt(ConnectConstants.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "processBroadcast-->" + "name=" + name + "  ip=" + ip);
        // 如果是自己发送的信息，则不加入可连接集合
        if (mIp.equals(ip)) {
            return ip;
        }
        if (type == ConnectConstants.BROADCAST_JOIN) {
            onJoin(name, ip);
        } else if (type == ConnectConstants.BROADCAST_EXIT) {
            onExit(name, ip);
        }
        return ip;
    }

    /**
     * 将本机名称和ip地址封装成byte数组
     *
     * @return
     */
    private byte[] packageBroadcast(int type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.IP, mIp);
            jsonObject.put(ConnectConstants.HOST_NAME, Build.BRAND + "-" + Build.MODEL);
            jsonObject.put(ConnectConstants.TYPE, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString().getBytes();
    }

    /**
     * 封装请求连接消息体
     *
     * @return
     */
    private byte[] createAskConnect() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.IP, mIp);
            jsonObject.put(ConnectConstants.HOST_NAME, Build.BRAND + "-" + Build.MODEL);
            jsonObject.put(ConnectConstants.TYPE, ConnectConstants.CONNECT_ASK);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString().getBytes();
    }

    /**
     * 解析请求联机数据
     *
     * @param jsonObject
     */
    private void processConnectAsk(JSONObject jsonObject) {
        String ip = null;
        String name = null;
        int type = 0;

        try {
            ip = jsonObject.getString(ConnectConstants.IP);
            name = jsonObject.getString(ConnectConstants.HOST_NAME);
            type = jsonObject.getInt(ConnectConstants.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "processUDPJoin-->" + "name=" + name + "  ip=" + ip);
        Message msg = Message.obtain();
        msg.what = type;
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("ip", ip);
        msg.setData(b);
        mRequestHandler.sendMessage(msg);
    }

    /**
     * 创建连接相应消息
     *
     * @param type
     * @return 消息数组
     */
    private byte[] createConnectResponse(int type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConnectConstants.IP, mIp);
            jsonObject.put(ConnectConstants.HOST_NAME, Build.BOARD);
            jsonObject.put(ConnectConstants.TYPE, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString().getBytes();
    }

    /**
     * 解析连接请求响应并处理
     *
     * @param jsonObject
     */
    private void processConnectResponse(JSONObject jsonObject) {
        String ip = null;
        String name = null;
        int type = 0;
        try {
            ip = jsonObject.getString(ConnectConstants.IP);
            name = jsonObject.getString(ConnectConstants.HOST_NAME);
            type = jsonObject.getInt(ConnectConstants.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "processConnectResponse-->" + "name=" + name + "  ip=" + ip);
        Message msg = Message.obtain();
        msg.what = type;
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("ip", ip);
        msg.setData(b);
        mRequestHandler.sendMessage(msg);
    }

}
