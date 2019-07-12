package org.wiseass.naturewallpapershd4k;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sanved on 01-06-2017.
 */

public class AlertRec extends BroadcastReceiver {

    SharedPreferences prefs;
    SharedPreferences.Editor ed;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("1","inside onreceive1");

        prefs = context.getSharedPreferences("football", MODE_PRIVATE);

        Log.e("1","inside onreceive2");

        boolean dailyUpdate = prefs.getBoolean("daily",false);

        if(dailyUpdate){

            Toast.makeText(context, "nagdi bai cha anus khaas", Toast.LENGTH_SHORT).show();
            Log.e("2","inside if clause");

        }

    }
}
