package ucast.com.padupdate.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

import ucast.com.padupdate.AppEntity;
import ucast.com.padupdate.R;
import ucast.com.padupdate.androidTools.AppInfo;
import ucast.com.padupdate.androidTools.AppManager;
import ucast.com.padupdate.androidTools.MyTools;

/**
 * Created by pj on 2016/11/28.
 */

@ContentView(R.layout.pad_update)
public class PadUpdateFragment extends Fragment {
    @ViewInject(R.id.paupdate_listview)
    ListView lv;
    ArrayList<AppInfo> appInfos;
    MyAdapter adapter;
    private MyReceiver receiver;

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

        appInfos = new AppManager(getContext().getPackageManager()).getUcastApp();
        adapter = new MyAdapter(R.layout.item_listview);

        lv.setAdapter(adapter);

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);// 注册广播机制
        filter.addDataScheme("package"); // 必须添加这项，否则拦截不到广播
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkVersion();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
    public void checkVersion(){
        if (appInfos != null) {
            appInfos.clear();
            appInfos = new AppManager(getContext().getPackageManager()).getUcastApp();
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } else {
            appInfos = new AppManager(getContext().getPackageManager()).getUcastApp();
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }


    class MyAdapter extends BaseAdapter {

        private int layout;

        public MyAdapter(int layout) {

            this.layout = layout;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return appInfos.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return appInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(layout, null);

                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.bt = (Button) convertView.findViewById(R.id.bt);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AppInfo info = appInfos.get(position);
            holder.iv.setImageDrawable(info.getAppIcon());
            holder.tv1.setText("应用名: " + info.getAppName());
            if (holder.tv2 != null) {
                holder.tv2.setText("版本号: " + info.getVersion());
            }
            holder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyTools.getPadVersion(info);
                }
            });

            return convertView;
        }

        class ViewHolder {
            private ImageView iv;
            private TextView tv1;
            private TextView tv2;
            private Button bt;
        }

    }
    /**
     * 设置广播监听
     */
    private class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                checkVersion();
            }
        }

    }

}
