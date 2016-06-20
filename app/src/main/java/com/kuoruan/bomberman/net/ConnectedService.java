package com.kuoruan.bomberman.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kuoruan.bomberman.util.ConnectConstants;
import com.kuoruan.bomberman.util.DataUtil;
import com.kuoruan.bomberman.util.GameConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 联机后数据传输
 */
public class ConnectedService {

    public static final String TAG = "ConnectedService";
    private static final boolean DEBUG = true;

    private String mIp;

    private Socket mSocket;
    private GameReceiver mReceiver;
    private GameSender mSender;
    private boolean isServer;

    private static final int TCP_PORT = 8899;

    private Handler mRequestHandler;

    public ConnectedService(Handler handler, String ip, boolean isServer) {
        mRequestHandler = handler;
        this.isServer = isServer;
        this.mIp = ip;
        mReceiver = new GameReceiver();
        mReceiver.start();

        HandlerThread sendThread = new HandlerThread("GameSender");
        sendThread.start();
        mSender = new GameSender(sendThread.getLooper());
    }

    public void stop() {
        mSender.quit();
        mReceiver.quit();
    }

    /**
     * 发送TCP数据
     *
     * @param jsonObject
     */
    public void sendTCPData(JSONObject jsonObject) {
        byte[] data = DataUtil.JSONToTCPBytes(jsonObject);
        Message msg = Message.obtain();
        msg.obj = data;
        mSender.sendMessage(msg);
    }

    /**
     * TCP消息接收线程
     */
    class GameReceiver extends Thread {

        //byte[] buf = new byte[1024];
        boolean isRunning = true;

        ServerSocket server;
        InputStream is;
        BufferedReader buf;

        public GameReceiver() {
        }

        @Override
        public void run() {
            try {
                if (isServer) {
                    Log.i(TAG, "Connected: it is server");
                    server = new ServerSocket(TCP_PORT);
                    mSocket = server.accept();
                    Log.d(TAG, "server:net connected");
                    mRequestHandler.sendEmptyMessage(ConnectConstants.GAME_CONNECTED);
                } else {
                    Log.i(TAG, "Connected: it is client");
                    Socket s = new Socket();
                    InetSocketAddress address = new InetSocketAddress(mIp, TCP_PORT);
                    /*
                     * 连接失败尝试重连，重试8次 因为机器性能不一样不能保证作为Server端的Activity先于客户端启动
					 */
                    int retryCount = 0;
                    while (retryCount < 8) {
                        try {
                            s.connect(address);
                            mSocket = s;
                            mRequestHandler.sendEmptyMessage(ConnectConstants.GAME_CONNECTED);
                            Log.d(TAG, "client:net connected");
                            break;
                        } catch (IOException e) {
                            retryCount++;
                            s = new Socket();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e1) {
                                Log.d(TAG, "thread exception : " + e1.getMessage());
                            }
                            Log.d(TAG, "connect exception ：" + e.getMessage() + "  retry count=" + retryCount);
                        }
                    }
                    if (retryCount >= 8) {
                        return;
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "socket exception:" + e.getMessage());
                return;
            }

            try {
                is = mSocket.getInputStream();
                buf = new BufferedReader(new InputStreamReader(is));
                String data;
                while (isRunning) {
                    if ((data = buf.readLine()) == null) {
                        // 连接断开
                        break;
                    }
                    if (DEBUG) {
                        Log.d(TAG, "tcp received:" + data);
                    }
                    processNetData(data);
                }
            } catch (IOException e) {
                Log.d(TAG, "IOException: an error occurs while receiving data");
            }
        }

        public void quit() {
            try {
                isRunning = false;
                if (is != null) {
                    is.close();
                }
                if (buf != null) {
                    buf.close();
                }
                if (mSocket != null) {
                    mSocket.close();
                }
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "close Socket Exception:" + e.getMessage());
            }
        }

    }

    /**
     * 把消息交给TCP发送线程发送
     */
    class GameSender extends Handler {

        public GameSender(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            byte[] data = (byte[]) msg.obj;
            try {
                if (mSocket == null) {
                    Log.d(TAG, "Send fail,socket is null");
                    return;
                }
                OutputStream os = mSocket.getOutputStream();
                // 发送数据
                os.write(data);
                os.flush();
            } catch (IOException e) {
                Log.d(TAG, "tcp socket error:" + e.getMessage());
            }

        }

        public void quit() {
            getLooper().quit();
        }

    }

    // 处理消息
    private void processNetData(String data) {
        JSONObject jsonObject = null;
        int type = 0;
        try {
            jsonObject = new JSONObject(data);
            type = jsonObject.getInt(ConnectConstants.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (type) {
            case GameConstants.PLAYER_ADD:
            case GameConstants.PLAYER_MOVE:
            case GameConstants.PLAYER_STOP:
                notifyPlayerAction(type, jsonObject);
                break;
            default:
                break;
        }
    }

    private void notifyPlayerAction(int type, JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        Message msg = Message.obtain();
        msg.what = type;
        msg.obj = jsonObject;
        mRequestHandler.sendMessage(msg);
    }

}