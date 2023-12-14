package com.example.usbsocket;


import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UsbSocketUtils {

    private static final String TAG = "UsbSocket";

    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private boolean isRunning;

    public interface OnReceiverListener {
        void onReceived(String text);
    }

    private OnReceiverListener receiverListener;

    public UsbSocketUtils(OnReceiverListener listener) {
        this.receiverListener = listener;
    }

    public boolean connect() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(9000);
                clientSocket = serverSocket.accept();

                inputStream = new DataInputStream(clientSocket.getInputStream());
                outputStream = new DataOutputStream(clientSocket.getOutputStream());

                isRunning = true;
                startReceiverThread();
            } catch (IOException e) {
                Log.e(TAG, "connect: ", e);
            }
        }).start();

        return true;
    }

    private void startReceiverThread() {
        new Thread(() -> {
            while(isRunning) {
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
        }).start();
    }

    public void disconnect() {
        new Thread(() -> {
            isRunning = false;
            closeSocket();
        }).start();
    }

    private void closeSocket() {
        try {
            if(clientSocket != null) {
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "closeSocket: ", e);
        }
    }

    public void send(String text) {
        new Thread(() -> {
            try {
                outputStream.writeUTF(text);
                outputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "sendText: ", e);
            }
        }).start();
    }
}
