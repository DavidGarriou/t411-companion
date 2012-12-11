package fr.lepetitpingouin.android.t411;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class readMailActivity extends Activity {

	public final String URL = "http://www.t411.me/users/login/?returnto=%2Fmailbox%2Fmail%2F%3Fid%3D";
	public final String DELURL = "http://www.t411.me/users/login/?returnto=%2Fmailbox%2Fdelete%2F%3Fid%3D";
	public final String ARCURL = "http://www.t411.me/users/login/?returnto=%2Fmailbox%2Farchive%2F%3Fid%3D";

	SharedPreferences prefs;

	ProgressDialog dialog;

	String id, message;

	WebView tvmsg;

	String t411message = "???";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msgread);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		Bundle bundle = this.getIntent().getExtras();
		id = bundle.getString("id");
		String de = bundle.getString("de");
		String objet = bundle.getString("objet");
		String date = bundle.getString("date");

		TextView tvexp = (TextView) findViewById(R.id.readMailSender);
		TextView tvobj = (TextView) findViewById(R.id.readMailSubject);
		TextView tvdate = (TextView) findViewById(R.id.readMailDate);
		tvmsg = (WebView) findViewById(R.id.www);

		tvexp.setText(de);
		tvobj.setText(objet);
		tvdate.setText(date);

		dialog = ProgressDialog.show(this,
				this.getString(R.string.getMesageContent),
				this.getString(R.string.pleasewait), true, true,
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						dialog.cancel();
						finish();
					}
				});

		ImageView BackBtn = (ImageView) findViewById(R.id.btnMailBack);
		BackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		ImageButton Delete = (ImageButton) findViewById(R.id.btnMailSuppr);
		Delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new mailDeleter().execute();
			}
		});

		ImageButton Archive = (ImageButton) findViewById(R.id.btnMailArchive);
		Archive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new mailArchiver().execute();
			}
		});

		new mailGetter().execute();
	}

	private class mailDeleter extends AsyncTask<Void, String[], Void> {

		@Override
		protected void onPreExecute() {
			Toast.makeText(
					getApplicationContext(),
					readMailActivity.this.getString(R.string.pleasewait)
							+ "...", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			// TODO Auto-generated method stub
			String username = prefs.getString("login", ""), password = prefs
					.getString("password", "");

			Connection.Response res = null;
			Document doc = null;
			try {
				res = Jsoup
						.connect(DELURL + id)
						.data("login", username, "password", password)
						.method(Method.POST)
						//.userAgent(
						//		"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(15000) // 15 sec. 
						.execute();

				doc = res.parse();

				t411message = doc.select("#messages").first().text();
				Log.v("archive link :", DELURL + id);
			} catch (Exception ex) {
				Log.e("Archivage message", ex.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(getApplicationContext(), t411message,
					Toast.LENGTH_SHORT).show();

			finish();
		}

	}

	private class mailArchiver extends AsyncTask<Void, String[], Void> {

		@Override
		protected void onPreExecute() {
			Toast.makeText(
					getApplicationContext(),
					readMailActivity.this.getString(R.string.pleasewait)
							+ "...", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			// TODO Auto-generated method stub
			String username = prefs.getString("login", ""), password = prefs
					.getString("password", "");

			Connection.Response res = null;
			Document doc = null;
			try {
				res = Jsoup
						.connect(ARCURL + id)
						.data("login", username, "password", password)
						.method(Method.POST)
						//.userAgent(
						//		"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(15000) // 15 sec
						.execute();

				doc = res.parse();

				t411message = doc.select("#messages").first().text();
				Log.v("archive link :", ARCURL + id);
			} catch (Exception ex) {
				Log.e("Archivage message", ex.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(getApplicationContext(), t411message,
					Toast.LENGTH_SHORT).show();

			finish();
		}

	}

	private class mailGetter extends AsyncTask<Void, String[], Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String username = prefs.getString("login", ""), password = prefs
					.getString("password", "");

			Connection.Response res = null;
			Document doc = null;
			Log.v("URL", URL + id);
			try {
				res = Jsoup
						.connect(URL + id)
						.data("login", username, "password", password)
						.method(Method.POST)
						//.userAgent(
						//		"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(15000) // timeout illimité
						.execute();

				doc = res.parse();

				message = doc.select(".msg").first().html();

				final String mimeType = "text/html";
				final String encoding = "utf-8";

				tvmsg.loadDataWithBaseURL(null, message, mimeType, encoding, "");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Erreur connect :", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
		}
	}
}
