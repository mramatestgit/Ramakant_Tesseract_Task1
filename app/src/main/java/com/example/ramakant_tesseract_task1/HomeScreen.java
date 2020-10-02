package com.example.ramakant_tesseract_task1;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ramakantsdk.AppInfo;
import com.example.ramakantsdk.AppSdk;

import java.util.List;

public class HomeScreen extends AppCompatActivity {
    public static String TAG = "HomeScreen";
    ProgressDialog p;
    List<AppInfo> appList;
    AppListAdapter mAdapter;
    private SearchView searchView;
    AppSdk appSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    private class AsyncFetchAppList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(HomeScreen.this);
            p.setMessage("Loading...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //use singleTone for App SDK
            appSdk = AppSdk.getInstance(getApplicationContext());
            appList = appSdk.fetchAppList();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter = new AppListAdapter(appList, HomeScreen.this);
                    ((RecyclerView) findViewById(R.id.list)).setAdapter(mAdapter);
                }
            });
            return "";
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute("");
            Log.d(TAG, "loaded");
            getSupportActionBar().setSubtitle("Total Apps : "+appList.size());
            p.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appSdk.unregisterReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncFetchAppList asyncTask = new AsyncFetchAppList();
        asyncTask.execute("");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
       // super.onBackPressed();
    }

}