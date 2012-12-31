package fr.lepetitpingouin.android.t411;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

public class Widget_huge extends AppWidgetProvider {

	ImageButton updateBtn;
	String ratio, upload, download, mails, username, origusername;
	// Les mêmes, qui vont servir de tampon pour valider les données du service
	String _ratio, _upload, _download, _mails, _username;
	Date date = new Date();
	Intent myIntent = new Intent();
	PendingIntent pIntent;
	SharedPreferences prefs;
	Editor edit;
	Intent serviceIntent;
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.v("Widget Clock","onDeleted");
		if(appWidgetIds.length < 1)
			context.stopService(new Intent(context, t411updater.class));
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.v("widget t411", "onUpdate");
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		Log.d("isClockPresent ?",String.valueOf(prefs.getBoolean("isClockPresent", false)));

		if(prefs.getBoolean("isClockPresent", false) == false) {
			serviceIntent = new Intent(context.getApplicationContext(), t411clock.class);
			context.startService(serviceIntent);
			edit = prefs.edit();
			edit.putBoolean("isClockPresent", true);
			edit.commit();
		}
		
		

		final int N = appWidgetIds.length;

		username = context.getString(R.string.waiting_for_update);
		origusername = username;

		// loop through all app widgets the user has enabled
		for (int i = 0; i < N; i++) {
			int widgetId = appWidgetIds[i];

			// get our view so we can edit the time
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_huge);
			date = new Date();

			try {
				Log.v("widget t411", "Définition de l'Intent");

				ratio = (ratio == null) ? prefs.getString("lastRatio", "?.??")
						: ratio;
				upload = (upload == null) ? prefs.getString("lastUpload",
						"  ???.?? MB") : upload;
				download = (download == null) ? prefs.getString("lastDownload",
						"  ???.?? MB") : download;
				mails = (mails == null) ? String.valueOf(prefs.getInt(
						"lastMails", 0)) : mails;
				username = (username == null || username == origusername) ? prefs
						.getString("lastUsername", origusername) : username;
				username = (username == null) ? String.valueOf(prefs.getInt(
						"lastUsername", 0)) : username;

				switch (prefs.getInt("widgetAction", 5)) {
				case 0:
					myIntent.setClassName("fr.lepetitpingouin.android.t411",
							"fr.lepetitpingouin.android.t411.MainActivity");
					pIntent = PendingIntent
							.getActivity(context, 0, myIntent, 0);
					break;
				case 1:
					myIntent.setClassName("fr.lepetitpingouin.android.t411",
							"fr.lepetitpingouin.android.t411.t411updater");
					pIntent = PendingIntent.getService(context, 0, myIntent, 0);
					break;
				case 2:
					break;
				case 3:
					String url = "http://www.t411.me";
					myIntent = new Intent(Intent.ACTION_VIEW);
					myIntent.setData(Uri.parse(url));
					pIntent = PendingIntent
							.getActivity(context, 0, myIntent, 0);
					break;
				default:
					myIntent.setClassName("fr.lepetitpingouin.android.t411",
							"fr.lepetitpingouin.android.t411.actionSelector");
					pIntent = PendingIntent
							.getActivity(context, 0, myIntent, 0);
					break;
				}
				views.setOnClickPendingIntent(R.id.topLogo, pIntent);
			} catch (Exception ex) {
				Log.e("widget t411 - lancement de l'Intent", ex.toString());
			}
			
