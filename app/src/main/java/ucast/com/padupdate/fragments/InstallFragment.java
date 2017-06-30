package ucast.com.padupdate.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ucast.com.padupdate.AppEntity;
import ucast.com.padupdate.R;
import ucast.com.padupdate.androidTools.MyTools;
import ucast.com.padupdate.app.ExceptionApplication;

import static android.content.ContentValues.TAG;

/**
 * Created by pj on 2016/11/28.
 */

@ContentView(R.layout.install)
public class InstallFragment extends Fragment implements AdapterView.OnItemClickListener {
    @ViewInject(R.id.main_lv)
    ListView lv;


    ArrayList<String> packagNameList;
    private MyReceiver receiver;
    private MyAdapter adapter;
    private ProgressDialog progressDialog;
    private boolean isYijian=false;
    ArrayList<AppEntity> installApplists;
    String isZhongyang="zy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Event(R.id.install)
    private void install(View view) {
        isYijian=true;
        for (int i = 0; i < installApplists.size(); i++) {
            detele(installApplists.get(i).getPackageName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        istall(installApplists.get(0));

    }

    @Event(R.id.check)
    private void check(View view) {
        checkIsInstall();
    }


    public void init() {
        // 监听系统新安装程序的广播
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);// 注册广播机制
        filter.addDataScheme("package"); // 必须添加这项，否则拦截不到广播
        getActivity().registerReceiver(receiver, filter);

        progressDialog = new ProgressDialog(getActivity());


        if (installApplists == null) {
            installApplists = new ArrayList<>();
        }
        installApplists.clear();

        setInstallDatas();


        adapter = new MyAdapter(getContext(),installApplists);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

        MyTools.copyCfg();
        MyTools.setRoat(getContext());
        checkIsInstall();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private void setInstallDatas() {
        //添加blueapk
        installApplists.add(new AppEntity("com.example.zxc.blue", "blue.apk", "PAD连接底座程序", R.mipmap.cuo));
        if (isZhongyang.equals("zy")){
            //添加serviceapk
            installApplists.add(new AppEntity("com.project.services", "service_2.1_5.10_zy.apk", "PAD服务程序", R.mipmap.cuo));
            //添加样例apk
            installApplists.add(new AppEntity("com.example.printtest", "sample_zy.apk", "样例程序", R.mipmap.cuo));
                //添加刷卡程序apk
            installApplists.add(new AppEntity("com.example.slotcardtest", "slotCardTest.apk", "磁卡测试", R.mipmap.cuo));
        }else {
            //添加serviceapk
            installApplists.add(new AppEntity("com.project.services", "services_tx.apk", "PAD服务程序", R.mipmap.cuo));
            //添加平板测试程序apk
            installApplists.add(new AppEntity("ucast.com.ucast_test_pad", "ucast_test_pad.apk", "平板测试程序", R.mipmap.cuo));
        }
        //添加底座监控程序apk
        installApplists.add(new AppEntity("ucast.com.dizuo_connect_test", "dizuo_connect_test_xml.apk", "底座监控程序", R.mipmap.cuo));
        //添加循环播放apk
        installApplists.add(new AppEntity("com.clov4r.android.nil", "moboPlayer.apk", "循环播放器", R.mipmap.cuo));
        //添加应用锁apk
//        installApplists.add(new AppEntity("com.ucast.applock_service", "apklock.apk", "应用锁", R.mipmap.cuo));
    }

    public void setDui(int i) {
        installApplists.get(i).setState(R.mipmap.dui);
    }

    public void setCuo(int i) {
        installApplists.get(i).setState(R.mipmap.cuo);
    }

    private void checkIsInstall() {
        initpackagNameList();

        for (int i = 0; i < installApplists.size(); i++) {
            if (packagNameList.contains(installApplists.get(i).getPackageName())) {
                setDui(i);
            } else {
                setCuo(i);
            }
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }


    private void initpackagNameList() {
        if (packagNameList != null) {
            packagNameList.clear();
            packagNameList = null;
        }
        // 初始化小模块列表
        packagNameList = new ArrayList<String>();
        PackageManager manager = getActivity().getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pI = pkgList.get(i);
            packagNameList.add(pI.packageName.toLowerCase());
        }

    }


    private void clearApk() {
        final String cachePath = Environment.getExternalStorageDirectory() + File.separator;
        // 删除file目录下的所有以安装的apk文件
        File file = new File(cachePath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".apk")) {
                f.delete();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        isYijian=false;
        //点击事件,确定是否安装
        new AlertDialog.Builder(getActivity()).setTitle("是否确认安装"+installApplists.get(position).getInstallName()+"?")
                .setPositiveButton("返回", null)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 先卸载原来的软件,再安装软件
                        detele(installApplists.get(position).getPackageName());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        istall(installApplists.get(position));
                    }

                }).create()
                .show();
    }


    public void istall(AppEntity appEntity) {
        progressDialog.setTitle(appEntity.getInstallName()+"正在安装");
        progressDialog.setMessage("请耐心等待.....");
        progressDialog.show();

        final String cachePath = Environment.getExternalStorageDirectory() + File.separator + appEntity.getApkName();
        if (MyTools.retrieveApkFromAssets(getActivity(), appEntity.getApkName(), cachePath)) {
            install(cachePath);
        }

    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            String packagName = (String) msg.obj;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("安装Handler___" + packagName + "____");

            if (msg.what == 1) {
                progressDialog.cancel();
                for (int i = 0; i < installApplists.size(); i++) {
                    if (packagName.equals(installApplists.get(i).getPackageName())) {
                        if (i < installApplists.size() - 1) {
                            if (!isYijian){
                                showToast(installApplists.get(i).getInstallName()+"安装成功");
                                checkIsInstall();
                                return;
                            }
                            istall(installApplists.get(i + 1));
                        } else {
                            showToast("安装完成");
                            // 删除file目录下的所有以安装的apk文件
                            clearApk();
                        }
                    }
                }
            } else if (msg.what == 0) {
                if (packagName == null) {
                    return;
                }
                progressDialog.cancel();
                for (int i = 0; i < installApplists.size(); i++) {
                    if (packagName.equals(installApplists.get(i).getPackageName())) {
                        if (i < installApplists.size() - 1) {
                            showToast(installApplists.get(i).getInstallName()+"已安装");
                            istall(installApplists.get(i + 1));
                        } else {
                            showToast(installApplists.get(i).getInstallName()+"已安装");
                        }
                    }
                }

            }

            checkIsInstall();


        }

        ;
    };


    public void install(String path) {
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


    private void detele(String packageName) {
        progressDialog.setTitle("正在卸载");
        progressDialog.show();
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

    private Class<?>[] getParamTypes(Class<?> cls, String mName) {
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

    public class MyPakcageInstallObserver extends android.content.pm.IPackageInstallObserver.Stub {
        public void packageInstalled(String packageName, int returnCode) {
            Message msg = Message.obtain();
            if (returnCode == 1) {
                System.out.println("安装laile___" + packageName + "____" + returnCode);
                msg.what = 1;
                msg.obj = packageName;
                handler.sendMessage(msg);
            } else {
                System.out.println("安装失败,返回码是_____" + packageName + "____" + returnCode);
                msg.what = 0;
                msg.obj = packageName;

                handler.sendMessage(msg);
            }
        }
    }

    public class MyPakcageDeleteObserver extends android.content.pm.IPackageDeleteObserver.Stub {
        public void packageDeleted(String packageName, int returnCode) throws RemoteException {
            if (returnCode == 1) {
                System.out.println("删除laile");

            } else {
                System.out.println("删除失败,返回码是");
            }
        }
    }

    /**
     * 设置广播监听
     */
    private class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packName = intent.getDataString().substring(8);
                Log.e(intent.getDataString() + "====", packName);
                // package:cn.oncomm.activity cn.oncomm.activity
                // packName为所安装的程序的包名
                packagNameList.add(packName.toLowerCase());
                clearApk();
            }
        }

    }

    public void showToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}
