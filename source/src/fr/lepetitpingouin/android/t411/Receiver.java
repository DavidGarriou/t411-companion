package fr.lepetitpingouin.android.t411;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent i) {

		Log.v("Receive boot sequence",
				"MyReceiver.onReceive : " + i.getAction());

		Intent intent = new Intent(ctx, t411updater.class);
		ctx.startService(intent);
		
		Intent intent2 = new Intent("android.intent.ACTION_TIME_CHANGED");
		PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, intent2, 0);
		AlarmManager am = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
		am.cancel(pi); // cancel any existing alarms
		am.setRepeating(AlarmManager.RTC, 2000, 2000, pi);
	}
}