			try{
				PackageManager packageManager = context.getPackageManager();
				Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
	
				// Verify clock implementation
				String clockImpls[][] = {
				        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
				        {"Standar Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
				        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
				        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
				        {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"}
				};
	
				boolean foundClockImpl = false;
	
				for(int j=0; j<clockImpls.length; j++) {
				    String vendor = clockImpls[j][0];
				    String packageName = clockImpls[j][1];
				    String className = clockImpls[j][2];
				    try {
				        ComponentName cn = new ComponentName(packageName, className);
				        ActivityInfo aInfo = packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
				        alarmClockIntent.setComponent(cn);
				        Log.d("","Found " + vendor + " --> " + packageName + "/" + className);
				        foundClockImpl = true;
				    } catch (NameNotFoundException e) {
				        Log.d("",vendor + " does not exists");
				    }
				}
	
				if (foundClockImpl) {
				    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmClockIntent, 0);
				        // add pending intent to your component
				    views.setOnClickPendingIntent(R.id.wClock, pendingIntent);
				}
				
				views.setOnClickPendingIntent(R.id.restartBtn, PendingIntent.getService(context, 0, new Intent(context.getApplicationContext(), t411clock.class), 0));
			}
			catch (Exception ex) {
				Log.e("Clock exception :",ex.toString());
			}
			

			Log.v("widget t411", "mise à jour des valeurs");
			views.setTextViewText(R.id.updatedTime,
					prefs.getString("lastDate", "?????"));
			views.setTextViewText(R.id.wUpload, upload);
			views.setTextViewText(R.id.wDownload, download);
			views.setTextViewText(R.id.wMails, mails);
			views.setTextViewText(R.id.wRatio, ratio);
			views.setTextViewText(R.id.wUsername, username);			

			// updating time
			views.setTextViewText(
					R.id.wHour,
					String.valueOf(Calendar.getInstance().get(
							Calendar.HOUR_OF_DAY)));

			int minutes = Calendar.getInstance().get(Calendar.MINUTE);

			String sMinutes = (minutes < 10) ? "0" + minutes : String
					.valueOf(minutes);
			views.setTextViewText(R.id.wMinutes, ":" + sMinutes);
			
			String sDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
			
			views.setTextViewText(R.id.wDate, sDate.toUpperCase());

			Log.v("widget t411", "mise √† jour du smiley");
			int smiley = R.drawable.smiley_unknown;
			try {
				double numRatio = Double.valueOf(ratio);

				if (numRatio < 2)
					smiley = R.drawable.smiley_good;
				if (numRatio < 1)
					smiley = R.drawable.smiley_neutral;
				if (numRatio < 0.75) {
					smiley = R.drawable.smiley_cry;
					views.setTextColor(R.id.wRatio, Color.RED);
				}
				if (numRatio > 1.99)
					smiley = R.drawable.smiley_w00t;
				if (numRatio > 9.99)
					smiley = R.drawable.smiley_love;

				// easter egg :) si le ratio contient 42, on affiche Marvin :)
				if (String.valueOf(numRatio).contains("42"))
					smiley = R.drawable.smiley_marvin;
				// easter egg :) si le ratio est de 13.37, on affiche
				// l'invadroid
				if (numRatio == 13.37)
					smiley = R.drawable.smiley_leet;
				//easter egg :) si on est le 25/12, on affiche le père noël-droid
				if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER 
						&& Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 25)
					smiley = R.drawable.smiley_xmas;
			} catch (Exception ex) {
				Log.e("widget t411", ex.toString());
			}
			views.setImageViewResource(R.id.wSmiley, smiley);

			Log.v("widget t411", "refresh du widget");
			// update the widget
			appWidgetManager.updateAppWidget(widgetId, views);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.v("widget t411",
				"onReceive a reçu le Broadcast Intent : " + intent.getAction());

		try {
			username = context.getString(R.string.waiting_for_update);
			origusername = username;
		} finally {
		}

		try {
			_ratio = intent.getStringExtra("ratio");
			ratio = (_ratio != null) ? _ratio : ratio;
			_upload = intent.getStringExtra("upload");
			upload = (_upload != null) ? _upload : upload;
			_download = intent.getStringExtra("download");
			download = (_download != null) ? _download : download;
			_mails = intent.getStringExtra("mails");
			mails = (_mails != null) ? _mails : mails;
			_username = intent.getStringExtra("username");
			username = (_username != null) ? _username : username;
		} catch (Exception ex) {
			Log.e("mise à jour des données dapuis le service", ex.toString());
		}
		Log.v("widget t411", "mise à jour forcée...");
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(
				context.getPackageName(), Widget_huge.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
