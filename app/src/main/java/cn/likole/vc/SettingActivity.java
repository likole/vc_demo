package cn.likole.vc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private Button btn_scan;
    private Button btn_save;
    private EditText et_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("设置接口");
        btn_save= (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_scan= (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        et_address= (EditText) findViewById(R.id.et_address);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan:
//                Intent intent = new Intent(SettingActivity.this, CaptureActivity.class);
//                startActivityForResult(intent, 0);
                break;
        }
    }
}
