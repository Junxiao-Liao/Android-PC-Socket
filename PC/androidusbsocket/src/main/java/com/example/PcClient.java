package com.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PcClient {

    private static boolean mRunning = true;

    // Set up adb port forwarding
    public static void main(String[] args) {
        if (!setupAdbForward()) {
            System.out.println("Failed to set up port forwarding");
            return;
        } else {
            System.out.println("Port forwarding set up successfully"); 
        }

        // Prompt user for input
        System.out.println("Enter any string and press enter to send");
        // Start client
        startClient();
    }

    // Method to set up adb port forwarding
    private static boolean setupAdbForward() {
        try {
             // Execute adb command to forward from PC port 8000 to device port 9000
            Process process = Runtime.getRuntime().exec("adb forward tcp:8000 tcp:9000"); 
            process.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to start the Socket client
    private static void startClient() {
        try {
            // Create Socket connected to port 8000, which is forwarded to device port 9000
            Socket socket = new Socket("127.0.0.1", 8000);
            // Start a thread to continuously receive data from server
            new Thread(new InThread(socket)).start(); 
            // Read user input with Scanner
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String msg = scanner.next();
                // Send user input string to server
                sendToServer(socket, msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread to continuously receive server data
    static class InThread implements Runnable {
        private Socket socket;

        InThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (mRunning) {
                if (socket.isClosed()) {
                    mRunning = false;
                    break;
                } 
                try {
                    // Receive data from server via Socket input stream
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    byte[] buffer = new byte[256];
                    int len = dis.read(buffer);  
                    if (len > 0) {
                        // Print received data
                        System.out.println("\nReceived:" + new String(buffer, 0, len, "UTF-8"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to send data to server 
    public static void sendToServer(Socket socket, String msg) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(msg);
        dos.flush();
        // Add brief delay before sending next message
        try {
            Thread.sleep(1000L);  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}