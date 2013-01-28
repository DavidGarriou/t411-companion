package fr.lepetitpingouin.android.t411;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final int ID_NOTIFICATION = 1990;

	String conError;

	LinearLayout searchButton, connectButton, mailButton, notifButton, profileButton,
			aboutButton, btnUpdate, btnStats, hWidget;
	TextView hLogin, hMails, hUpDown, hRatio, hGoLeft, hUpLeft;
	Context context;

	String cookie;

	SharedPreferences prefs;

	public void onReceive(Context context, Intent intent) {
		// Toast.makeText(context, "onReceiveApp", Toast.LENGTH_SHORT).show();
		Log.v("Receive", "OK!");
	}

	public void onPause(Context context) {
		super.onPause();
	}
	
	public void onResume(Context context) {
		super.onResume();
		updateValues();
		Log.v("update", "updateValues();");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		context = this.getApplicationContext();
		updateValues();

		if (prefs.getString("login", null) == null) {
			Intent myIntent = new Intent(MainActivity.this, userLogin.class);
			MainActivity.this.startActivity(myIntent);
		}

		searchButton = (LinearLayout) findViewById(R.id.btnSearch);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(MainActivity.this,
						SearchActivity.class);
				MainActivity.this.startActivity(myIntent);
			}
		});
		
		btnUpdate = (LinearLayout) findViewById(R.id.btnHomeUpdate);
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				update();
			}
		});

		mailButton = (LinearLayout) findViewById(R.id.btnMails);
		mailButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on click
				Intent myIntent = new Intent(MainActivity.this,
						messagesActivity.class);
				MainActivity.this.startActivity(myIntent);
			}
		});
		
		btnStats = (LinearLayout) findViewById(R.id.btnStats);
		btnStats.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on click
				Intent myIntent = new Intent(MainActivity.this,
						statsActivity.class);
				MainActivity.this.startActivity(myIntent);
			}
		});

		hWidget = (LinearLayout) findViewById(R.id.hWidget);
		hWidget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateValues();
			}
		});
		
		connectButton = (LinearLayout) findViewById(R.id.btnService);
		connectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on click
				Intent myIntent = new Intent(MainActivity.this, Settings.class);
				MainActivity.this.startActivity(myIntent);
			}
		});

		profileButton = (LinearLayout) findViewById(R.id.btnUseraccount);
		profileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on click
				Intent myIntent = new Intent(MainActivity.this, userLogin.class);
				try {
					MainActivity.this.startActivity(myIntent);
				} catch (Exception ex) {
					Log.e("lancement du profil", ex.toString());
				}
			}
		});

		aboutButton = (LinearLayout) findViewById(R.id.LinearLayout01);
		aboutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on click
				Intent myIntent = new Intent(MainActivity.this,
						aboutActivity.class);
				MainActivity.this.startActivity(myIntent);
			}
		});

	}

	public void updateValues() {

		hLogin = (TextView) findViewById(R.id.hLogin);
		hLogin.setText(prefs.getString("lastUsername", "???"));

		hMails = (TextView) findViewById(R.id.hMails);
		hMails.setText(String.valueOf(prefs.getInt("lastMails", 0)));

		hUpDown = (TextView) findViewById(R.id.hUpDown);
		hUpDown.setText(prefs.getString("lastUpload", "???.?? GB") + " / "
				+ prefs.getString("lastDownload", "???.?? GB"));

		hRatio = (TextView) findViewById(R.id.hRatio);
		hRatio.setText(prefs.getString("lastRatio", "??.??"));
		
		hGoLeft = (TextView) findViewById(R.id.hGoLeft);
		hGoLeft.setText(prefs.getString("GoLeft", "?.?? GB"));
		
		hUpLeft = (TextView) findViewById(R.id.hUpLeft);
		hUpLeft.setText(prefs.getString("UpLeft", "?.?? GB"));
		
		hLogin.setTextColor(context.getResources().getColor(R.color.t411_blue));
		
		if(prefs.getString("classe", "???").contains("Power Seeder"))
			hLogin.setTextColor(context.getResources().getColor(R.color.t411_purple));
		if(prefs.getString("classe", "???").contains("Uploader"))
			hLogin.setTextColor(context.getResources().getColor(R.color.t411_gold));
		if(prefs.getString("classe", "???").contains("Team Pending"))
			hLogin.setTextColor(context.getResources().getColor(R.color.t411_grey));
		if(prefs.getString("classe", "???").contains("Modérateur"))
			hLogin.setTextColor(context.getResources().getColor(R.color.t411_black));
		if(prefs.getString("classe", "???").contains("Super Modérateur"))
			hLogin.setTextColor(context.getResources().getColor(R.color.t411_darkred));
		if(prefs.getString("classe", "???").contains("Administrateur"))
			hLogin.setTextColor(context.getResources().getColor(R.color.t411_salmon));

	}
	
	

	public void update() {
		try {
			stopService(new Intent(MainActivity.this, t411updater.class));
		} catch (Exception ex) {
			Log.e("erreur service", ex.toString());
		}
		startService(new Intent(MainActivity.this, t411updater.class));
		
		updateValues();
	}
}
