package com.android.ag.blocklock;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

public class LocalService extends Service{
	public static final String TAG = "LocalService";
	
	private LocalBroadcastReceiver mReceiver = null;
	private Intent rebootIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		mReceiver = new LocalBroadcastReceiver();
		
		registerReceiver(mReceiver, filter);
		
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("KeyguardLock");
		keyguardLock.disableKeyguard();

        if(Build.VERSION.SDK_INT > 10) {
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.icons_lock);
            Notification notification;
            if (Build.VERSION.SDK_INT < 16)
                notification = builder.getNotification();
            else
                notification = builder.build();
            startForeground(777, notification);
        }
        else
        {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

            int icon = R.drawable.icons_lock;
            CharSequence tickerText = "Hello";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, tickerText, when);

            Context context = getApplicationContext();
            CharSequence contentTitle = "My notification";
            CharSequence contentText = "Hello World!";
            Intent notificationIntent = new Intent(context,getClass());
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            //notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

            final int HELLO_ID = 777;

            mNotificationManager.notify(HELLO_ID, notification);

        }
        super.onCreate();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		rebootIntent = intent;
		//return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
	}
	
	@Override
	public void onDestroy() {

		unregisterReceiver(mReceiver);

		if (rebootIntent != null) {
			startService(rebootIntent);
		}
		super.onDestroy();
	}

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (Build.VERSION.SDK_INT == 19)
        {
            Intent restartIntent = new Intent(this, getClass());

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            restartIntent.putExtra("RESTART", "RESTART");
            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
        }
        super.onTaskRemoved(rootIntent);
    }
}
