package fr.lepetitpingouin.android.t411;

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
		
		ctx.startService(new Intent(ctx.getApplicationContext(), t411clock.class));
	}
}