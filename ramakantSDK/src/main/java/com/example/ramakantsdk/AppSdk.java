package com.example.ramakantsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppSdk {
    public static String TAG = "AppSDK";
    private static AppSdk instance = null;
    private Context mContext;
    private PackageManager mPackageManager;

    private BroadcastReceiver applicationControl = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
                    || intent.getAction().equals(Intent.ACTION_PACKAGE_INSTALL)) {
                Toast.makeText(mContext, "Ramakant : App installed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Ramakant : App un-installed", Toast.LENGTH_LONG).show();
            }
        }
    };

    public AppSdk(Context context) {
        mContext = context;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(applicationControl, intentFilter);
        Log.d(TAG, "AppSdk created");
    }

    //use singleTone for App SDK
    public static AppSdk getInstance(Context context) {
        if (instance == null) {
            instance = new AppSdk(context);
        }
        return instance;
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(applicationControl);
    }

    public List<AppInfo> fetchAppList() {
        mPackageManager = mContext.getPackageManager();

        // Query the package manager for all apps
        List<ResolveInfo> activities = mPackageManager.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);

        // Sort the applications in Alphabetical Order
        Collections.sort(activities, new ResolveInfo.DisplayNameComparator(mPackageManager));
        return getAppInfo(activities);
    }

    private ArrayList<AppInfo> getAppInfo(List<ResolveInfo> appList) {
        ArrayList<AppInfo> mListOfApps = new ArrayList<>();
        for (ResolveInfo resolver : appList) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName((String) resolver.loadLabel(mContext.getPackageManager()));
            String pkgName = (String) resolver.activityInfo.packageName;
            appInfo.setPkgName(pkgName);
            appInfo.setIconId(resolver.getIconResource());

            Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);

            if (launchIntent != null) {
                appInfo.setClassName(launchIntent.getComponent().getClassName());
            }
            PackageInfo pInfo;
            try {
                pInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                appInfo.setVersionName(pInfo.versionName);
                appInfo.setVersionCode(pInfo.versionCode);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            mListOfApps.add(appInfo);
        }
        return mListOfApps;
    }
}
