package cn.likole.vc;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private Button btn_scan;
    private Button btn_save;
    private EditText et_address1;
    private EditText et_address2;
    private static String[] PERMISSION_CAMERA = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //权限
        int permission = android.support.v4.app.ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.CAMERA);
        if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.support.v4.app.ActivityCompat.requestPermissions(SettingActivity.this, PERMISSION_CAMERA, 1);
        }

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("接口地址设置");
        btn_save= (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_scan= (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        et_address1= (EditText) findViewById(R.id.et_address1);
        et_address2= (EditText) findViewById(R.id.et_address2);

        //读出地址
        SharedPreferences read = getSharedPreferences("VC",MODE_WORLD_READABLE);
        et_address1.setText(read.getString("address1", ""));
        et_address2.setText(read.getString("address2", ""));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan:
                Intent intent = new Intent(SettingActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.btn_save:
                SharedPreferences.Editor editor = getSharedPreferences("VC",MODE_WORLD_WRITEABLE).edit();
                editor.putString("address1", et_address1.getText().toString());
                editor.putString("address2", et_address2.getText().toString());
                editor.commit();
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                et_address1.setText(content);
                et_address2.setText(content+"uploads/");
            }
        }
    }
}
