package fr.lepetitpingouin.android.t411;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class statsActivity extends Activity {
	// on définit un gestionnaire de préférences
	SharedPreferences prefs;

	WebView www;
	
	String scripts = "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.js\" type=\"text/javascript\"></script><script src=\"http://code.highcharts.com/highcharts.js\" type=\"text/javascript\"></script>";
	// aussi, on définit un modificateur de préférences
	Editor editor;
	String pagecontent;

	// et on prépare les champs de l'interface
	ImageView topLogo;
	static final String STATSURL = "http://www.t411.me/users/login/?returnto=%2Fusers%2Fdaily-stats%2F?id=";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		
		www = (WebView) findViewById(R.id.webView1);
		www.getSettings().setJavaScriptEnabled(true); 
		//www.getSettings().setDomStorageEnabled(true);
		www.setWebViewClient(new WebViewClient());
		www.setWebChromeClient(new WebChromeClient());

		topLogo = (ImageView) findViewById(R.id.topLogo);
		topLogo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		try {
		new grapher().execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("erreur tâche", e.toString());
			Toast.makeText(getApplicationContext(),
					"Erreur lors de la récupération des messages...",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private class grapher extends AsyncTask<Void, String[], Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			www.clearView();
			pagecontent = "Error.";
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String username = prefs.getString("login", ""), password = prefs
					.getString("password", "");
			Log.v("Credentials :", username + "/" + password);

			Connection.Response res = null;
			Document doc = null;

			try {
				res = Jsoup
						.connect(STATSURL+prefs.getString("usernumber", "0"))
						.data("login", username, "password", password)
						.method(Method.POST)
						.userAgent(
								"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(10000) 
						.execute();
				doc = res.parse();

				try {
					//pagecontent = doc.select("div.content").outerHtml();
					pagecontent = "<html><head>"+scripts+"</head><body><div id=\"chart\" style=\"height: 100%;\"></div><script>"+doc.select("div.content > script").get(2).html()+"</script></body></html>";
					Log.d("page content :", pagecontent);
				} catch (Exception ex) {
					Log.e("Erreur get tabledata", ex.toString());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("erreur", e.toString());
			}
			//dialog.cancel();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			www.loadDataWithBaseURL(null,pagecontent, "text/html", "utf-8",null);
			
		}
	}
}
