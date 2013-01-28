package fr.lepetitpingouin.android.t411;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class t411clock extends Service {
	
	BroadcastReceiver bR;
	SharedPreferences prefs;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		bR = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				sendBroadcast(new Intent(
						"android.appwidget.action.APPWIDGET_UPDATE"));
				Log.v("ACTION_TIME_TICK", "Clock updated !");
			}
		};
		
		IntentFilter iF = new IntentFilter(Intent.ACTION_TIME_TICK);
		sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
		
		try {
			unregisterReceiver(bR);
		} catch (Exception ex){}
		try {
			if(prefs.getBoolean("isClockPresent", false));
			registerReceiver(bR, iF);
		} catch (Exception ex){Log.e("registerReceiver",ex.toString());}
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
