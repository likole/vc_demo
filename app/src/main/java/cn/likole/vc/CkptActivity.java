package cn.likole.vc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CkptActivity extends AppCompatActivity {


    private ArrayAdapter modelAdapter;
    private List<String> modelList = new ArrayList<>();
    private ListView lv_ckpt;
    private Toolbar mToolbar;
    private TextView tv_ckpt;
    private String ckpt;
    private OkHttpClient client = new OkHttpClient();
    private String address;
    private boolean processing=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ckpt);
        SharedPreferences read = getSharedPreferences("VC",
                MODE_PRIVATE);
        address = read.getString("address1", "");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("检查点信息");
        tv_ckpt = (TextView) findViewById(R.id.tv_ckpt);
        lv_ckpt = (ListView) findViewById(R.id.lv_ckpt);
        modelAdapter = new ArrayAdapter(CkptActivity.this, android.R.layout.simple_list_item_1, modelList);
        lv_ckpt.setAdapter(modelAdapter);
        lv_ckpt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(processing){
                    ToastUtils.showLong("切换任务仍在进行中,请耐心等待~");
                    return;
                }
                ToastUtils.showLong("正在切换,请耐心等待");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        processing=true;
                        String t=modelList.get(i);
                        Request request = new Request.Builder()
                                .url(address + "reset?ckpt="+t)
                                .get()
                                .build();
                        Response response = null;
                        try {
                            response=client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        processing=false;
                        if(response!=null&&response.isSuccessful()){
                            ServiceResult result=null;
                            try {
                                result= GsonUtils.fromJson(response.body().string(),ServiceResult.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(result!=null&&result.getCode()==0){
//                                final ServiceResult finalResult = result;
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ckpt = finalResult.getCkpt();
//                                        if (ckpt.contains("/")) {
//                                            ckpt=ckpt.substring(ckpt.lastIndexOf('/') + 1);
//                                            tv_ckpt.setText("目前检查点:" +ckpt);
//                                            updateLabel();
//                                        } else {
//                                            tv_ckpt.setText(ckpt);
//                                        }
//                                    }
//                                });
                                ToastUtils.showLong("已成功切换至"+result.getCkpt());
                                finish();
                            }
                        }else{
                            ToastUtils.showLong("切换检查点失败");
                        }

                    }
                }).start();
            }
        });
        getData();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //目前检查点
                Request request = new Request.Builder()
                        .url(address + "ckpt")
                        .get()
                        .build();
                Response response1 = null;
                try {
                    response1 = client.newCall(request).execute();
                    final ServiceResult result = GsonUtils.fromJson(response1.body().string(), ServiceResult.class);
                    if (result.getCode() != 0) {
                        ToastUtils.showLong("请求失败,请检查网络连接或接口地址");
                        finish();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ckpt = result.getCkpt();
                            if (ckpt.contains("/")) {
                                ckpt=ckpt.substring(ckpt.lastIndexOf('/') + 1);
                                tv_ckpt.setText("目前检查点:" +ckpt);
                            } else {
                                tv_ckpt.setText(ckpt);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtils.showLong("请求失败,请检查网络连接或接口地址");
                    finish();
                }
                //所有检查点
                request = new Request.Builder()
                        .url(address + "ckpts")
                        .get()
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    final ServiceResult result = GsonUtils.fromJson(response.body().string(), ServiceResult.class);
                    if (result.getCode() != 0) {
                        ToastUtils.showLong("请求失败,请检查网络连接或接口地址");
                        finish();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateData(result.getList());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtils.showLong("请求失败,请检查网络连接或接口地址");
                    finish();
                }
            }
        }).start();
    }

    private void updateData(List<String> list) {
        modelList.clear();
        modelList.addAll(list);
        updateLabel();
    }

    private void updateLabel(){
        for (int i=0;i<modelList.size();i++){
            if (modelList.get(i).equals(ckpt))modelList.set(i,modelList.get(i)+"(目前检查点)");
        }
        modelAdapter.notifyDataSetChanged();
    }
}
