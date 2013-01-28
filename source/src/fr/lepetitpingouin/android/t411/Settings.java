package fr.lepetitpingouin.android.t411;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Settings extends Activity {

	// on définit un gestionnaire de préférences
	SharedPreferences prefs;

	// aussi, on définit un modificateur de préférences
	Editor editor;

	// et on prépare les champs de l'interface
	CheckBox ratioAlert, mailAlert, notifAlert, autoUpdate;
	EditText minimum, frequency, targetR;
	LinearLayout btnStartService, btnWidgetAction;
	ImageView topLogo;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		editor = prefs.edit();

		btnStartService = (LinearLayout) findViewById(R.id.btnStartService);
		btnStartService.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on click
				try {
					stopService(new Intent(Settings.this, t411updater.class));
				} catch (Exception ex) {
					Log.e("erreur service", ex.toString());
				}
				startService(new Intent(Settings.this, t411updater.class));
			}
		});

		final CharSequence[] items = { this.getString(R.string.open_app),
				this.getString(R.string.update_now),
				this.getString(R.string.read_mails),
				this.getString(R.string.goto_t411),
				this.getString(R.string.askWhatToDo) };
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.ChooseAction));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int item) {
				Toast.makeText(getApplicationContext(), "Enregistré.",
						Toast.LENGTH_SHORT).show();
				editor.putInt("widgetAction", item);
				editor.commit();
				startService(new Intent(Settings.this, t411updater.class));

				return;
			}
		});
		
		topLogo = (ImageView) findViewById(R.id.topLogo);
		topLogo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btnWidgetAction = (LinearLayout) findViewById(R.id.btnWidgetAction);
		btnWidgetAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on click
				builder.create().show();
			}
		});

		autoUpdate = (CheckBox) findViewById(R.id.checkBox0);
		autoUpdate.setChecked(prefs.getBoolean("autoUpdate", false));
		autoUpdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				editor.putBoolean("autoUpdate", arg1);
				editor.commit();
				Intent myIntent = new Intent(Settings.this, t411updater.class);
				if (prefs.getBoolean("autoUpdate", false))
					Settings.this.startService(myIntent);
			}
		});

		ratioAlert = (CheckBox) findViewById(R.id.checkBox1);
		ratioAlert.setChecked(prefs.getBoolean("ratioAlert", false));
		ratioAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				editor.putBoolean("ratioAlert", arg1);
				editor.commit();
				Intent myIntent = new Intent(Settings.this, t411updater.class);
				if (prefs.getBoolean("autoUpdate", false))
					Settings.this.startService(myIntent);
			}
		});

		minimum = (EditText) findViewById(R.id.editText1);
		minimum.setText(prefs.getString("ratioMinimum", "1"));
		minimum.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

				// you can call or do what you want with your EditText here
				editor.putString("ratioMinimum", s.toString());
				editor.commit();
				// Toast.makeText(getApplicationContext(), s.toString(),
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}
		});
		
		targetR = (EditText) findViewById(R.id.EditText01);
		targetR.setText(prefs.getString("ratioCible", "1"));
		targetR.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

				// you can call or do what you want with your EditText here
				editor.putString("ratioCible", s.toString());
				editor.commit();
				// Toast.makeText(getApplicationContext(), s.toString(),
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}
		});

		mailAlert = (CheckBox) findViewById(R.id.checkBox2);
		mailAlert.setChecked(prefs.getBoolean("mailAlert", false));
		mailAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				editor.putBoolean("mailAlert", arg1);
				editor.commit();
			}
		});

		frequency = (EditText) findViewById(R.id.field_frequency);
		frequency.setText(prefs.getString("updateFreq", "15"));
		frequency.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

				// you can call or do what you want with your EditText here
				editor.putString("updateFreq", s.toString());
				editor.commit();
				// Toast.makeText(getApplicationContext(), s.toString(),
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}
		});

		notifAlert = (CheckBox) findViewById(R.id.checkBox3);
		notifAlert.setChecked(prefs.getBoolean("updAlert", false));
		notifAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				editor.putBoolean("updAlert", arg1);
				editor.commit();
			}
		});
	}
}