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
    public final void MakeMessage(final String message_log)//TextViewを動的に生成する関数
    {
        LinearLayout linearLayout=findViewById(R.id.layout);
        TextView textView=new TextView(this);
        String name=message_log.substring(0,message_log.indexOf(":"));//サーバから受信したIDのみ取得
        String message=message_log.substring(message_log.indexOf(":")+1,message_log.length());//入力された文字列のみ取得
        textView.setText(message);
        textView.setWidth(100);
        textView.setHeight(50*((message.length()/16)+1));
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(name.equals(id))
        {
            param.setMargins(600,0,0,0);//IDが自分のものであれば位置を調整
        }
        linearLayout.addView(textView,param);//テキスト追加
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIDconfig();
    }
    public void setIDconfig()//IDを入力させる画面の表示
    {
        setContentView(R.layout.activity_idconfig);
        Button id_btn=findViewById(R.id.ID_btn);
        final EditText id_edit=findViewById(R.id.IDconfig_edit);
        id_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                id=id_edit.getText().toString();//入力したIDを文字列として取得
                setMainWindow();//画面遷移
            }
        });
    }
    public void setMainWindow()//チャットさせる画面
    {
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        if ("sdk".equals(Build.PRODUCT)) {
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }
        try {
            URI uri = new URI("ws://192.168.0.23:8888");//サーバーのIPアドレス、ポート番号
            mClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "onOpen");
                }

                @Override
                public void onMessage(final String message) {//サーバからのデータ受信
                    Log.d(TAG, "onMessage");
                    Log.d(TAG, "Message:" + message);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MakeMessage(message);
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
            mClient.connect();//サーバーに接続

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Button button =  findViewById(R.id.ID_btn);//送信用のボタン
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//クリックされたらIDと入力された文字列を送信
                EditText edit =  findViewById(R.id.text);//文字入力するところ
                try {
                    mClient.send(id+":"+edit.getText().toString());//データ送信
                } catch (NotYetConnectedException e) {
                    e.printStackTrace();
                }/* catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }
}
