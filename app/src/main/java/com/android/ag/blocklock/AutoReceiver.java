package com.android.ag.blocklock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, LocalService.class);
        context.startService(startServiceIntent);
    }
}
