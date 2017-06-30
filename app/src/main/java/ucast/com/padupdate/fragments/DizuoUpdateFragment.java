package ucast.com.padupdate.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ucast.com.padupdate.MainActivity;
import ucast.com.padupdate.R;
import ucast.com.padupdate.androidTools.SavePasswd;
import ucast.com.padupdate.app.ExceptionApplication;
import ucast.com.padupdate.socket.Common;

import static ucast.com.padupdate.androidTools.MyTools.isNetworkAvailable;

/**
 * Created by pj on 2016/11/28.
 */

@ContentView(R.layout.dizuo_update)
public class DizuoUpdateFragment extends Fragment{


    @ViewInject(R.id.dizuo_update_url)
    EditText dizuo_upload_url;
    @ViewInject(R.id.log_upload_url)
    EditText log_upload_url;
    @ViewInject(R.id.timesync_url)
    EditText timesync_url;

    @ViewInject(R.id.dizuoupdate_bt)
    Button bt;
    @ViewInject(R.id.timesync_bt)
    Button bt_time;
    @ViewInject(R.id.dizuo_mac)
    Button bt_mac;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this,inflater,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        dizuo_upload_url.setText(SavePasswd.getInstace().getIp("dizuoUpdateUrl",SavePasswd.DIZUOUPDATEURL));
        log_upload_url.setText(SavePasswd.getInstace().getIp("dizuoUploadUrl",SavePasswd.DIZUOUPLOADURL));
        timesync_url.setText(SavePasswd.getInstace().getIp("timeSyncUrl",SavePasswd.TIMESYNCURL));
        super.onResume();
    }

    @Event(R.id.dizuoupdate_bt)
    private void update(View view){
        String dizuouploadurl=dizuo_upload_url.getText().toString().trim();
        String loguploadurl=log_upload_url.getText().toString().trim();

        SavePasswd.getInstace().save("dizuoUpdateUrl",dizuouploadurl);
        SavePasswd.getInstace().save("dizuoUploadUrl",loguploadurl);

        String str = "@s001,1$";
        Common.SendData(str.getBytes());
    }
    @Event(R.id.timesync_bt)
    private void syncTime(View view){
        String timeSyncUrl=timesync_url.getText().toString().trim();

        SavePasswd.getInstace().save("timeSyncUrl",timeSyncUrl);

        getSystemTime(timeSyncUrl.trim());
    }
    @Event(R.id.dizuo_mac)
    private void getDizuoMac(View view){
        String str = "@getmac$";
        Common.SendData(str.getBytes());
    }
    public void getSystemTime(String url){
        if (!isNetworkAvailable(ExceptionApplication.getInstance())) {
            Toast.makeText(getContext(),"网络不可用，请检查连接完成后重新同步！",Toast.LENGTH_LONG).show();
            return;
        }
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String time=result.replace("\"","").trim();
                Log.e("DizuoUpdateFragment", "onSuccess: " +time);
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
