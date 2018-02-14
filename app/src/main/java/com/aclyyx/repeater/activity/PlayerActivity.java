package com.aclyyx.repeater.activity;

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
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aclyyx.repeater.MP3;
import com.aclyyx.repeater.RepeaterApplication;
import com.aclyyx.repeater.service.PlayerService;
import com.aclyyx.repeater.R;
import com.aclyyx.repeater.Repeater;
import com.aclyyx.repeater.util.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends Activity implements Repeater, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private RepeaterApplication app;
    private Button btnPrev, btnNext, btnPrv5, btnNxt5, btnRept, btnAddMp3;
    private CheckBox btnSetA, btnSetB, btnPlay;
    private SeekBar seekBar;
    private TextView tvProgressInfo;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isSeekbarChanging;
    private int pointA, pointB, duration, currPosition;
    private PlayerService.PlayBinder binder;


    private void test() {
        MP3 currMp3 = new MP3();
        String sdcardPath = System.getenv("EXTERNAL_STORAGE");
        currMp3.setPath(sdcardPath + "/qqmusic/song/林俊杰 - 江南 [mqms2].mp3");
        Log.i("aclyyx", currMp3.getPath());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        app = (RepeaterApplication) getApplication();
        tvProgressInfo = findViewById(R.id.PlayProgressInfo);
        seekBar   = findViewById(R.id.SeekBar);
        btnPlay   = findViewById(R.id.BtnPlay);
        btnSetA   = findViewById(R.id.BtnSetA);
        btnSetB   = findViewById(R.id.BtnSetB);
        btnRept   = findViewById(R.id.BtnRept);
        btnPrev   = findViewById(R.id.BtnPrev);
        btnNext   = findViewById(R.id.BtnNext);
        btnPrv5   = findViewById(R.id.BtnPrv5);
        btnNxt5   = findViewById(R.id.BtnNxt5);
        btnAddMp3 = findViewById(R.id.BtnAddMp3);


        seekBar.setOnSeekBarChangeListener(this);
        btnPlay.setOnClickListener(this); btnSetA.setOnClickListener(this);
        btnSetB.setOnClickListener(this); btnRept.setOnClickListener(this);
        btnPrev.setOnClickListener(this); btnNext.setOnClickListener(this);
        btnPrv5.setOnClickListener(this); btnNxt5.setOnClickListener(this);
        btnAddMp3.setOnClickListener(this);

        test();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pointA = pointB = -1;
        btnSetA.setChecked(false);
        btnSetB.setChecked(false);
        btnSetA.setEnabled(false);
        btnSetB.setEnabled(false);
        btnRept.setEnabled(false);
        Intent intent = startService(PlayerService.PLAY_OPTION_INIT);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (connection != null) unbindService(connection);
        pointA = pointB = -1;
        btnSetA.setChecked(false);
        btnSetB.setChecked(false);
        btnSetB.setEnabled(false);
        stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BtnPlay:  play();     break;
            case R.id.BtnSetA:  setPointA();break;
            case R.id.BtnSetB:  setPointB();break;
            case R.id.BtnRept:  repeat();   break;
            case R.id.BtnPrev:  break;
            case R.id.BtnNext:  break;
            case R.id.BtnPrv5:  prev5s();   break;
            case R.id.BtnNxt5:  next5s();   break;
            case R.id.BtnAddMp3:
                startAddMp3Activity();      break;
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
            btnPlay.setChecked(binder.isPlaying());
            btnSetA.setEnabled(binder.isPlaying());
            if (binder.isPlaying()) {
                setSeekBarInfo();
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d(LOGGER_TAG, "= StartActivity.ServiceConnection.onBindingDied");
        }
    };

    private void play() {
        pointA = pointB = -1;
        if (binder.isPlaying()) {
            startService(PlayerService.PLAY_OPTION_PAUSE);
            stop();
        } else {
            startService(PlayerService.PLAY_OPTION_PLAY);
            btnSetA.setEnabled(true);
            setSeekBarInfo();
        }
    }

    private void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        btnSetA.setChecked(false);
        btnSetB.setChecked(false);
        btnSetA.setEnabled(false);
        btnSetB.setEnabled(false);
    }

    private void prev5s() {
        startService(PlayerService.PLAY_OPTION_SUB_5S);
    }

    private void next5s() {
        startService(PlayerService.PLAY_OPTION_ADD_5S);
    }

    private void setPointA() {
        if (btnSetA.isChecked())
            pointA = binder.getCurrentPosition();
        else
            pointA = -1;
        btnSetB.setChecked(false);
        pointB = -1;
    }

    private void setPointB() {
        if (btnSetB.isChecked())
            if (pointA > binder.getCurrentPosition())
                btnSetB.setChecked(false);
            else
                pointB = binder.getCurrentPosition();
        else
            pointB = -1;
    }

    private void repeat() {
        binder.seekTo(pointA);
    }

    private void startAddMp3Activity() {
        Intent intent = new Intent(this, AddMp3Activity.class);
        startActivityForResult(intent, 0);
    }

    private void setSeekBarInfo() {
        isSeekbarChanging = false;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                currPosition = binder.getCurrentPosition();
                duration = binder.getDuration();
                if(!isSeekbarChanging)
                    handler.sendEmptyMessage(1);
                if (pointA > -1 && pointB > -1 && pointB < currPosition)
                    binder.seekTo(pointA);
            }
        };
        mTimer.schedule(mTimerTask, 250, 250);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 定时器记录播放进度
                    seekBar.setMax(duration);
                    seekBar.setProgress(currPosition);
                    tvProgressInfo.setText(getPlayProgress());
                    btnSetB.setEnabled(btnSetA.isChecked());
                    btnRept.setEnabled(btnSetA.isChecked() && btnSetB.isChecked());
                    break;
            }
        }
    };

    private String getPlayProgress() {
        return StringUtil.newInstance().timeSec2mmss(currPosition) + "/" +
               StringUtil.newInstance().timeSec2mmss(duration);
    }

}
