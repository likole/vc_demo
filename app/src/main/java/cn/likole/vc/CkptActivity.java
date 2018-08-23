package cn.likole.vc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ckpt);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("检查点信息");
        tv_ckpt=(TextView)findViewById(R.id.tv_ckpt);
        lv_ckpt = (ListView) findViewById(R.id.lv_ckpt);
        modelAdapter = new ArrayAdapter(CkptActivity.this, android.R.layout.simple_list_item_1, modelList);
        lv_ckpt.setAdapter(modelAdapter);
        getData();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences read = getSharedPreferences("VC",
                        MODE_WORLD_READABLE);
                String address = read.getString("address1", "");
                OkHttpClient client = new OkHttpClient();
                //所有检查点
                Request request = new Request.Builder()
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
                //目前检查点
                request = new Request.Builder()
                        .url(address + "ckpt")
                        .get()
                        .build();
                Response response1 = null;
                try {
                    response1=client.newCall(request).execute();
                    final ServiceResult result = GsonUtils.fromJson(response1.body().string(), ServiceResult.class);
                    if (result.getCode() != 0) {
                        ToastUtils.showLong("请求失败,请检查网络连接或接口地址");
                        finish();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String ckpt=result.getCkpt();
                            if(ckpt.contains("/")){
                                tv_ckpt.setText("目前检查点:"+ckpt.substring(ckpt.lastIndexOf('/')+1));
                            }else{
                                tv_ckpt.setText(ckpt);
                            }

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
        modelAdapter.notifyDataSetChanged();
    }
}
