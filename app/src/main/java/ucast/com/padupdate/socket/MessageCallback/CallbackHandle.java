package ucast.com.padupdate.socket.MessageCallback;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;
import ucast.com.padupdate.AppEntity;
import ucast.com.padupdate.MainActivity;
import ucast.com.padupdate.MyDialog;
import ucast.com.padupdate.UpdateService;
import ucast.com.padupdate.androidTools.ApkInfo;
import ucast.com.padupdate.androidTools.MyTools;
import ucast.com.padupdate.androidTools.SavePasswd;
import ucast.com.padupdate.androidTools.Second;
import ucast.com.padupdate.app.ExceptionApplication;
import ucast.com.padupdate.socket.Common;
import ucast.com.padupdate.socket.Message.AllFileDown;
import ucast.com.padupdate.socket.Message.ApkMessage;
import ucast.com.padupdate.socket.Message.AppDateMessage;
import ucast.com.padupdate.socket.Message.DizuoLogMessage;
import ucast.com.padupdate.socket.Message.DizuoMacMessage;
import ucast.com.padupdate.socket.Message.FileEntityMessage;
import ucast.com.padupdate.socket.Message.Heartbeat;
import ucast.com.padupdate.socket.Message.OneFileDown;
import ucast.com.padupdate.socket.Message.UpdateMessage;
import ucast.com.padupdate.socket.Message.UpdateResultMessage;
import ucast.com.padupdate.socket.Message.UploadResultMessage;

/**
 * Created by Administrator on 2016/2/4.
 */
public class CallbackHandle implements IMsgCallback {

    public static final String LOG_PATH = Environment.getExternalStorageDirectory().toString() + "/Ucast";

    @Override
    public void Receive(Channel channel, Object obj) {
        if (obj == null)
            return;
        try {

            FindHandle(channel, obj);
        } catch (Exception e) {

        }
    }

    private static final String TAG = "CallbackHandle";

    public void FindHandle(Channel channel, Object msg) {
        try {

            if (msg instanceof AppDateMessage) {
                AppDateMessage appMsg = (AppDateMessage) msg;
                String version = appMsg.version;
                //TODO 请求url判断版本号做出相应的处理 如果不是最新的版本想底座询问是否有最新的apk文件
                //TODO 返回的数据格式 @s002,version,url,size$

                String url = SavePasswd.getInstace().getIp("dizuoUpdateUrl",SavePasswd.DIZUOUPDATEURL);
                MyTools.getDizuoVersion(url, version);
            }
            if (msg instanceof ApkMessage) {
                ApkMessage apkMessage = (ApkMessage) msg;
                boolean isNew = apkMessage.isNew;
                if (!isNew) {
                    //TODO 不是最新的版本 可能需要发送底座apk的下载的url
                }
            }

            if (msg instanceof UpdateMessage) {
                UpdateMessage updateMessage = (UpdateMessage) msg;
                boolean isUpdate = updateMessage.isUpdate;
                if (isUpdate) {
                    //TODO 跳出更新提示框 返回数据类型 @s003,1/0$
                    Log.e(TAG, "FindHandle 底座更新提示");
                    UpdateService.handle.sendEmptyMessage(1);
                }
            }

            if (msg instanceof UpdateResultMessage) {
                UpdateResultMessage updateResultMessage = (UpdateResultMessage) msg;
                boolean isSucess = updateResultMessage.isSucess;

                if (isSucess) {
                    //TODO 跳出更新成功框
                    UpdateService.handle.sendEmptyMessage(2);
                } else {
                    //TODO 跳出更新失败框
                    UpdateService.handle.sendEmptyMessage(3);
                }
            }


            if (msg instanceof DizuoLogMessage) {
                DizuoLogMessage dizuoLogMessage = (DizuoLogMessage) msg;
                List<String> logs = dizuoLogMessage.logs;
                Message logsMessage = new Message();
                logsMessage.what = 301;
                logsMessage.obj = logs;
                MainActivity.handle.sendMessage(logsMessage);
            }
            if (msg instanceof UploadResultMessage) {
                UploadResultMessage uploadResultMessage = (UploadResultMessage) msg;
                boolean isUpload = uploadResultMessage.isUpload;
                if (isUpload) {
                    MainActivity.handle.sendEmptyMessage(308);
                } else {
                    MainActivity.handle.sendEmptyMessage(309);
                }
            }
            //接收文件消息
            if (msg instanceof FileEntityMessage) {
                FileEntityMessage fileEntityMessage = (FileEntityMessage) msg;
                appendMethodA(LOG_PATH + "/" + fileEntityMessage.fileName, fileEntityMessage.data);
                String resultFilemsg = "@sw002," + fileEntityMessage.fileName + "," + fileEntityMessage.number_bao + "$";
                Common.SendData(resultFilemsg.getBytes());
            }
            //某个文件已接受完
            if (msg instanceof OneFileDown){
                MainActivity.handle.sendEmptyMessage(401);
            }
            //所有文件已接收完
            if (msg instanceof AllFileDown){
                MainActivity.handle.sendEmptyMessage(402);
            }
            if (msg instanceof DizuoMacMessage){
                DizuoMacMessage dizuoMacMessage = (DizuoMacMessage) msg;
                Message macMessage = new Message();
                macMessage.what = 501;
                macMessage.obj = dizuoMacMessage.mac;
                MainActivity.handle.sendMessage(macMessage);
            }
        } catch (Exception e) {

        }
    }


    public static void appendMethodA(String fileName, String content) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.write(Common.decode(content));
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
