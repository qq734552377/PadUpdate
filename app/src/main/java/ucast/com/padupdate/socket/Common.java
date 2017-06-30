package ucast.com.padupdate.socket;

import android.content.Intent;
import android.util.Base64;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import ucast.com.padupdate.socket.Memory.NettyChannelMap;
import ucast.com.padupdate.socket.Memory.NettyClientMap;

/**
 * Created by Administrator on 2016/2/16.
 */
public class Common {

    public static String ConfigKey = "ServiceClient";

    public static String DeviceNumber = "185632126";

    public static String SerialName = "ttyS2";
    public static Object CheckFirstStatus = new Object();
    private static boolean  FirstStatus=false;

    public static Map<String, String> ssid_Password_Map = new HashMap<>();

    public static String encode(byte[] bstr) {
        return Base64.encodeToString(bstr, Base64.DEFAULT);
    }


    /**
     * 解码
     *
     * @param str
     * @return string
     */
    public static byte[] decode(String str) {
        try {
            return Base64.decode(str, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    public static void ChangeFirstStatus() {
        synchronized (Common.CheckFirstStatus) {
            Common.FirstStatus = true;
        }
    }


    public static boolean SendData(byte[] Data) {
        NioTcpClient client = NettyClientMap.GetChannel(Common.ConfigKey);
        if (client == null)
            return false;
        return client.Send(Data);
    }

    public static boolean SendDataHead(byte[] Data) {
        NioTcpClient client = NettyClientMap.GetChannel(Common.ConfigKey);
        if (client == null)
            return false;
        return client.Send(Data);
    }

    public static void ServicesAllSend(byte[] Data) {
        Set set = NettyChannelMap.ToList();
        for (Iterator iter = set.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            Channel value = (Channel) entry.getValue();
            if (value == null)
                return;
            ByteBuf resp = Unpooled.copiedBuffer(Data);
            value.writeAndFlush(resp);
//            Log.e("calm", "---2201---------" + value.id());
        }
    }

    public static void SendChannelData(Channel channel) {
        byte[] data = Common.GetFormat("2307", 1, 1, new String[]{"1"});
        ByteBuf resp = Unpooled.copiedBuffer(data);
        channel.writeAndFlush(resp);
    }

    public static void ChannelSendBuffer(Channel channel, byte[] buffer) {
        if (channel == null)
            return;
        ByteBuf resp = Unpooled.copiedBuffer(buffer);
        channel.writeAndFlush(resp);
    }



    //发送底座
    public static byte[] GetFormat(String cmd, String type, int total, int current, String[] data) {
        StringBuffer sb = new StringBuffer();
        sb.append("@");
        sb.append(cmd);
        sb.append("," + type);
        sb.append("," + DeviceNumber);
        sb.append("," + total);
        sb.append("," + current);
        for (int i = 0; i < data.length; i++) {
            sb.append("," + data[i]);
            if (i + 1 >= data.length) {
                sb.append("$");
            }
        }
        return sb.toString().getBytes();
    }

    public static byte[] GetFormatTransparent(String cmd, String type, int total, int current, String data) {
        StringBuffer sb = new StringBuffer();
        sb.append("@");
        sb.append(cmd);
        sb.append("," + type);
        sb.append("," + DeviceNumber);
        sb.append("," + total);
        sb.append("," + current);
        sb.append(",/dev/ttyS3");
        sb.append("," + data.length());
        sb.append("," + data);
        sb.append("$");
        return sb.toString().getBytes();
    }

    //发送App
    public static byte[] GetFormat(String cmd, int total, int current, String[] data) {
        StringBuffer sb = new StringBuffer();
        sb.append("@");
        sb.append(cmd);
        sb.append("," + total);
        sb.append("," + current);
        for (int i = 0; i < data.length; i++) {
            sb.append("," + data[i]);
            if (i + 1 >= data.length) {
                sb.append("$");
            }
        }
        return sb.toString().getBytes();
    }

    public static int Xor(byte[] buffer, int start) {
        int value = 0;
        for (int i = start; i < buffer.length - 1; i++) {
            value ^= buffer[i];
        }
        return value;
    }

}
