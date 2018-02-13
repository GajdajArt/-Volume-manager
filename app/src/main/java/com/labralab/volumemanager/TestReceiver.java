package com.labralab.volumemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.labralab.volumemanager.models.DayParamsList;
import com.labralab.volumemanager.models.Repository;
import com.labralab.volumemanager.models.VolumeManager;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by pc on 12.02.2018.
 */

public class TestReceiver extends BroadcastReceiver {

    VolumeManager volumeManager;
    @Override
    public void onReceive(Context context, Intent intent) {

        int pos = intent.getIntExtra("pos", 0);
        int state = intent.getIntExtra("state", 0);
        String title = intent.getStringExtra("title");
        Repository repository = new Repository();
        DayParamsList day = repository.getDay(title);

        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        v.vibrate(500);

        volumeManager = new VolumeManager(context);
        volumeManager.setParams(day.getParamsList().get(pos), state);
        volumeManager.startAlarmManager(day);
        volumeManager.showNotification(title, title);


    }
}
