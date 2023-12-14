package com.example.usbsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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
            public void onReceived(String text) {
                receiveText.append(text + "\n");
            }
        });

        socketUtils.connect();

        inputEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                socketUtils.send(inputEdit.getText().toString());
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketUtils.disconnect();
    }
}
