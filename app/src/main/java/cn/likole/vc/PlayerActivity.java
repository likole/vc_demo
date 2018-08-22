package cn.likole.vc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by likole on 8/21/18.
 */

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    //原音频
    private MediaPlayer mMediaPlayer;
    private SeekBar seek;
    private TextView left;
    private TextView right;
    private int sum;
    private ImageButton start;
    private boolean pause = true;

    //目标音频
    private MediaPlayer mMediaPlayer1;
    private SeekBar seek1;
    private TextView left1;
    private TextView right1;
    private int sum1;
    private ImageButton start1;
    private boolean pause1 = true;
    private TextView tv_info;


    private Button convert;
    private Toolbar mToolbar;
    private String path;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    int current = mMediaPlayer.getCurrentPosition();// 得到数值的单位是毫秒
                    int prass = (int) (current / (sum * 1.0) * 100);
                    left.setText(formatTime(current / 1000));
                    seek.setProgress(prass);
                    if (!pause) {
                        handler.sendEmptyMessageDelayed(0, 50);//0.05秒后继续更新
                    }
                    break;
                }
                case 1: {
                    if (mMediaPlayer1 == null) break;
                    int current = mMediaPlayer1.getCurrentPosition();// 得到数值的单位是毫秒
                    int prass = (int) (current / (sum1 * 1.0) * 100);
                    left1.setText(formatTime(current / 1000));
                    seek1.setProgress(prass);
                    if (!pause1) {
                        handler.sendEmptyMessageDelayed(1, 50);//0.05秒后继续更新
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //路径
        if (SDCardUtils.getSDCardPaths(true).size() > 0)
            path = SDCardUtils.getSDCardPaths(true).get(0);
        else path = SDCardUtils.getSDCardPaths(false).get(0);
        path = path + File.separator + "vc_demo_result" + File.separator;
        File dir = new File(path);
        if (dir.list() == null) {
            dir.mkdirs();
        }

        //转换
        convert = (Button) findViewById(R.id.convert);
        convert.setOnClickListener(this);

        //日志
        tv_info = (TextView) findViewById(R.id.tv_info);
        if (NetworkUtils.isMobileData()) addLog("您目前处于移动网络下,请注意流量消耗~");

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(new File(getIntent().getStringExtra("filepath")).getName());
        setSupportActionBar(mToolbar);

        //原音频
        left = (TextView) findViewById(R.id.left);
        right = (TextView) findViewById(R.id.right);
        start = (ImageButton) findViewById(R.id.start);
        start.setOnClickListener(this);
        start.setEnabled(false);
        //拖动条
        seek = (SeekBar) findViewById(R.id.seek);
        seek.setMax(100);
        seek.setOnSeekBarChangeListener(this);
        //播放器初始化
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getIntent().getStringExtra("filepath"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PlayerActivity", "设置播放源异常");
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);


        //目标音频
        left1 = (TextView) findViewById(R.id.left1);
        right1 = (TextView) findViewById(R.id.right1);
        start1 = (ImageButton) findViewById(R.id.start1);
        start1.setOnClickListener(this);
        start1.setEnabled(false);
        //拖动条
        seek1 = (SeekBar) findViewById(R.id.seek1);
        seek1.setMax(100);
        seek1.setOnSeekBarChangeListener(this);
        //播放器初始化
        initTargetMedia();
    }

    private void initTargetMedia() {
        String filename = new File(getIntent().getStringExtra("filepath")).getName();
        File file = new File(path + filename);

        if (!file.exists()) {
            addLog("请先转换后播放");
            return;
        }else{
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(file.lastModified());
            addLog("转换时间:"+calendar.getTime().toLocaleString());
        }
        mMediaPlayer1 = new MediaPlayer();
        try {
            mMediaPlayer1.setDataSource(path + filename);
            mMediaPlayer1.prepareAsync();
            mMediaPlayer1.setOnPreparedListener(this);
            mMediaPlayer1.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLog(String s) {
        tv_info.setText("[" + TimeUtils.date2String(new Date()) + "]" + s + "\n" + tv_info.getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(PlayerActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_about) {

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == seek) {
            int a = (int) ((sum / 100.0) * (seekBar.getProgress()));
            mMediaPlayer.seekTo(a); //seekTo方法接收的单位是:毫秒
            handler.sendEmptyMessage(0); //更新seekBar
        } else {
            int a = (int) ((sum1 / 100.0) * (seekBar.getProgress()));
            mMediaPlayer1.seekTo(a); //seekTo方法接收的单位是:毫秒
            handler.sendEmptyMessage(1); //更新seekBar
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp == mMediaPlayer) {
            start.setEnabled(true);
            convert.setEnabled(true);
            seek.setEnabled(true);
            sum = mMediaPlayer.getDuration();
            right.setText(formatTime(sum / 1000));
            pause = true;
        } else {
            start1.setEnabled(true);
            seek1.setEnabled(true);
            sum1 = mMediaPlayer1.getDuration();
            right1.setText(formatTime(sum1 / 1000));
            pause1 = true;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp == mMediaPlayer) {
            start.setImageDrawable(getResources().getDrawable(R.drawable.play));
            seek.setProgress(0);
            mMediaPlayer.seekTo(0);
            pause = true;
        } else {
            start1.setImageDrawable(getResources().getDrawable(R.drawable.play));
            seek1.setProgress(0);
            mMediaPlayer1.seekTo(0);
            pause1 = true;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (pause) {
                    pause = false;
                    handler.sendEmptyMessage(0);
                    mMediaPlayer.start();
                    start.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                } else {
                    pause = true;
                    mMediaPlayer.pause();
                    start.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }
                break;
            case R.id.start1:
                if (pause1) {
                    pause1 = false;
                    handler.sendEmptyMessage(1);
                    mMediaPlayer1.start();
                    start1.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                } else {
                    pause1 = true;
                    mMediaPlayer1.pause();
                    start1.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }
                break;
            case R.id.convert:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取文件和请求路径
                        File file = new File(getIntent().getStringExtra("filepath"));
                        SharedPreferences read = getSharedPreferences("VC",
                                MODE_WORLD_READABLE);
                        String address = read.getString("address", "");
                        address += "convert";
                        if (!RegexUtils.isURL(address)) {
                            ToastUtils.showLong("接口地址设置有误");
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addLog("开始转换,请耐心等待");
                            }
                        });
                        //构建请求
                        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file", file.getName(),
                                        RequestBody.create(MediaType.parse("audio/x-wav"), file));
                        RequestBody requestBody = builder.build();
                        Request request = new Request.Builder()
                                .url(address)
                                .post(requestBody)
                                .build();
                        Response response = null;
                        try {
                            response = mOkHttpClient.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //请求失败
                        if (response == null || !response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addLog("请求失败,请检查网络连接或接口地址");
                                }
                            });
                            return;
                        }

                        //设置返回结果
                        try {
                            final ServiceResult serviceResult = GsonUtils.fromJson(response.body().string(), ServiceResult.class);
                            if (serviceResult.getCode() != 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addLog(serviceResult.getMessage());
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addLog("转换成功,正在下载");
                                    }
                                });
                                //下载文件
                                String targetUrl = read.getString("address", "") + "uploads/" + serviceResult.getTarget();
                                Request targetRequest = new Request.Builder().url(targetUrl).get().build();
                                Response targetResponse = mOkHttpClient.newCall(targetRequest).execute();
                                if (targetResponse.isSuccessful()) {
                                    String filename = new File(getIntent().getStringExtra("filepath")).getName();
                                    FileIOUtils.writeFileFromIS(path+filename,targetResponse.body().byteStream());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addLog("文件下载成功");
                                            initTargetMedia();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addLog("文件下载失败");
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            ToastUtils.showLong("无法解析服务器响应结果");
                            return;
                        }
                    }
                }).start();
                break;
        }
    }

    private String formatTime(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }
}
