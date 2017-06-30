package ucast.com.padupdate.androidTools;

import android.content.Context;
import android.content.SharedPreferences;

import ucast.com.padupdate.app.ExceptionApplication;

/**
 * Created by Administrator on 2016/6/12.
 */
public class SavePasswd {

    private static SavePasswd savePasswd=new SavePasswd();

    public static final String   DIZUOUPDATEURL="http://130.180.2.100:7799/APK/GetVersionFile?merchant=zy&device=dizuo";
    public static final String   DIZUOUPLOADURL="http://130.180.2.100:7799/APK/LogFilePost?merchant=zy&device=dizuo";
    public static final String   TIMESYNCURL="http://130.180.2.100:12500";


    private SavePasswd(){
        save("原始密码","YL230220");
        if(get("新密码").equals("ucast_test")){
            save("新密码","YL230220");
        }
    };
    public static SavePasswd getInstace(){
        return savePasswd;
    }

    public void save (String name , String passwd){
        SharedPreferences sp= ExceptionApplication
                .context.getSharedPreferences("passwd", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(name,passwd);
        editor.commit();
    }
    public String get(String name){
        SharedPreferences sp=ExceptionApplication
                .context.getSharedPreferences("passwd", Context.MODE_PRIVATE);

        return sp.getString(name,"ucast_test");
    }
    public String getIp(String name, String staticIp){
        SharedPreferences sp=ExceptionApplication
                .context.getSharedPreferences("passwd", Context.MODE_PRIVATE);

        return sp.getString(name,staticIp);
    }

}
