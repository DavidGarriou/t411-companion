package fr.lepetitpingouin.android.t411;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class userLogin extends Activity {
	// on définit un gestionnaire de préférences
	SharedPreferences prefs;

	// aussi, on définit un modificateur de préférences
	Editor editor;

	// et on prépare les champs de l'interface
	EditText login, passwd;
	Button btn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		login = (EditText) findViewById(R.id.input_username);
		passwd = (EditText) findViewById(R.id.input_password);

		// on remplit les champs avec les données de notre gestionnaire de
		// préférences
		login.setText(prefs.getString("login", null));
		passwd.setText(prefs.getString("password", null));

		btn = (Button) findViewById(R.id.btn_connect);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				save();
			}
		});

	}

	public void save() {
		editor = prefs.edit();
		editor.putString("login", login.getText().toString());
		editor.putString("password", passwd.getText().toString());
		if (editor.commit()) {
			Toast.makeText(this, getString(R.string.savedData),
					Toast.LENGTH_SHORT).show();
			startService(new Intent(userLogin.this, t411updater.class));
			this.finish();
		}
	}
}
