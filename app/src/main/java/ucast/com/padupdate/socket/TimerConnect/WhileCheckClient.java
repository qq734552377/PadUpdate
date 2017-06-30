package ucast.com.padupdate.socket.TimerConnect;

import android.util.Log;



import java.util.Timer;
import java.util.TimerTask;

import ucast.com.padupdate.mytime.MyTimeTask;
import ucast.com.padupdate.mytime.MyTimer;
import ucast.com.padupdate.socket.Common;
import ucast.com.padupdate.socket.Memory.NettyClientMap;
import ucast.com.padupdate.socket.NioTcpClient;

/**
 * Created by Administrator on 2016/4/21.
 */
public class WhileCheckClient {

    private static Object obj = new Object();

    private static MyTimer timer;

    private static int TotalNumber = 1;

    private static long heartTime;

    public static void StartTimer() {
        timer = new MyTimer(new MyTimeTask(new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    NioTcpClient client = NettyClientMap.GetChannel(Common.ConfigKey);
                    if (client == null)
                        return;
                    long second = (System.currentTimeMillis() - heartTime) / 1000;
                    if (second >= 12) {
                        client.Dispose();
                    } else {
                        boolean result = WhetherCconnect(client);
                        if (result)
                            return;
                    }
                    NettyClientMap.Remove(Common.ConfigKey);
//                    if (TotalNumber > 2) {
//                        Common.SendStop(client.SSid);
//                        return;
//                    }
                    TotalNumber++;
                    ClinetRun(client.SSid, client.Password, client.Ip, false);
                }
            }

        }),3000L,3000L);
        timer.initMyTimer().startMyTimer();
    }


    public static void HeartbeatTimeUpdate() {
        synchronized (obj) {
            heartTime = System.currentTimeMillis();
            System.out.println("------心跳发送--回来了------");
        }
    }

    public static void UpTotalNumber() {
        synchronized (obj) {
            TotalNumber = 1;
        }
    }

    private static boolean WhetherCconnect(NioTcpClient client) {
        if (client.WaitChannel == 0) {
            return true;
        } else if (client.WaitChannel == 1) {
            boolean success = client.f.isSuccess();
            SendHead();
            System.out.println("------心跳发送--------");
            return true;
        }
        Log.e("calm", "Heartbeat send connection error " + client.Ip + " Ssid:" + client.SSid);
        return false;
    }

    public static void Run(String ssid, String password, String ip) {
        synchronized (obj) {
            NioTcpClient client = NettyClientMap.GetChannel(Common.ConfigKey);
            if (client == null) {
                TotalNumber = 1;
                ClinetRun(ssid, password, ip, true);
                return;
            }
            if (client.WaitChannel == 2 || client.WaitChannel == 0) {
                client.Dispose();
                Log.e("calm", "Being and connection error " + ip + " Ssid:" + ssid);
                NettyClientMap.Remove(Common.ConfigKey);
                TotalNumber = 1;
                ClinetRun(ssid, password, ip, true);
                return;
            }
            if (ssid.equals(client.SSid) && ip.equals(client.Ip)) {
                if (client.Old) {
                    Log.e("calm", "Old Connection close " + ip + " Ssid:" + ssid);
                    return;
                }

                //TODO 9.14改
//                Common.SendUpdate();
                return;
            }
            client.Dispose();
            Log.e("calm", "Connection close " + ip + " Ssid:" + ssid);
            NettyClientMap.Remove(Common.ConfigKey);
            TotalNumber = 1;
            ClinetRun(ssid, password, ip, true);

        }
    }

    private static void ClinetRun(String ssid, String password, String ip, boolean old) {
        NioTcpClient clientFor = new NioTcpClient(ssid, password, ip, 43700, old);
        heartTime = System.currentTimeMillis();
        NettyClientMap.Add(clientFor);
        new Thread(clientFor).start();
    }

    private static void SendHead() {
        String heart = "@1105,123456789$";
        Common.SendDataHead(heart.getBytes());
    }
}
