package cn.likole.vc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.SDCardUtils;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private VisualizerView mVisualizerView;
    private MyAudioRecorder myAudioRecorder;
    private Button btn_record;

    private static String[] PERMISSION_AUDIO = {
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permission = android.support.v4.app.ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO);
        if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.support.v4.app.ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_AUDIO, 1);
        }

        mVisualizerView = (VisualizerView) findViewById(R.id.visualizer);
        myAudioRecorder = MyAudioRecorder.getInstanse(false);
        myAudioRecorder.link(mVisualizerView);

        btn_record = (Button) findViewById(R.id.btn_record);
        btn_record.setOnTouchListener(this);
        btn_record.setText("按住录音");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.btn_record) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    String path;
                    if (SDCardUtils.getSDCardPaths(true).size() > 0)
                        path = SDCardUtils.getSDCardPaths(true).get(0);
                    else path = SDCardUtils.getSDCardPaths(false).get(0);
                    myAudioRecorder.recordChat(path + File.separator+"vc_demo"+File.separator, new Date().getTime()+".wav");
                    btn_record.setText("正在录制...");
                    break;
                case MotionEvent.ACTION_UP:
                    myAudioRecorder.stopRecord();
                    btn_record.setText("按住录音");
                    break;
                default:
                    break;
            }
        }
        return false;
    }
}
