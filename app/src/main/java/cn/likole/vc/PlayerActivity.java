package cn.likole.vc;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by likole on 8/21/18.
 */

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    private MediaPlayer mMediaPlayer;
    private SeekBar seek;
    private TextView left;
    private TextView right;
    private int sum;
    private Button start;
    private Button stop;
    private TextView tv_filename;
    private boolean pause = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    int current = mMediaPlayer.getCurrentPosition();// 得到数值的单位是毫秒
                    int prass = (int) (current / (sum * 1.0) * 100);
                    left.setText(formatTime(current / 1000 ));
                    seek.setProgress(prass);
                    if (!pause) {
                        handler.sendEmptyMessageDelayed(1, 1000);//1 秒后继续更新
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
        left = (TextView) findViewById(R.id.left);
        right = (TextView) findViewById(R.id.right);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        tv_filename= (TextView) findViewById(R.id.tv_filename);

        //播放器初始化
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getIntent().getStringExtra("filepath"));
            tv_filename.setText(getIntent().getStringExtra("filepath"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PlayerActivity", "设置播放源异常");
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        //拖动条
        seek = (SeekBar) findViewById(R.id.seek);
        seek.setMax(100);
        seek.setOnSeekBarChangeListener(this);

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
        start.setEnabled(true);
        stop.setEnabled(true);
        seek.setEnabled(true);
        sum = mMediaPlayer.getDuration();
        right.setText(formatTime(sum / 1000 ));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        start.setText("播放");
        seek.setProgress(0);
        mMediaPlayer.seekTo(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                pause = false;
                handler.sendEmptyMessage(1);
                mMediaPlayer.start();
                break;
            case R.id.stop:
                pause = true;
                mMediaPlayer.pause();
                break;
        }
    }

    private String formatTime(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }
}
