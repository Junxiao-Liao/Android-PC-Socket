package com.example.usbsocket;


import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UsbSocketUtils {

    private static final String TAG = "UsbSocket";

    // Server socket to listen for USB device connections
    private ServerSocket serverSocket;
    // Client socket once USB device is connected
    private Socket clientSocket;
    // Streams for sending and receiving data
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    // Interface for receiving data callbacks
    public interface OnReceiverListener {
        void onReceived(String text);
    }

    private OnReceiverListener receiverListener;

    public UsbSocketUtils(OnReceiverListener listener) {
        this.receiverListener = listener;
    }

    // Accept USB connection and open streams
    public boolean connect() {
        try {
            // Create server socket to listen for connections
            serverSocket = new ServerSocket(9000);
            // Accept connection from USB client
            clientSocket = serverSocket.accept();

            // Get input and output streams
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

    // Start thread to listen for incoming data
    public void startReceiver() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                // Continuously read input stream
                while(true) {
                    try {
                        byte[] buffer = new byte[256];
                        int len = inputStream.read(buffer);
                        if(len > 0) {
                            // Read data if available
                            String text = new String(buffer, 0, len);
                            // Call receiver callback
                            receiverListener.onReceived(text);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

    // Send text data to the USB device
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
