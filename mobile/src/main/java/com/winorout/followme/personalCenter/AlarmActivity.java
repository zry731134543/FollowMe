package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.winorout.followme.R;

/**
 * Created by minzhang on 2016/10/26.
 */

public class AlarmActivity extends Activity {
    MediaPlayer alarmMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmMusic = MediaPlayer.create(this, R.raw.alarmm);
        alarmMusic.setLooping(true);

        alarmMusic.start();
        AlertDialog dialog = new AlertDialog.Builder(AlarmActivity.this).setTitle("闹钟")
                .setMessage("闹钟响了")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alarmMusic.stop();
                        AlarmActivity.this.finish();
                    }
                }).show();
        dialog.setCancelable(false);
    }
}
