package fr.lepetitpingouin.android.t411;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class t411updater extends Service {

	public Integer mails, oldmails;
	public double ratio;
	public String upload, download, username, conError = "";

	AlarmManager alarmManager;
	PendingIntent pendingIntent;

	SharedPreferences prefs;

	int freq; // fréquence de rafraichissement (en minutes)

	Intent i = new Intent("android.appwidget.action.APPWIDGET_UPDATE");

	// La page de login t411 :
	static final String CONNECTURL = "http://www.t411.me/users/login/";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("Service t411", "onStartCommand");

		// on charge les préférences de l'application
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		// notifier le début de la mise à jour, si l'option a été cochée
		if (prefs.getBoolean("updAlert", false))
			createNotify(1992, R.drawable.ic_stat_updating,
					this.getString(R.string.notif_update),
					this.getString(R.string.notif_upd_title),
					this.getString(R.string.notif_upd_content), false,
					t411updater.class);

		try {
			// Détection du changement de configuration utilisateur : si la
			// config a changé, on dégomme le cookie
			update(prefs.getString("login", ""),
					prefs.getString("password", ""));
		} catch (Exception ex) {
			Log.v("Credentials :",
					prefs.getString("login", "") + ":"
							+ prefs.getString("password", ""));
			Log.e("update", ex.toString());
		}

		Intent myIntent = new Intent(t411updater.this, t411updater.class);
		pendingIntent = PendingIntent.getService(t411updater.this, 0, myIntent,
				0);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		freq = Integer.valueOf(prefs.getString("updateFreq", "15"));
		Log.v("Fréquence avant vérification", String.valueOf(freq));
		freq = (freq < 1) ? 1 : freq;
		Log.v("Fréquence après vérification", String.valueOf(freq));

		// on définit une instance de Calendar selon la configuration
		// utilisateur (15 mn par défaut)
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MINUTE, freq);

		try {
			// on annule l'alarme existante
			alarmManager.cancel(pendingIntent);

			// ...et on la reprogramme, si la config utilisateur le permet
			if (prefs.getBoolean("autoUpdate", false))
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), pendingIntent);
		} catch (Exception ex) {
			Log.e("AlarmManager", ex.toString());
		}

		try {
			// annuler la notification de mise à jour
			cancelNotify(1992);
		} finally {
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(this.getClass().getName(), "onDestroy");
		// à l'arret, annuler les répétitions
		alarmManager.cancel(pendingIntent);
	}

	public void update(String login, String password) throws IOException {
		Connection.Response res = null;
		Document doc = null;
		// on exécute la requête HTTP, en passant le login et le password en
		// POST.
		res = Jsoup
				.connect(CONNECTURL)
				.data("login", login, "password", password)
				.method(Method.POST)
				.userAgent(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
				.timeout(0) // timeout illimité
				.execute();

		doc = res.parse();

		// on récupère l'erreur de connexion, le cas échéant
		try {
			conError = doc.select("#messages").first().text();
		} catch (Exception ex) {
			Log.e("conError", ex.toString());
		}
		if (conError != "") {
			Toast.makeText(getApplicationContext(), conError, Toast.LENGTH_LONG)
					.show();
		} else {
			// on récupère le ratio, sous forme de Double à 2 décimales
			ratio = Math.round(Float.valueOf(doc.select(".rate").first().text()
					.replace(',', '.')) * 100.0) / 100.0;

			username = doc.select(".avatar-big").attr("alt");
			Log.v("username :", username);

			// on récupère la chaine de l'upload sans la traiter, avec la flèche
			// et l'unité
			upload = doc.select(".up").first().text();
			Log.v("upload :", upload);

			// idem pour le download
			download = doc.select(".down").first().text();
			Log.v("download :", download);

			// et enfin le nombre de mails, sous forme d'entier
			oldmails = (mails != null) ? mails : prefs.getInt("lastMails", 0);
			Log.v("mails (avant check) :", oldmails.toString());
			mails = Integer.valueOf(doc.select(".mail  > strong").first()
					.text());
			Log.v("mails (après check) :", mails.toString());

			// on stocke tout ce petit monde (si non nul) dans les préférences
			Editor editor = prefs.edit();
			if (mails != null)
				editor.putInt("lastMails", mails);
			if (upload != null)
				editor.putString("lastUpload", upload);
			if (download != null)
				editor.putString("lastDownload", download);
			if (ratio != Double.NaN)
				editor.putString("lastRatio", String.valueOf(ratio));

			Date date = new Date();
			editor.putString("lastDate", date.toLocaleString());
			editor.putString("lastUsername", username);

			editor.commit();

			Log.v("t411 Error :", conError);
			Log.v("INFOS T411 :", "Mails (" + String.valueOf(mails) + ") "
					+ upload + " " + download + " " + String.valueOf(ratio));

			try {
				i.putExtra("ratio", String.valueOf(ratio));
				i.putExtra("upload", upload);
				i.putExtra("download", download);
				i.putExtra("mails", String.valueOf(mails));
				i.putExtra("username", username);
				Log.v("t41updater", "Envoi du Broadcast Intent");
				sendBroadcast(i);
				doNotify();
			} catch (Exception ex) {
				Log.v("Broadcast Sender", ex.toString());
			}
		}

	}

	public void doNotify() {
		if (prefs.getBoolean("ratioAlert", false))
			if (ratio < Double.valueOf(prefs.getString("ratioMinimum", "0")))
				createNotify(1990, R.drawable.ic_stat_ratio,
						this.getString(R.string.notif_ratio),
						this.getString(R.string.notif_ratio_title) + " (< "
								+ prefs.getString("ratioMinimum", "???") + ")",
						this.getString(R.string.notif_ratio_content), true,
						t411updater.class);
			else
				cancelNotify(1990);
		if (prefs.getBoolean("mailAlert", false))
			if (mails > oldmails)
				createNotify(1991, R.drawable.ic_stat_message,
						this.getString(R.string.notif_message),
						this.getString(R.string.notif_msg_title),
						this.getString(R.string.notif_msg_content), true,
						messagesActivity.class);
			else
				cancelNotify(1991);
	}

	private void createNotify(int number, int icon, String label, String title,
			String description, boolean vibrate, Class<?> cls) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(icon, label,
				System.currentTimeMillis());

		if (cls != null)
			pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					cls), 0);
		String titreNotification = title;
		String texteNotification = description;

		notification.setLatestEventInfo(this, titreNotification,
				texteNotification, pendingIntent);

		if (vibrate == true) {
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
		}

		notificationManager.notify(number, notification);
	}

	private void cancelNotify(int number) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(number);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("Binder", intent.toString());
		return null;
	}
}
