package com.aclyyx.repeater;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends Activity implements Repeater, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Button btnPrev, btnPlay, btnStop, btnNext,
                   BtnPrv5, BtnNxt5,
                   btnSetA, btnSetB, btnRept, btnPaus;
    private SeekBar seekBar;
    private TextView tvProgressInfo;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isSeekbarChanging;
    private int pointA, pointB;
    private PlayerService.PlayBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        tvProgressInfo = findViewById(R.id.PlayProgressInfo);
        seekBar = findViewById(R.id.SeekBar);
        btnPlay = findViewById(R.id.BtnPlay);
        btnStop = findViewById(R.id.BtnStop);
        btnPaus = findViewById(R.id.BtnPaus);
        btnSetA = findViewById(R.id.BtnSetA);
        btnSetB = findViewById(R.id.BtnSetB);
        btnRept = findViewById(R.id.BtnRept);
        btnPrev = findViewById(R.id.BtnPrev);
        btnNext = findViewById(R.id.BtnNext);
        BtnPrv5 = findViewById(R.id.BtnPrv5);
        BtnNxt5 = findViewById(R.id.BtnNxt5);

        seekBar.setOnSeekBarChangeListener(this);
        btnPlay.setOnClickListener(this); btnStop.setOnClickListener(this);
        btnPaus.setOnClickListener(this); btnSetA.setOnClickListener(this);
        btnSetB.setOnClickListener(this); btnRept.setOnClickListener(this);
        btnPrev.setOnClickListener(this); btnNext.setOnClickListener(this);
        BtnPrv5.setOnClickListener(this); BtnNxt5.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BtnPlay:
                start();
                break;
            case R.id.BtnStop:
                stop();
                break;
            case R.id.BtnPaus:
                pause();
                break;
            case R.id.BtnSetA:
                setPointA();
                break;
            case R.id.BtnSetB:
                setPointB();
                break;
            case R.id.BtnRept:
                repeat();
                break;
            case R.id.BtnPrev:
                break;
            case R.id.BtnNext:
                break;
            case R.id.BtnPrv5:
                prev5s();
                break;
            case R.id.BtnNxt5:
                next5s();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        currPosition = seekBar.getProgress();
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekbarChanging =true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        binder.seekTo(seekBar.getProgress());
        isSeekbarChanging =false;
    }

    private Intent startService(int option) {
        Intent intentStartService = new Intent(getApplicationContext(), PlayerService.class);
        intentStartService.putExtra("option", option);
        startService(intentStartService);
        return intentStartService;
    }


    private ServiceConnection connection = new ServiceConnection() {
        /**
         * 服务解除绑定时候调用
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOGGER_TAG, "= StartActivity.ServiceConnection.onServiceDisconnected");
        }
        /**
         * 绑定服务的时候调用
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayerService.PlayBinder) service;
            setSeekBarInfo();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d(LOGGER_TAG, "= StartActivity.ServiceConnection.onBindingDied");
        }
    };

    private void start() {
        Intent startIntent = startService(PlayerService.PLAY_OPTION_PLAY);
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }

    private void stop() {
        currPosition = 0;
        if (connection != null) unbindService(connection);
        startService(PlayerService.PLAY_OPTION_STOP);
    }

    private void pause() {
        startService(PlayerService.PLAY_OPTION_PAUSE);
    }

    private void prev5s() {
        startService(PlayerService.PLAY_OPTION_SUB_5S);
    }

    private void next5s() {
        startService(PlayerService.PLAY_OPTION_ADD_5S);
    }

    private void setPointA() {
        pointB = -1;
//        pointA = mediaPlayer.getCurrentPosition();
    }

    private void setPointB() {
//        pointB = mediaPlayer.getCurrentPosition();
    }

    private void repeat() {
//        mediaPlayer.seekTo(pointA);
    }

    private int duration, currPosition;
    private void setSeekBarInfo() {
        isSeekbarChanging = false;
        duration = binder.getDuration();
        seekBar.setMax(duration);//----------定时器记录播放进度---------//
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                currPosition = binder.getCurrentPosition();
                if(!isSeekbarChanging) {
                    handler.sendEmptyMessage(1);
                }
//                if (pointA > -1 && pointB > -1 && pointB < currPosition) {
//                    binder.seekTo(pointA);
//                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 250);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    seekBar.setProgress(currPosition);
                    tvProgressInfo.setText(timeSec2mmss(currPosition) + "/" + timeSec2mmss(duration));
                    break;
            }
        }
    };

    private DecimalFormat format00;
    private String timeSec2mmss(int msec) {
        if (format00 == null) {
            format00 = new DecimalFormat("00");
        }
        int sec = msec/1000;
        return format00.format(sec/60) + ":" + format00.format(sec%6);
    }
}
