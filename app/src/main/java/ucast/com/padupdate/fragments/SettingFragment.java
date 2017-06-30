package ucast.com.padupdate.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import ucast.com.padupdate.R;
import ucast.com.padupdate.androidTools.SavePasswd;
import ucast.com.padupdate.socket.TimerConnect.WhileCheckClient;

/**
 * Created by pj on 2016/11/28.
 */

@ContentView(R.layout.setting)
public class SettingFragment extends Fragment{
    @ViewInject(R.id.setting_et)
    EditText et;
    @ViewInject(R.id.setting_bt)
    Button connect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this,inflater,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        et.setText(SavePasswd.getInstace().getIp("ip","192.168.43.1"));
        super.onResume();
    }

    @Event(R.id.setting_bt)
    private void connect(View view){
        String info = et.getText().toString().trim();
        WhileCheckClient.Run("Tenda_Ucast", "123456", info);
    }
}
