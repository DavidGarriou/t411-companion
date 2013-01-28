package fr.lepetitpingouin.android.t411;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SearchActivity extends Activity {
	
	

	SharedPreferences prefs;

	public void onPause(Context context) {
		super.onPause();
	}
	
	public void onResume(Context context) {
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
	}
}
