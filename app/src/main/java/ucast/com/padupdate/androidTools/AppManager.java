package ucast.com.padupdate.androidTools;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppManager {

    private PackageManager pm;

    public AppManager(PackageManager pm) {
        this.pm = pm;
    }

    public ArrayList<AppInfo> getApp() {
        List<PackageInfo> packageInfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        ArrayList<AppInfo> appinfos = new ArrayList<AppInfo>();
        for (PackageInfo info : packageInfos) {
            appinfos.add(new AppInfo(info, pm));
        }
        return appinfos;
    }

    public ArrayList<AppInfo> getUcastApp() {

        List<PackageInfo> packageInfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        ArrayList<AppInfo> appinfos = new ArrayList<AppInfo>();
        for (PackageInfo info : packageInfos) {
            if (info.packageName.equals(ApkInfo.BLUE_PACKAGENAME) ||
                    info.packageName.equals(ApkInfo.SERVICE_PACKAGENAME) ||
                    info.packageName.equals(ApkInfo.JIANKONG_PACKAGENAME) ||
                    info.packageName.equals(ApkInfo.PADTEST_PACKAGENAME)
                    ) {
                appinfos.add(new AppInfo(info, pm));
            }
        }
        return appinfos;
    }

    public AppInfo getAppInfoByPackageName(String packageName){
        List<PackageInfo> packageInfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        AppInfo appInfo=null;
        for (PackageInfo info : packageInfos) {
            if (info.packageName.equals(packageName)) {
                appInfo=new AppInfo(info,pm);
            }
        }
        return appInfo;
    }

    public ArrayList<AppInfo> getSystemApp() {
        ArrayList<AppInfo> appinfos = new ArrayList<AppInfo>();
        for (AppInfo appInfo : getApp()) {
            if (((appInfo.getAppFlag() & ApplicationInfo.FLAG_SYSTEM) != 0) || ((appInfo.getAppFlag() & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
                appinfos.add(appInfo);
            }
        }
        return appinfos;
    }

    public ArrayList<AppInfo> getUserApp() {
        ArrayList<AppInfo> appinfos = new ArrayList<AppInfo>();
        for (AppInfo appInfo : getApp()) {
            if (!(((appInfo.getAppFlag() & ApplicationInfo.FLAG_SYSTEM) != 0) || ((appInfo.getAppFlag() & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0))) {
                appinfos.add(appInfo);
            }
        }
        return appinfos;
    }

}
