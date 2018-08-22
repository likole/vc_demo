package cn.likole.vc;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private ImageButton start;
    private Button convert;
    private boolean pause = true;
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
        left = (TextView) findViewById(R.id.left);
        right = (TextView) findViewById(R.id.right);
        start = (ImageButton) findViewById(R.id.start);
        convert = (Button) findViewById(R.id.convert);
        start.setOnClickListener(this);
        convert.setOnClickListener(this);
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

        //拖动条
        seek = (SeekBar) findViewById(R.id.seek);
        seek.setMax(100);
        seek.setOnSeekBarChangeListener(this);

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
        start.setEnabled(true);
        convert.setEnabled(true);
        seek.setEnabled(true);
        sum = mMediaPlayer.getDuration();
        right.setText(formatTime(sum / 1000 ));
        pause=true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        start.setImageDrawable(getResources().getDrawable(R.drawable.play));
        seek.setProgress(0);
        mMediaPlayer.seekTo(0);
        pause=true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if(pause){
                    pause = false;
                    handler.sendEmptyMessage(1);
                    mMediaPlayer.start();
                    start.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }else{
                    pause = true;
                    mMediaPlayer.pause();
                    start.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }
                break;
            case R.id.convert:

                break;
        }
    }

    private String formatTime(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }
}
