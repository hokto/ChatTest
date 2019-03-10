package com.example.hokut.chattest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.os.Build;
import android.os.Handler;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Handler mHandler;

    private WebSocketClient mClient;
    public String id;
    public final void MakeMessage(final String message_log)
    {
        LinearLayout linearLayout=findViewById(R.id.layout);
        TextView textView=new TextView(this);
        String name=message_log.substring(0,message_log.indexOf(":"));
        String message=message_log.substring(message_log.indexOf(":")+1,message_log.length());
        textView.setText(message);
        textView.setWidth(100);
        textView.setHeight(50*((message.length()/16)+1));
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(name.equals(id))
        {
            param.setMargins(600,0,0,0);
        }
        linearLayout.addView(textView,param);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIDconfig();
    }
    public void setIDconfig()
    {
        setContentView(R.layout.activity_idconfig);
        Button id_btn=findViewById(R.id.ID_btn);
        final EditText id_edit=findViewById(R.id.IDconfig_edit);
        id_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                id=id_edit.getText().toString();
                setMainWindow();
            }
        });
    }
    public void setMainWindow()
    {
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        if ("sdk".equals(Build.PRODUCT)) {
            // エミュレータの場合はIPv6を無効    ----1
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }
        try {
            URI uri = new URI("ws://192.168.0.23:8888");
            mClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "onOpen");
                }

                @Override
                public void onMessage(final String message) {
                    Log.d(TAG, "onMessage");
                    Log.d(TAG, "Message:" + message);
                    mHandler.post(new Runnable() {    // ----2
                        @Override
                        public void run() {
                            MakeMessage(message);
                            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(TAG, "onError");
                    ex.printStackTrace();
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "onClose");
                }
            };
            mClient.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // 送信ボタン
        Button button =  findViewById(R.id.ID_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit =  findViewById(R.id.text);
                try {
                    // 送信
                    mClient.send(id+":"+edit.getText().toString());
                } catch (NotYetConnectedException e) {
                    e.printStackTrace();
                }/* catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }
}
