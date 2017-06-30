package ucast.com.padupdate.androidTools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import ucast.com.padupdate.AppEntity;
import ucast.com.padupdate.MainActivity;
import ucast.com.padupdate.MyDialog;
import ucast.com.padupdate.UpdateService;
import ucast.com.padupdate.app.ExceptionApplication;
import ucast.com.padupdate.socket.Common;

import static android.content.ContentValues.TAG;
import static ucast.com.padupdate.UpdateService.dialog;


/**
 * Created by pj on 2016/11/23.
 */
public class MyTools {
    private static ProgressDialog progressDialog;

    public static Dialog padAutoupdateDialog;

    public static void getPadVersion_AutoUpdate(final AppInfo info) {
        if (!isNetworkAvailable(ExceptionApplication.getInstance())) {
            return;
        }
        RequestParams params = new RequestParams(info.getUrl());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("onSuccess", "onSuccess   " + s);
                String[] str = s.split(",");

                String new_version = str[0].replace("\"", "").trim();
                long new_size = Long.parseLong(str[1].trim());
                String url = str[2].replace("\"", "").trim();
                Log.e("", "onSuccess " + new_version + "  " + new_size + "   " + url);

                String app_version = AppInfo.getVersionName(ExceptionApplication.getInstance(), info.getPackageName());

                if (!app_version.equals(new_version)) {
                    long apk_size = 0;
                    String path_base = Environment.getExternalStorageDirectory().toString() + "/" + info.getAppName()
                            + ".apk";
                    try {
                        apk_size = MyTools.getFileSizes(path_base);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String apk_version = MyTools.getApkversion(path_base);
                    Log.e("", "onSuccess1  " + path_base + " " + apk_version + "   " + new_version);
                    if (new_size == apk_size) {
                        if (apk_version.equals(new_version)) {
                            //TODO 跳出是否升级平板
                            if (padAutoupdateDialog != null) {
                                Log.e(TAG, "onSuccess: 消失啊");
                                padAutoupdateDialog.dismiss();
                            }
                            String packageName = MyTools.getApkPackageName(path_base);
                            AppInfo appInfo = new AppManager(ExceptionApplication.getInstance().getPackageManager())
                                    .getAppInfoByPackageName(packageName);
                            Log.e("", "onSuccess1 开始升级");
                            padAutoupdateDialog = MyDialog.showPadIsUpdate(appInfo);
                            padAutoupdateDialog.show();
                        } else {
                            //版本不对重新下载
                            Log.e("", "onSuccess1 版本不对重新下载" + apk_version + "  " + new_version);
                            MyTools.downloadFile(url, path_base);
                        }
                    } else {
                        Log.e("", "onSuccess1 开始下载");
                        MyTools.downloadFile(url, path_base);
                    }
                } else {
//                    Dialog dialog = MyDialog.showUpdateResult("pad已经是最新版本了");
//                    dialog.show();
                }


            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static void getPadVersion(final AppInfo info) {
        if (!isNetworkAvailable(ExceptionApplication.getInstance())) {
            return;
        }
        RequestParams params = new RequestParams(info.getUrl());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("onSuccess", "onSuccess   " + s);
                String[] str = s.split(",");

                String new_version = str[0].replace("\"", "").trim();
                long new_size = Long.parseLong(str[1].trim());
                String url = str[2].replace("\"", "").trim();
                Log.e("", "onSuccess " + new_version + "  " + new_size + "   " + url);

                String app_version = AppInfo.getVersionName(ExceptionApplication.getInstance(), info.getPackageName());

                if (!app_version.equals(new_version)) {
                    long apk_size = 0;
                    String path_base = Environment.getExternalStorageDirectory().toString() + "/" + info.getAppName()
                            + ".apk";
                    try {
                        apk_size = MyTools.getFileSizes(path_base);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String apk_version = MyTools.getApkversion(path_base);
                    Log.e("", "onSuccess2  " + path_base + " " + apk_version + "   " + new_version);
                    if (new_size == apk_size) {
                        if (apk_version.equals(new_version)) {
                            //TODO 直接升级
                            MyTools.detele(info.getPackageName());
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            MyTools.install(Environment.getExternalStorageDirectory().toString() + "/" + info
                                    .getAppName() + ".apk");
                            Log.e("", "onSuccess2 直接升级");
                        } else {
                            //版本不对重新下载
                            Log.e("", "onSuccess2 版本不对重新下载");
                            MyTools.downloadFile(url, path_base);
                        }
                    } else {
                        Log.e("", "onSuccess2 开始下载");
                        MyTools.downloadFile(url, path_base);
                    }
                } else {
                    Log.e("", "onSuccess2 pad已经是最新版本了");
                    Dialog dialog = MyDialog.showUpdateResult("已经是最新版本了");
                    dialog.show();
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    dialog.dismiss();
                }


            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    public static void getDizuoVersion(String url, final String dizuo_version) {
        if (!isNetworkAvailable(ExceptionApplication.getInstance())) {
            return;
        }
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("onSuccess", "onSuccess   " + s);
                String[] strs = s.split(",");

                String new_version = strs[0].replace("\"", "").trim();
                long new_size = Long.parseLong(strs[1].trim());
                String url = strs[2].replace("\"", "").trim();
                if (!new_version.equals(dizuo_version)) {
                    String str = "@s002," + new_version + "," + new_size + "," + url + "$";
                    Common.SendData(str.getBytes());
                } else {
                    Dialog dialog = MyDialog.showUpdateResult("底座已经是最新版本了");
                    dialog.show();

                    List<AppInfo> infos = new AppManager(ExceptionApplication.getInstance().getPackageManager())
                            .getUcastApp();
                    for (AppInfo a : infos) {
                        MyTools.getPadVersion_AutoUpdate(a);
                    }

                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
    public static Dialog firstDownDialog;
    public static void downloadFile(final String url, final String path) {
        if (!isNetworkAvailable(ExceptionApplication.getInstance())) {
            return;
        }

        progressDialog = new ProgressDialog(ExceptionApplication.getInstance());
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("亲，努力下载中。。。");
                progressDialog.show();
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) current);

                Log.e("", "onLoading toeal>>" + total + "   cuurrent >>" + current);

            }

            @Override
            public void onSuccess(File result) {
                if (firstDownDialog!=null){
                    firstDownDialog.dismiss();
                }
                String packageName = MyTools.getApkPackageName(path);
                AppInfo appInfo = new AppManager(ExceptionApplication.getInstance().getPackageManager())
                        .getAppInfoByPackageName(packageName);
                firstDownDialog = MyDialog.showPadIsUpdate(appInfo);
                firstDownDialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

    public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /***
     * 获取文件大小
     ***/
    public static long getFileSizes(String apkPath) throws Exception {
        File f = new File(apkPath);
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    /***
     * 获取apk文件的的版本号
     ***/
    public static String getApkversion(String path) {

        File f = new File(path);
        if (!f.exists()) {
            return "0.0";
        }
        PackageInfo packageInfo;
        try {
            PackageManager packageManager = ExceptionApplication.getInstance().getPackageManager();
            packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            return "1.0";
        }
        return packageInfo == null ? "0.0" : packageInfo.versionName;
    }

    public static String getApkPackageName(String path) {

        File f = new File(path);
        if (!f.exists()) {
            return "0.0";
        }
        PackageInfo packageInfo;
        try {
            PackageManager packageManager = ExceptionApplication.getInstance().getPackageManager();
            packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            return "1.0";
        }
        return packageInfo == null ? "0.0" : packageInfo.packageName;
    }

    /**
     * 静默安装apk文件
     */
    public static void install(String path) {
        try {
            Uri uri = Uri.fromFile(new File(path));
            PackageManager pm = ExceptionApplication.getInstance().getPackageManager();
            MyPakcageInstallObserver observer = new MyPakcageInstallObserver();
            //获取方法中的参数类型列表
            Class<?> param[] = getParamTypes(pm.getClass(), "installPackage");
            //获取方法
            Method method = pm.getClass().getDeclaredMethod("installPackage", param);
            //方法的调用
            method.invoke(pm, uri, observer, 0, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 静默s删除apk文件
     */
    public static void detele(String packageName) {
        try {
            PackageManager pm = ExceptionApplication.getInstance().getPackageManager();
            MyPakcageDeleteObserver observer = new MyPakcageDeleteObserver();
            Class<?> param[] = getParamTypes(pm.getClass(), "deletePackage");
            Method method = pm.getClass().getDeclaredMethod("deletePackage", param);//
            method.invoke(pm, packageName, observer, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static Class<?>[] getParamTypes(Class<?> cls, String mName) {
        Class<?> cs[] = null;
        Method[] mtd = cls.getMethods();
        for (int i = 0; i < mtd.length; i++) {
            if (!mtd[i].getName().equals(mName)) {
                continue;
            }
            cs = mtd[i].getParameterTypes();
        }
        return cs;
    }


    public static class MyPakcageDeleteObserver extends android.content.pm.IPackageDeleteObserver.Stub {
        public void packageDeleted(String packageName, int returnCode) throws RemoteException {
            if (returnCode == 1) {
                System.out.println("删除laile");

            } else {
                System.out.println("删除失败,返回码是");

            }
        }
    }


    public static class MyPakcageInstallObserver extends android.content.pm.IPackageInstallObserver.Stub {
        public void packageInstalled(String packageName, int returnCode) {
            if (returnCode == 1) {
                System.out.println("安装laile___" + packageName + "____" + returnCode);
                //需要发送hangdle
                Message message = new Message();
                message.obj = packageName;
                message.what = 201;
                UpdateService.handle.sendMessage(message);


            } else {
                System.out.println("安装失败,返回码是_____" + packageName + "____" + returnCode);
                Message message = new Message();
                message.obj = packageName;
                message.what = 202;
                UpdateService.handle.sendMessage(message);

            }
        }
    }
    //将屏幕旋转锁定
    public static int setRoat(Context context){
        Settings.System.putInt(context.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
		//得到是否开启
        int flag = Settings.System.getInt(context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0);
        return  flag;
    }


    //将assets目录下的cfg.xml拷入到SD卡中
    public static void copyCfg() {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/cfg.xml";
        FileOutputStream os = null;
        InputStream is = null;
        int len = -1;
        try {
            is =  ExceptionApplication.getInstance().getClass().getClassLoader().getResourceAsStream("assets/cfg.xml");
            os = new FileOutputStream(dirPath);
            byte b[] = new byte[1024];

            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }

            is.close();
            os.close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "copyCfg: 写入失败");
        }
    }

    // 将文件从assets目录拷入到SD卡中
    public static boolean retrieveApkFromAssets(Context context, String fileName, String path) {
        boolean bRet = false;

        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            } else {
                file.createNewFile();
                InputStream is = context.getClass().getClassLoader().getResourceAsStream("assets/" + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] temp = new byte[1024];
                int i = 0;
                while ((i = is.read(temp)) != -1) {
                    fos.write(temp, 0, i);
                }
                fos.flush();
                fos.close();
                is.close();
                bRet = true;
            }
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(e.getMessage());
            builder.show();
            e.printStackTrace();
        }
        return bRet;
    }

    //设置为中文环境
    public  static boolean SettingLanguage() {
        try {
            Class amnClass = Class.forName("android.app.ActivityManagerNative");
            Method methodGetDefault = amnClass.getMethod("getDefault");
            Object amn = methodGetDefault.invoke(amnClass);
            Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
            Configuration config = (Configuration) methodGetConfiguration.invoke(amn);
            Class configClass = config.getClass();
            Field f = configClass.getField("userSetLocale");
            f.setBoolean(config, true);
//            if(config.locale==Locale.CHINA){
//            	return false;
//            }
            config.locale = Locale.CHINA;
            Method methodUpdateConfiguration = amnClass.getMethod("updateConfiguration", Configuration.class);
            methodUpdateConfiguration.invoke(amn, config);
            BackupManager.dataChanged("com.android.providers.settings");
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;

    }
    /***
     * 获取内核版本号
     */
    public static String  getLinuxKernalInfo() {
        Process process = null;
        String mLinuxKernal = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
                // result += "\n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.toString();
    }

}
