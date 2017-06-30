package ucast.com.padupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ucast.com.padupdate.androidTools.SavePasswd;
import ucast.com.padupdate.socket.TimerConnect.WhileCheckClient;

/**
 * Created by pj on 2016/11/21.
 */
public class UpdateBroadCast extends BroadcastReceiver {
    public static final String ConnectStr = "com.example.zxc.blue.tcpConnect";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectStr)) {
            Log.e("UpdateBroadCast", "onReceive  收到连接指示");
            final String info = intent.getStringExtra("IP");
            if (info == null || info == "")
                return;
            final String ssid = intent.getStringExtra("SSID");
            final String password = intent.getStringExtra("PASSWORD");

            SavePasswd.getInstace().save("ip",info);

            String port = intent.getStringExtra("PORT");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WhileCheckClient.Run(ssid, password, info);
                }
            }).start();


        }
    }
}
