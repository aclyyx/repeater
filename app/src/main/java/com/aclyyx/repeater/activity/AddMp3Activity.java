package com.aclyyx.repeater.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aclyyx.repeater.R;
import com.aclyyx.repeater.Repeater;
import com.aclyyx.repeater.util.SharedPreferencesUtil;

import java.io.File;
import java.io.FilenameFilter;

public class AddMp3Activity extends Activity implements Repeater {

    private String startPath;
    private File[] files;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mp3);

        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setMultiChoiceModeListener(multiChoiceModeListener);

        startPath = SharedPreferencesUtil.newInstance(this)
                .getStringValue("startPath", System.getenv("EXTERNAL_STORAGE"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        File file = new File(startPath);
        if (file.isDirectory()) {
            files = file.listFiles(filter);
        }
    }

    private FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File file, String s) {
            if (file.isDirectory()) {
                File f = new File(file.getPath()+'/'+s);
                return (f.isDirectory() && !s.startsWith("."))
                        || s.endsWith("mp3") || s.endsWith("aac");
            } else {
                return s.endsWith("mp3");
            }
        }
    };

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return files == null ? 0 : files.length;
        }

        @Override
        public Object getItem(int i) {
            return files[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = new TextView(AddMp3Activity.this, null);
            }
            ((TextView) view).setText(files[i].getName());
            return view;
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (files[i].isDirectory()) {
                files = files[i].listFiles(filter);
            }
            adapter.notifyDataSetChanged();
        }
    };

    private AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    };
}
