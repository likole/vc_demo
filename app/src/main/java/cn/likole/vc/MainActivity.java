package cn.likole.vc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.blankj.utilcode.util.SDCardUtils;
import com.daimajia.swipe.util.Attributes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private VisualizerView mVisualizerView;
    private MyAudioRecorder myAudioRecorder;
    private Button btn_record;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<File> mDatas;
    private String path;

    private static String[] PERMISSION_AUDIO = {
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        权限
        int permission = android.support.v4.app.ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO);
        if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.support.v4.app.ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_AUDIO, 1);
        }

//        可视化
        mVisualizerView = (VisualizerView) findViewById(R.id.visualizer);
        myAudioRecorder = MyAudioRecorder.getInstanse(false);
        myAudioRecorder.link(mVisualizerView);

//        录音按钮
        btn_record = (Button) findViewById(R.id.btn_record);
        btn_record.setOnTouchListener(this);
        btn_record.setText("按住录音");

//        路径
        if (SDCardUtils.getSDCardPaths(true).size() > 0)
            path = SDCardUtils.getSDCardPaths(true).get(0);
        else path = SDCardUtils.getSDCardPaths(false).get(0);
        path = path + File.separator + "vc_demo" + File.separator;
        File dir = new File(path);
        if (dir.list() == null) {
            dir.mkdirs();
        }

//        数据
        mDatas = getFileSort(path);

//        列表
        mListView = (ListView) findViewById(R.id.lv_file);
        mAdapter = new ListViewAdapter(this, mDatas);
        mAdapter.setMode(Attributes.Mode.Single);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,PlayerActivity.class);
                intent.putExtra("filepath",mDatas.get(i).getAbsolutePath());
                startActivity(intent);
            }
        });

    }

    public static List<File> getFileSort(String path) {

        List<File> list = getFiles(path, new ArrayList<File>());

        if (list != null && list.size() > 0) {

            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }

                }
            });

        }

        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {

        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }


    /**
     * 更新listview
     */
    private void updateData() {
        List<File> tmp=getFileSort(path);
        mDatas.clear();
        mDatas.addAll(tmp);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.btn_record) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    myAudioRecorder.recordChat(path, new Date().getTime() + ".wav");
                    btn_record.setText("正在录制...");
                    break;
                case MotionEvent.ACTION_UP:
                    myAudioRecorder.stopRecord();
                    btn_record.setText("按住录音");
                    myAudioRecorder = MyAudioRecorder.getInstanse(false);
                    myAudioRecorder.link(mVisualizerView);
                    updateData();
                    break;
                default:
                    break;
            }
        }
        return false;
    }
}
