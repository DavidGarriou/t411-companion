package fr.lepetitpingouin.android.t411;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

public class Widget_Full extends AppWidgetProvider {

	ImageButton updateBtn;
	String ratio, upload, download, mails, username, origusername;
	// Les m�mes, qui vont servir de tampon pour valider les donn�es du service
	String _ratio, _upload, _download, _mails, _username;
	Date date = new Date();
	Intent myIntent = new Intent();
	PendingIntent pIntent;
	SharedPreferences prefs;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.v("widget t411", "onUpdate");
		final int N = appWidgetIds.length;

		username = context.getString(R.string.waiting_for_update);
		origusername = username;

		// loop through all app widgets the user has enabled
		for (int i = 0; i < N; i++) {
			int widgetId = appWidgetIds[i];

			// get our view so we can edit the time
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_full);
			if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER 
					&& Calendar.getInstance().get(Calendar.DAY_OF_MONTH) > 22 && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 27)
				views.setImageViewResource(R.id.topLogo, R.drawable.ic_xmas);
			
			if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER 
					&& Calendar.getInstance().get(Calendar.DAY_OF_MONTH) > 0 && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 23)
				views.setImageViewResource(R.id.topLogo, R.drawable.ic_xmastree);


			date = new Date();

			try {
				Log.v("widget t411", "D�finition de l'Intent");

				prefs = PreferenceManager.getDefaultSharedPreferences(context);
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

			Log.v("widget t411", "mise � jour des valeurs");
			views.setTextViewText(R.id.updatedTime,
					prefs.getString("lastDate", "?????"));
			views.setTextViewText(R.id.wUpload, upload);
			views.setTextViewText(R.id.wDownload, download);
			views.setTextViewText(R.id.wMails, mails);
			views.setTextViewText(R.id.wRatio, ratio);
			views.setTextViewText(R.id.wUsername, username);
			
			views.setTextColor(R.id.wUsername, context.getResources().getColor(R.color.t411_blue));
			
			if(prefs.getString("classe", "???").contains("Power Seeder"))
				views.setTextColor(R.id.wUsername, context.getResources().getColor(R.color.t411_purple));
			if(prefs.getString("classe", "???").contains("Uploader"))
				views.setTextColor(R.id.wUsername, context.getResources().getColor(R.color.t411_gold));
			if(prefs.getString("classe", "???").contains("Team Pending"))
				views.setTextColor(R.id.wUsername, context.getResources().getColor(R.color.t411_grey));
			if(prefs.getString("classe", "???").contains("Mod�rateur"))
				views.setTextColor(R.id.wUsername, context.getResources().getColor(R.color.t411_black));
			if(prefs.getString("classe", "???").contains("Super Mod�rateur"))
				views.setTextColor(R.id.wUsername, context.getResources().getColor(R.color.t411_darkred));
			if(prefs.getString("classe", "???").contains("Administrateur"))
				views.setTextColor(R.id.wUsername, context.getResources().getColor(R.color.t411_salmon));

			Log.v("widget t411", "mise à jour du smiley");
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
				
				//easter egg :) si on est le 25/12, on affiche le p�re no�l-droid
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
		Log.v("widget t411", "onReceive a re�u le Broadcast Intent");

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
			Log.e("mise � jour des donn�es dapuis le service", ex.toString());
		}
		Log.v("widget t411", "mise � jour forc�e...");
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(
				context.getPackageName(), Widget_Full.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
