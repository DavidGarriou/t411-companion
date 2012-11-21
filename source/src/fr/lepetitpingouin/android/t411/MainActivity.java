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
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int ID_NOTIFICATION = 1990;

	String conError;

	LinearLayout connectButton, mailButton, notifButton, profileButton,
			aboutButton, btnUpdate;
	TextView hLogin,hMails,hUpDown,hRatio;

	String cookie;
	
	SharedPreferences prefs;

	public void onReceive(Context context, Intent intent) {
		// Toast.makeText(context, "onReceiveApp", Toast.LENGTH_SHORT).show();
		Log.v("Receive", "OK!");
	}
	
	public void onResume(Context context) {
		super.onResume();
		updateValues();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		updateValues();
		
		if(prefs.getString("login", null) == null) {
			Intent myIntent = new Intent(MainActivity.this,
					userLogin.class);
			MainActivity.this.startActivity(myIntent);
		}

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
				
		hLogin = (TextView)findViewById(R.id.hLogin);
		hLogin.setText(prefs.getString("lastUsername", "???"));
		
		hMails = (TextView)findViewById(R.id.hMails);
		hMails.setText(String.valueOf(prefs.getInt("lastMails", 0)));
		
		hUpDown = (TextView)findViewById(R.id.hUpDown);
		hUpDown.setText(prefs.getString("lastUpload", "???.?? GB") + " / " + prefs.getString("lastDownload", "???.?? GB"));
		
		hRatio = (TextView)findViewById(R.id.hRatio);
		hRatio.setText(prefs.getString("lastRatio", "??.??"));
	}

	public void update() {
		Intent myIntent = new Intent(MainActivity.this, t411updater.class);
		MainActivity.this.startService(myIntent);
		// Toast.makeText(getApplicationContext(), "coming soon",
		// Toast.LENGTH_SHORT).show();
		updateValues();
	}
}
