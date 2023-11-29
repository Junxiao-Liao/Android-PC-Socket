package com.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PcClient {

    private static boolean mRunning = true;

    public static void main(String[] args) {
        if (!setupAdbForward()) {
            System.out.println("设置端口转发失败");
            return;
        } else {
            System.out.println("端口转发设置完成"); 
        }
        System.out.println("任意字符, 回车键发送");
        startClient();
    }

    private static boolean setupAdbForward() {
        try {
            Process process = Runtime.getRuntime().exec("adb forward tcp:8000 tcp:9000"); 
            process.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void startClient() {
        try {
            Socket socket = new Socket("127.0.0.1", 8000);
            new Thread(new InThread(socket)).start(); 
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String msg = scanner.next();
                sendToServer(socket, msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    byte[] buffer = new byte[256];
                    int len = dis.read(buffer);  
                    if (len > 0) {
                        System.out.println("\n接收到:" + new String(buffer, 0, len, "UTF-8"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendToServer(Socket socket, String msg) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(msg);
        dos.flush();
        try {
            Thread.sleep(1000L);  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}