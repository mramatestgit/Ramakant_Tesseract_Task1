package com.example.ramakant_tesseract_task1;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ramakantsdk.AppSdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private PackageManager packageManager;
    private ArrayList<String> packageNames;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    private static  int prev_total = 0;
    private  int curr_total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure UI
        listView = new ListView(this);
        listView.setVerticalScrollBarEnabled(false);
        listView.setId(android.R.id.list);
        listView.setDivider(null);
        listView.setBackground(getDrawable( (int) (R.drawable.bg)));
        setContentView(listView);
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) listView.getLayoutParams();
        p.setMargins(0, 0, 0, 0);

        // List Installed Applications
        packageManager = getPackageManager();
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        packageNames = new ArrayList<>();
        prev_total = packageManager.getInstalledPackages(0).size();

        // Tap on an item in the list to launch the app
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    startActivity(packageManager.getLaunchIntentForPackage(packageNames.get(position)));
                } catch (Exception e) {
                    fetchAppList();
                }
            }
        });

        // Open Application Settings on Long Press
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // Attempt to launch the app with the package name
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + packageNames.get(position)));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    fetchAppList();
                }
                return false;
            }
        });
        fetchAppList();
    }

    private void fetchAppList() {
        adapter.clear();
        packageNames.clear();

        int duration = Toast.LENGTH_SHORT;

        // Query the package manager for all apps
        List<ResolveInfo> activities = packageManager.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);

        // Sort the applications in Alphabetical Order
        Collections.sort(activities, new ResolveInfo.DisplayNameComparator(packageManager));
        for (ResolveInfo resolver : activities) {

            //Exclude Test Launcher from Application List
            String appName = (String) resolver.loadLabel(packageManager);
            if (appName.equals("Ramakant_Tesseract_Task1"))
                continue;

            adapter.add(appName);
            packageNames.add(resolver.activityInfo.packageName);

        }
        curr_total = packageManager.getInstalledPackages(0).size();
        listView.setAdapter(adapter);
        if(prev_total == curr_total) {
           //Do Nothing
        } else if(prev_total < curr_total) {
            Toast.makeText(getApplicationContext(), "Application Installed Suceessfully", duration).show();
            prev_total = curr_total;
        } else if(prev_total > curr_total) {
            Toast.makeText(getApplicationContext(), "Application Unistalled Suceessfully", duration).show();
            prev_total = curr_total;
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent the back button from closing the activity
        fetchAppList();
    }
}
