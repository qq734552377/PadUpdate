package ucast.com.padupdate.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import ucast.com.padupdate.androidTools.ResponseEntity;
import ucast.com.padupdate.androidTools.SavePasswd;
import ucast.com.padupdate.socket.Common;

import static android.content.ContentValues.TAG;
import static ucast.com.padupdate.UpdateService.dialog;

/**
 * Created by pj on 2016/11/28.
 */

@ContentView(R.layout.upload)
public class UploadFragment extends Fragment{
    @ViewInject(R.id.spinner)
    Spinner spinner;
    @ViewInject(R.id.dizuo_spinner)
    Spinner dizuo_spinner;
    @ViewInject(R.id.upload_bt)
    Button upload;
    public static final String LOG_PATH= Environment.getExternalStorageDirectory().toString()+"/Ucast";
    private File path_file=null;
    List<String> log_files=new ArrayList<>();
    List<String> dizuo_log_files=new ArrayList<>();
    ProgressDialog dialog;
    int progress=1;
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
        dialog=new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("获取进度提示");


        if (path_file==null){
            path_file=new File(LOG_PATH);
        }

    }

    @Override
    public void onResume() {
        File[] files = path_file.listFiles();
        log_files.clear();
        for (File f:files){
            if (f.isFile()&&(f.getName().contains(".log")||f.getName().contains(".txt"))){
                log_files.add(f.getName());
            }
        }
        // 建立Adapter并且绑定SSID数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, log_files);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner.setAdapter(adapter);
        super.onResume();
    }

    @Event(R.id.upload_bt)
    private void upload(View view){
        String fileName=spinner.getSelectedItem().toString().trim();
        upload(fileName);
    }
    @Event(R.id.upload_all_bt)
    private void uploadAll(View view){
        if (!log_files.isEmpty()){
            for (String fileName:dizuo_log_files) {
                Log.e(TAG, "uploadAll: "+fileName );
                upload(fileName);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
        }else{
            showToast("没有选中文件");
        }
    }
    @Event(R.id.dizuo_querry)
    private void queery(View view){
        String str="@s004$";
        Common.SendData(str.getBytes());
    }
    @Event(R.id.get_dizuo_file)
    private void get_dizuo_file(View view){
        if (!dizuo_log_files.isEmpty()){
            for (String fileName:dizuo_log_files) {
                progress=1;
                dialog.setMax(dizuo_log_files.size());
                dialog.setProgress(0);
                dialog.show();
                deleteFile(LOG_PATH+"/"+fileName);
                Log.e(TAG, "get_dizuo_file: " + fileName);
                String getFileMsg = "@sw001," + fileName + "$";
                Common.SendData(getFileMsg.getBytes());
            }

        }else{
            showToast("请先查询底座所有文件");
        }
    }
    @Event(R.id.dizuo_upload_bt)
    private void dizuoUpload(View view){
        if (dizuo_spinner==null){
            return;
        }
        String dizuo_upload_url=SavePasswd.getInstace().getIp("dizuoUploadUrl",SavePasswd.DIZUOUPLOADURL);
        String path="";
        try {
            path=dizuo_spinner.getSelectedItem().toString().trim();
        }catch (Exception e){
            showToast("请选中需要上传的文件");
            return;
        }
        String str="@s005,"+dizuo_upload_url+","+path+"$";
        Common.SendData(str.getBytes());
    }

    @Event(R.id.dizuo_upload_all_bt)
    private void dizuoUploadAll(View view){
        if (!dizuo_log_files.isEmpty()){
            String dizuo_upload_url=SavePasswd.getInstace().getIp("dizuoUploadUrl",SavePasswd.DIZUOUPLOADURL);
            for (String fileName:dizuo_log_files) {
                Log.e(TAG, "dizuoUploadAll: "+fileName );
                String getFileMsg="@s005,"+dizuo_upload_url+","+fileName+"$";
                Common.SendData(getFileMsg.getBytes());
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
        }else{
            showToast("请先查询底座所有文件");
        }
    }

    public void setDizuoLogs(List<String> logs){

        dizuo_log_files.clear();
        dizuo_log_files=logs;
        if (dizuo_spinner!=null){
            // 建立Adapter并且绑定SSID数据源
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, logs);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //绑定 Adapter到控件
            dizuo_spinner.setAdapter(adapter);
        }
    }

    public void upload(String  fileName){
        String url= SavePasswd.getInstace().getIp("padUploadUrl",SavePasswd.DIZUOUPLOADURL);
        String path=LOG_PATH+"/"+fileName;

        RequestParams params=new RequestParams(url);
        params.setMultipart(true);
        params.addBodyParameter("file",new File(path));
        x.http().post(params, new Callback.CommonCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity o) {
                Toast.makeText(getActivity(),"上传成功",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(getActivity(),"上传失败 "+throwable.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void deleteFile(String name) {
        File file=new File(name);
        if (file.exists()) { // 判断文件是否存在
            file.delete();
        } else {
            return;
        }
    }
    public void setDialogProgress(){
        if (progress==dizuo_log_files.size()){
            dialog.dismiss();
        }
        dialog.setProgress(progress++);

    }
    public void dismissDialog(){
        dialog.dismiss();
    }
    public void showToast(String str){
        Toast.makeText(getContext(),str,Toast.LENGTH_LONG).show();
    }

}
