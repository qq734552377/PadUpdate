package ucast.com.padupdate.socket.MessageProtocol;


import android.util.Log;

import io.netty.channel.Channel;
import ucast.com.padupdate.socket.Message.AllFileDown;
import ucast.com.padupdate.socket.Message.ApkMessage;
import ucast.com.padupdate.socket.Message.AppDateMessage;
import ucast.com.padupdate.socket.Message.DizuoLogMessage;
import ucast.com.padupdate.socket.Message.DizuoMacMessage;
import ucast.com.padupdate.socket.Message.FileEntityMessage;
import ucast.com.padupdate.socket.Message.MessageBase;
import ucast.com.padupdate.socket.Message.OneFileDown;
import ucast.com.padupdate.socket.Message.UpdateMessage;
import ucast.com.padupdate.socket.Message.UpdateResultMessage;
import ucast.com.padupdate.socket.Message.UploadResultMessage;
import ucast.com.padupdate.socket.TimerConnect.WhileCheckClient;

/**
 * Created by Administrator on 2016/2/3.
 */
public class StationPackage extends Package {

    private StringBuffer sBuffer;

    public StationPackage(Channel _channel) {
        super(_channel);
        sBuffer = new StringBuffer();
    }

    @Override
    public void Import(byte[] buffer, int Offset, int count) throws Exception {
        sBuffer.append(new String(buffer));
        int offset = 0;
        while (sBuffer.length() > offset && !mDispose) {
            int startIndex = sBuffer.indexOf("@", offset);
            if (startIndex == -1)
                break;
            int endIndex = sBuffer.indexOf("$", startIndex);
            if (endIndex == -1)
                break;
            int len = endIndex + 1;
            String value = sBuffer.substring(startIndex, len);
            OnMessageDataReader(value);
            offset = len;
        }
        sBuffer.delete(0, offset);
    }

    @Override
    public MessageBase MessageRead(byte[] data) {
        return null;
    }

    private static final String TAG = "StationPackage";
    public MessageBase MessageRead(String value) throws Exception {
        try {
            String msg = value.substring(1, value.length() - 1);
            String[] item = msg.split(",");
            MessageBase mbase = null;
            switch (item[0]) {
                //TODO 解析协议
                case "p001": //包含app的版本号
                    Log.e(TAG, "MessageRead  p001");
                    mbase=new AppDateMessage();
                    break;
                case "p002"://暂时没用到
                    Log.e(TAG, "MessageRead  p002");
                    mbase=new ApkMessage();
                    break;
                case "p003"://提示是否更新的提示框
                    Log.e(TAG, "MessageRead  p003");
                    mbase=new UpdateMessage();
                    break;
                case "p004"://底座app更新的结果
                    Log.e(TAG, "MessageRead  p004");
                    mbase=new UpdateResultMessage();
                    break;
                case"p005"://底座log文件目录
                    mbase=new DizuoLogMessage();
                    break;
                case "p006"://底座上传文件的结果
                    mbase=new UploadResultMessage();
                    break;
                case "pw001"://接收文件
                    mbase=new FileEntityMessage();
                    Log.e(TAG, "MessageRead: 底座传上来的文件" );
                    break;
                case "pw002"://某个文件已接受完
                    mbase=new OneFileDown();
                    Log.e(TAG, "MessageRead: 某个文件已接受完" );
                    break;
                case "pw003"://所有文件已接收完
                    mbase=new AllFileDown();
                    Log.e(TAG, "MessageRead: 所有文件已接收完" );
                    break;

                case "mac"://所有文件已接收完
                    mbase=new DizuoMacMessage();
                    break;
                default:
                    break;

            }
            WhileCheckClient.HeartbeatTimeUpdate();
            if (mbase == null)
                return null;
            mbase.Load(item);
            return mbase;
        } catch (Exception e) {
            return null;
        }
    }

}
