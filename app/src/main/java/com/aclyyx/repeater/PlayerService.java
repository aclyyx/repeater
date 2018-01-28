package com.aclyyx.repeater;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class PlayerService extends Service {

    private MediaPlayer mediaPlayer;
    private MP3 currMp3;
    private PlayBinder playBinder;

    public static final int PLAY_OPTION_PLAY   = 901;
    public static final int PLAY_OPTION_STOP   = 902;
    public static final int PLAY_OPTION_PAUSE  = 903;
    public static final int PLAY_OPTION_ADD_5S = 5;
    public static final int PLAY_OPTION_SUB_5S = -5;

    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        playBinder  = new PlayBinder();
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
            return mediaPlayer.getDuration();
        }
        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }
        public void seekTo(int msec) {
            mediaPlayer.seekTo(msec);
        }
    }

    private void test() {
        currMp3 = new MP3();
        String sdcardPath = System.getenv("EXTERNAL_STORAGE");
        currMp3.setPath(sdcardPath + "/qqmusic/song/林俊杰 - 江南 [mqms2].mp3");
        Log.i("aclyyx", currMp3.getPath());
    }

    private void start() {
        test();
        try {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(currMp3.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pause() {
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else if (mediaPlayer!=null&&!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
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
