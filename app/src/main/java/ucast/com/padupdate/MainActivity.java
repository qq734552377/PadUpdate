package ucast.com.padupdate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


import java.util.List;

import ucast.com.padupdate.androidTools.AppInfo;
import ucast.com.padupdate.androidTools.AppManager;
import ucast.com.padupdate.androidTools.MyTools;
import ucast.com.padupdate.app.ExceptionApplication;
import ucast.com.padupdate.fragments.DizuoUpdateFragment;
import ucast.com.padupdate.fragments.InstallFragment;
import ucast.com.padupdate.fragments.PadUpdateFragment;
import ucast.com.padupdate.fragments.SettingFragment;
import ucast.com.padupdate.fragments.UploadFragment;
import ucast.com.padupdate.socket.Common;
import ucast.com.padupdate.socket.TimerConnect.WhileCheckClient;

@ContentView(R.layout.main)
public class MainActivity extends FragmentActivity {
    @ViewInject(R.id.edt)
    EditText editText;


    private FragmentManager fm;
    private static UploadFragment uploadFragment = null;
    private Fragment settingFragment;
    private Fragment dizuoUpdateFragment;
    private static PadUpdateFragment padUpdateFragment;
    private Fragment installFragment;
    int oldId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        MyTools.setRoat(this);
        MyTools.SettingLanguage();
        fm = getSupportFragmentManager();


        Intent ootStartIntent = new Intent(this, UpdateService.class);
        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startService(ootStartIntent);


        uploadFragment = new UploadFragment();
        settingFragment = new SettingFragment();
        dizuoUpdateFragment = new DizuoUpdateFragment();
        padUpdateFragment = new PadUpdateFragment();
        installFragment = new InstallFragment();


    }

    @Event(R.id.pad_update)
    private void pad_update(View view) {
        if (oldId == view.getId()) {
            return;
        }
        oldId = view.getId();
        if (padUpdateFragment != null) {
            setFragment(padUpdateFragment);
        }
    }

    @Event(R.id.dizuo_update)
    private void dizuo_update(View view) {
        if (oldId == view.getId()) {
            return;
        }
        oldId = view.getId();
        if (dizuoUpdateFragment != null) {
            setFragment(dizuoUpdateFragment);
        }
    }

    @Event(R.id.setting)
    private void setting(View view) {
        if (oldId == view.getId()) {
            return;
        }
        oldId = view.getId();
        if (settingFragment != null) {
            setFragment(settingFragment);
        }
    }

    @Event(R.id.upload)
    private void upload(View view) {
        if (oldId == view.getId()) {
            return;
        }
        oldId = view.getId();
        if (uploadFragment != null) {
            setFragment(uploadFragment);
        }
    }

    @Event(R.id.install)
    private void install(View view) {
        if (oldId == view.getId()) {
            return;
        }
        oldId = view.getId();
        if (installFragment != null) {
            setFragment(installFragment);
        }
    }

    public void setFragment(android.support.v4.app.Fragment fragment) {
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
        fm.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content, fragment)
                .commit();
    }


    @Event(R.id.button)
    private void connect(View view) {
        String info = editText.getText().toString().trim();
        WhileCheckClient.Run("Tenda_Ucast", "123456", info);
    }

    @Event(R.id.button2)
    private void tishiGengxin(View view) {
        Dialog dialog = MyDialog.showIsUpdate("底座有新版本,是否更新?");
        dialog.show();
    }

    @Event(R.id.button3)
    private void tishiGengxinResult(View view) {
        Dialog dialog = MyDialog.showUpdateResult("更新成功?");
        dialog.show();
    }

    @Event(R.id.button4)
    private void querry(View view) {
        String str = "@s001,1$";
        Common.SendData(str.getBytes());
    }

    @Event(R.id.exit)
    private void exit(View view) {
        this.finish();
    }
    public static Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Dialog dialog = MyDialog.showIsUpdate("底座有新版本,是否更新?");
                    dialog.show();
                    break;
                case 2:
                    Dialog dialog_dizuo_sucess = MyDialog.showUpdateResult("底座更新成功了,已经是最新版本了!");
                    dialog_dizuo_sucess.show();
                    break;
                case 3:
                    Dialog dialog_dizuo_default = MyDialog.showUpdateResult("底座更新失败,可以通过Pad上的程序重新更新");
                    dialog_dizuo_default.show();
                    break;
                case 201://pad更新成功
                    String packageName = (String) msg.obj;
                    AppInfo appInfo = new AppManager(ExceptionApplication.getInstance().getPackageManager())
                            .getAppInfoByPackageName(packageName);
                    Dialog dialog_pad_success = MyDialog.showPadUpdateSuccess(appInfo);
                    dialog_pad_success.show();
                    break;

                case 202://pad更新失败
                    Dialog dialog_pad_default = MyDialog.showUpdateResult("更新失败,可以通过平板更新程序手动更新!");
                    dialog_pad_default.show();
                    break;
                case 301://更新Upload界面的UI
                    if (uploadFragment != null) {
                        List<String> logs = (List<String>) msg.obj;
                        uploadFragment.setDizuoLogs(logs);
                    }
                    break;
                case 308:
                    if (uploadFragment != null) {
                        uploadFragment.showToast("底座日志上传成功");
                    }
                    break;
                case 309:
                    if (uploadFragment != null) {
                        uploadFragment.showToast("底座日志上传失败");
                    }
                    break;
                case 401://更新获取进度提示框
                    if (uploadFragment != null) {
                        uploadFragment.setDialogProgress();
                    }
                    break;

                case 402://结束更新进度提示框
                    if (uploadFragment != null) {
                        uploadFragment.dismissDialog();
                    }
                    break;
                case 501://结束更新进度提示框

                    Dialog dizuo_mac = MyDialog.showUpdateResult((String) msg.obj);
                    dizuo_mac.show();
                    break;
            }
        }
    };
}
