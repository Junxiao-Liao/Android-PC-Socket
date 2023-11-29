package com.example.usbsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText inputEdit;
    private TextView receiveText;

    private UsbSocketUtils socketUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEdit = findViewById(R.id.input_edit);
        receiveText = findViewById(R.id.receive_text);

        socketUtils = new UsbSocketUtils(new UsbSocketUtils.OnReceiverListener() {
            @Override
            public void onReceived(final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在主线程中更新UI的操作
                        receiveText.append(text + "\n");
                    }
                });
            }
        });

        // 接收
        new Thread() {

            @Override
            public void run() {

                while(true) {
                    try {
                        socketUtils.connect();
                        if(socketUtils.isConnected()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    socketUtils.startReceiver();
                                }
                            });
                            break;
                        }
                    } catch (Exception e) {
                        // 连接异常
                    }

                    // 连接失败,1秒后重试
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }.start();

        // 发送
        inputEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        socketUtils.sendText(inputEdit.getText().toString());
                    }
                }).start();
                return false;
            }
        });
    }
}
