package com.example.ramakant_tesseract_task1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ramakantsdk.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder>
        implements Filterable {

    private final Context mContext;
    private List<AppInfo> mValues;
    private List<AppInfo> mValuesFiltered;
    private String category;

    public AppListAdapter(List<AppInfo> items, Context context) {
        mValues = items;
        mValuesFiltered = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_apps, parent, false);
        //updateListView();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mApps = mValuesFiltered.get(position);
        holder.appName.setText("App Name : " + holder.mApps.getAppName());
        holder.pkgName.setText("Package Name : " + holder.mApps.getPkgName());
        holder.className.setText("Class Name : " + holder.mApps.getClassName());
        holder.versionName.setText("Version Name[Code] : v" + holder.mApps.getVersionName()
                + "[" + holder.mApps.getVersionCode() + "]");

        try {
            holder.logo.setImageDrawable(mContext.getPackageManager().getApplicationIcon(holder.mApps.getPkgName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mValuesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mValuesFiltered = mValues;
                } else {
                    ArrayList<AppInfo> filteredList = new ArrayList<>();
                    for (AppInfo row : mValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getAppName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mValuesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mValuesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mValuesFiltered = (ArrayList<AppInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView appName;
        public final TextView pkgName, className, versionName;
        public final ImageView logo;
        public AppInfo mApps;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            appName = view.findViewById(R.id.tv_appName);
            pkgName = view.findViewById(R.id.tv_pkgName);
            className = view.findViewById(R.id.tv_className);
            versionName = view.findViewById(R.id.tv_versionName);
            logo = view.findViewById(R.id.logo);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(mContext.getPackageManager()
                            .getLaunchIntentForPackage(mApps.getPkgName()));
                }
            });
        }
    }
}