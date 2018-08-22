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

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.IOException;

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
    private TextView tv_info1;


    private Button convert;
    private Toolbar mToolbar;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    int current = mMediaPlayer.getCurrentPosition();// 得到数值的单位是毫秒
                    int prass = (int) (current / (sum * 1.0) * 100);
                    left.setText(formatTime(current / 1000 ));
                    seek.setProgress(prass);
                    if (!pause) {
                        handler.sendEmptyMessageDelayed(0, 50);//0.05秒后继续更新
                    }
                    break;
                }
                case 1: {
                    int current = mMediaPlayer1.getCurrentPosition();// 得到数值的单位是毫秒
                    int prass = (int) (current / (sum1 * 1.0) * 100);
                    left1.setText(formatTime(current / 1000 ));
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

        //转换
        convert = (Button) findViewById(R.id.convert);
        convert.setOnClickListener(this);

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(new File(getIntent().getStringExtra("filepath")).getName());
        setSupportActionBar(mToolbar);

        //原音频
        left = (TextView) findViewById(R.id.left);
        right = (TextView) findViewById(R.id.right);
        start = (ImageButton) findViewById(R.id.start);
        start.setOnClickListener(this);
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
        tv_info1=(TextView) findViewById(R.id.tv_info1);
        tv_info1.setText("请先转化后播放");
        //拖动条
        seek1 = (SeekBar) findViewById(R.id.seek1);
        seek1.setMax(100);
        seek1.setOnSeekBarChangeListener(this);
        //播放器初始化
        mMediaPlayer1 = new MediaPlayer();
        try {
            mMediaPlayer1.setDataSource(getIntent().getStringExtra("filepath"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PlayerActivity", "设置播放源异常");
        }
        mMediaPlayer1.prepareAsync();
        mMediaPlayer1.setOnPreparedListener(this);
        mMediaPlayer1.setOnCompletionListener(this);
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
            Intent intent=new Intent(PlayerActivity.this,SettingActivity.class);
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
        int a = (int) ((sum / 100.0) * (seekBar.getProgress()));
        mMediaPlayer.seekTo(a); //seekTo方法接收的单位是:毫秒
        handler.sendEmptyMessage(1); //更新seekBar
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(mp==mMediaPlayer){
            start.setEnabled(true);
            convert.setEnabled(true);
            seek.setEnabled(true);
            sum = mMediaPlayer.getDuration();
            right.setText(formatTime(sum / 1000 ));
            pause=true;
        }else{
            start1.setEnabled(true);
            seek1.setEnabled(true);
            sum1 = mMediaPlayer1.getDuration();
            right1.setText(formatTime(sum1 / 1000 ));
            pause1=true;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mp==mMediaPlayer){
            start.setImageDrawable(getResources().getDrawable(R.drawable.play));
            seek.setProgress(0);
            mMediaPlayer.seekTo(0);
            pause=true;
        }else{
            start1.setImageDrawable(getResources().getDrawable(R.drawable.play));
            seek1.setProgress(0);
            mMediaPlayer1.seekTo(0);
            pause1=true;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if(pause){
                    pause = false;
                    handler.sendEmptyMessage(0);
                    mMediaPlayer.start();
                    start.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }else{
                    pause = true;
                    mMediaPlayer.pause();
                    start.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }
                break;
            case R.id.start1:
                if(pause1){
                    pause1 = false;
                    handler.sendEmptyMessage(1);
                    mMediaPlayer1.start();
                    start1.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }else{
                    pause1 = true;
                    mMediaPlayer1.pause();
                    start1.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }
                break;
            case R.id.convert:
                if(NetworkUtils.isMobileData()) ToastUtils.showLong("您目前处于移动网络下,请注意流量消耗~");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取文件和请求路径
                        File file=new File(getIntent().getStringExtra("filepath"));
                        SharedPreferences read = getSharedPreferences("VC",
                                MODE_WORLD_READABLE);
                        String address = read.getString("address", "");
                        if(!RegexUtils.isURL(address)){
                            ToastUtils.showLong("接口地址设置有误");
                            return;
                        }

                        //构建请求
                        OkHttpClient mOkHttpClient = new OkHttpClient();
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file", file.getName(),
                                        RequestBody.create(MediaType.parse("audio/x-wav"), file));
                        RequestBody requestBody = builder.build();
                        Request request = new Request.Builder()
                                .url(address)
                                .post(requestBody)
                                .build();
                        Response response=null;
                        try {
                            response= mOkHttpClient.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //请求失败
                        if(response==null||!response.isSuccessful()){
                            ToastUtils.showLong("请求失败,请检查网络连接或接口地址");
                            return;
                        }

                        //设置返回结果

                    }
                }).start();
                break;
        }
    }

    private String formatTime(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }
}
