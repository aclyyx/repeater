package com.aclyyx.repeater.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.aclyyx.repeater.MP3;
import com.aclyyx.repeater.RepeaterApplication;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerService extends Service {

    private RepeaterApplication app;
    private int playState;
    private MediaPlayer mediaPlayer;
    private MP3 currMp3;
    private PlayBinder playBinder;

    public static final int PLAY_OPTION_INIT   = 900;
    public static final int PLAY_OPTION_PLAY   = 901;
    public static final int PLAY_OPTION_STOP   = 902;
    public static final int PLAY_OPTION_PAUSE  = 903;
    public static final int PLAY_OPTION_ADD_5S = 5;
    public static final int PLAY_OPTION_SUB_5S = -5;
    public static final int PLAY_OPTION_ADD_MP3 = 904;
    public static final int PLAY_OPTION_RESET_LIST = 905;

    public static final int PLAY_STATE_STOP  = 0;
    public static final int PLAY_STATE_PLAY  = 1;
    public static final int PLAY_STATE_PAUSE = 2;

    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (RepeaterApplication) getApplication();
        mediaPlayer = new MediaPlayer();
        playBinder  = new PlayBinder();
        playState   = PLAY_STATE_STOP;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int option = intent.getIntExtra("option", 0);

        switch (option) {
            case PLAY_OPTION_PLAY:
                start();
                break;
            case PLAY_OPTION_STOP:
                stop();
                break;
            case PLAY_OPTION_PAUSE:
                pause();
                break;
            case PLAY_OPTION_ADD_5S:
                next5s();
                break;
            case PLAY_OPTION_SUB_5S:
                prev5s();
                break;
            default:
                break;
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playBinder;
    }

    /**
     * 内部类继承Binder
     * @author lenovo
     *
     */
    public class PlayBinder extends Binder {

        public int getDuration() {
            if (mediaPlayer.isPlaying())
                return mediaPlayer.getDuration();
            else
                return 0;
        }
        public int getCurrentPosition() {
            if (mediaPlayer.isPlaying())
                return mediaPlayer.getCurrentPosition();
            else
                return 0;
        }
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }
        public void seekTo(int msec) {
            mediaPlayer.seekTo(msec);
        }
    }

    private void start() {
        try {
            switch (playState) {
                case PLAY_STATE_STOP:
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(currMp3.getPath());
                        mediaPlayer.prepare();
                    }
                case PLAY_STATE_PAUSE:
                    mediaPlayer.start();
                    playState = PLAY_STATE_PLAY;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        playState = PLAY_STATE_STOP;
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pause() {
        playState = PLAY_STATE_PAUSE;
        mediaPlayer.pause();
    }

    private void prev5s() {
        int currTime = mediaPlayer.getCurrentPosition();
        if (currTime-5000 > 0) {
            mediaPlayer.seekTo(currTime-5000);
        }
    }

    private void next5s() {
        int currTime = mediaPlayer.getCurrentPosition();
        if (currTime+5000 < mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(currTime+5000);
        }
    }
}
