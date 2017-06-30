package ucast.com.padupdate;

import android.app.Dialog;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.LoginFilter;
import android.util.Log;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import ucast.com.padupdate.androidTools.AppInfo;
import ucast.com.padupdate.androidTools.AppManager;
import ucast.com.padupdate.androidTools.SavePasswd;
import ucast.com.padupdate.app.ExceptionApplication;
import ucast.com.padupdate.mytime.MyTimeTask;
import ucast.com.padupdate.mytime.MyTimer;
import ucast.com.padupdate.socket.Common;
import ucast.com.padupdate.socket.Memory.NettyClientMap;
import ucast.com.padupdate.socket.NioTcpClient;
import ucast.com.padupdate.socket.TimerConnect.WhileCheckClient;
import java.util.Date;

import static ucast.com.padupdate.androidTools.MyTools.isNetworkAvailable;

/**
 * Created by pj on 2016/11/21.
 */
public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return this.START_STICKY;
    }

    @Override
    public void onCreate() {

        startForeground(0, new Notification());

        //TODO 连接底座服务

        super.onCreate();

        WhileCheckClient.StartTimer();

        startTimer();

    }

    public static Dialog dialog = MyDialog.showIsUpdate("底座有新版本,是否更新?");


    public static Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dialog.show();
                    break;
                case 2:
                    Dialog dialog_dizuo_sucess = MyDialog.showUpdateResult("底座更新成功了,已经是最新版本了!");
                    dialog_dizuo_sucess.show();
                    break;
                case 3:
                    Dialog dialog_dizuo_default = MyDialog.showUpdateResult("更新失败,可以通过Pad上的程序重新更新");
                    dialog_dizuo_default.show();
                    break;
                case 201://pad更新成功
                    String packageName= (String) msg.obj;
                    AppInfo appInfo=new AppManager(ExceptionApplication.getInstance().getPackageManager()).getAppInfoByPackageName(packageName);
                    Dialog dialog_pad_success=MyDialog.showPadUpdateSuccess(appInfo);
                    dialog_pad_success.show();
                    break;
                case 202://pad更新失败
                    Dialog dialog_pad_default=MyDialog.showUpdateResult("更新失败,可以通过平板更新程序手动更新!");
                    dialog_pad_default.show();
                    break;

            }
        }
    };

    /**
     * 当服务被杀死时重启服务
     * */
    public void onDestroy() {
        stopForeground(true);
        Intent localIntent = new Intent();
        localIntent.setClass(this, UpdateService.class);
        this.startService(localIntent);    //销毁时重新启动Service
    }

    public MyTimer timer;
    public void startTimer() {
        timer = new MyTimer(new MyTimeTask(new Runnable() {
            @Override
            public void run() {
                String url= SavePasswd.getInstace().getIp("timeSyncUrl",SavePasswd.TIMESYNCURL);
                getSystemTime(url.trim());
            }
        }), 1000*60*2L, 1000*30*60L);
        timer.initMyTimer().startMyTimer();
    }

    private static final String TAG = "UpdateService";
    public void getSystemTime(String url){
        if (!isNetworkAvailable(ExceptionApplication.getInstance())) {
            return;
        }
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String time=result.replace("\"","").trim();
                Log.e(TAG, "onSuccess: " +time);
                setTime(time);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }




    public void setTime(String mytime){
        Date mydate=StringToDate(mytime);
        long curMs=mydate.getTime();
        boolean isSuc = SystemClock.setCurrentTimeMillis(curMs);//需要Root权限
        Log.e(TAG, "setTime: "+isSuc );
    }
    private Date StringToDate(String s){
        Date time=null;
        SimpleDateFormat sd=new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            time=sd.parse(s);
        } catch (java.text.ParseException e) {
            System.out.println("输入的日期格式有误！");
            e.printStackTrace();
        }
        return time;
    }

}
