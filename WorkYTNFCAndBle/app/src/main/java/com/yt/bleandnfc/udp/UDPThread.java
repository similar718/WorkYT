package com.yt.bleandnfc.udp;

import android.util.Log;

import com.yt.bleandnfc.MainActivity;
import com.yt.bleandnfc.listener.SocketListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPThread extends Thread{

    // 定义一个socket
    private DatagramSocket socket = null;
    // 服务器的地址和端口号
    private String serverAddress = "119.23.226.237";
    private int serverPort = 9088;

    private SocketListener socketListener = null;

    private boolean mIsInit = false;

    public UDPThread(String serverAddress1,int serverPort1){
        if (!mIsInit || socket == null) {
            serverAddress = serverAddress1;
            serverPort = serverPort1;
            init();
        }
    }

    private void init(){
        try {
            mIsInit = true;
            if (socket == null) {
                socket = new DatagramSocket(serverPort);
            }
        } catch (SocketException e){
            e.printStackTrace();
            Log.d("socket", e.getMessage());
        }
    }

    public void setSocketListener(SocketListener listener){
        socketListener = listener;
    }

    public void sendSocketData(String message){
        if (!mIsInit || socket == null) {
            init();
        }
        byte[] packs = MainActivity.hexStrToByteArray(message);
        try {
            if (socket == null) {
                init();
            }
            socket.send(new DatagramPacket(packs,packs.length,InetAddress.getByName(serverAddress),serverPort));
            if(socketListener != null)
                socketListener.sendSocketData(message);
        } catch (IOException e){
            e.printStackTrace();
            Log.d("socket", e.getMessage());
            if(socketListener != null)
                socketListener.error(e);
        }
    }

    @Override
    public void run() {
        super.run();
        Log.d("socket", "start run");
        try {
            if (!mIsInit || socket == null) {
                init();
            }
            while (true){
                // 接收数据
                byte[] receiveBytes = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(receiveBytes, receiveBytes.length);
                if(socket != null)
                    socket.receive(datagramPacket);
                // 解析数据
//                byte[] data = datagramPacket.getData();
                String json = new String(datagramPacket.getData() , datagramPacket.getOffset() , datagramPacket.getLength());
                if(socketListener != null)
                    socketListener.receiveSocketData(json);
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Log.d("socket", "socket exception :" + e.getMessage());
            if(socketListener != null)
                socketListener.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("socket", "io exception :" + e.getMessage());
            if(socketListener != null)
                socketListener.error(e);
        } finally {
            Log.d("socket", "close");
            if (socket != null) {
                socket.close();
            }
        }
    }
}
