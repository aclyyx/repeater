package com.aclyyx.repeater;

import android.app.Application;

import java.util.List;

/**
 * Created by aclyyx on 2018/2/14.
 */

public class RepeaterApplication extends Application {

    private List<MP3> mp3List;

    private void addMp3(MP3 mp3) {
        mp3List.add(mp3);
    }

    private void resetMp3List(List<MP3> list) {
            mp3List.clear();
            mp3List.addAll(list);
    }
}
