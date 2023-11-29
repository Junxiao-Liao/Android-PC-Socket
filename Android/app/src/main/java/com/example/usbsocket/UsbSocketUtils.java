package com.example.usbsocket;


import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UsbSocketUtils {

    private static final String TAG = "UsbSocket";

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public interface OnReceiverListener {
        void onReceived(String text);
    }

    private OnReceiverListener receiverListener;

    public UsbSocketUtils(OnReceiverListener listener) {
        this.receiverListener = listener;
    }

    public boolean connect() {
        try {
            serverSocket = new ServerSocket(9000);
            clientSocket = serverSocket.accept();

            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "connect: ", e);
        }
        return false;
    }

    public boolean isConnected() {
        return clientSocket != null && !clientSocket.isClosed();
    }

    public void startReceiver() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                while(true) {
                    try {
                        byte[] buffer = new byte[256];
                        int len = inputStream.read(buffer);
                        if(len > 0) {
                            String text = new String(buffer, 0, len);
                            receiverListener.onReceived(text);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

    public boolean sendText(String text) {
        try {
            outputStream.writeUTF(text);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "sendText: ", e);
        }
        return false;
    }

}
