package fr.lepetitpingouin.android.t411;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final int ID_NOTIFICATION = 1990;
		
	String conError;
	
	LinearLayout connectButton, mailButton, notifButton, profileButton, aboutButton, btnUpdate;
	EditText loginField, passwordField;
	
	String cookie;
	
	public void onReceive(Context context, Intent intent) {
		//Toast.makeText(context, "onReceiveApp", Toast.LENGTH_SHORT).show();
		Log.v("Receive","OK!");
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnUpdate = (LinearLayout)findViewById(R.id.btnHomeUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				update();
			}
		});
        
        mailButton = (LinearLayout)findViewById(R.id.btnMails);
        mailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(MainActivity.this, messagesActivity.class);
        		MainActivity.this.startActivity(myIntent);            }
        });
        
        connectButton = (LinearLayout)findViewById(R.id.btnService);
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(MainActivity.this, Settings.class);
        		MainActivity.this.startActivity(myIntent);
            }
        });
        
        profileButton = (LinearLayout)findViewById(R.id.btnUseraccount);
        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(MainActivity.this, userLogin.class);
            	try {
        		MainActivity.this.startActivity(myIntent);
            	} catch(Exception ex) {Log.e("lancement du profil",ex.toString());}
            }
        });
        
        aboutButton = (LinearLayout)findViewById(R.id.LinearLayout01);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(MainActivity.this, aboutActivity.class);
        		MainActivity.this.startActivity(myIntent);            
        		}
        });
        
    }
    
    public void update() {
    	Intent myIntent = new Intent(MainActivity.this, t411updater.class);
		MainActivity.this.startService(myIntent);
    	//Toast.makeText(getApplicationContext(), "coming soon", Toast.LENGTH_SHORT).show();
    }
}
