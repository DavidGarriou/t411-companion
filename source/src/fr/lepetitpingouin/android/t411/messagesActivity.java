package fr.lepetitpingouin.android.t411;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class messagesActivity extends Activity {
	static final String CONNECTURL = "http://www.t411.me/users/login/?returnto=%2Fmailbox%2F";
	public ProgressDialog dialog;
	
	SharedPreferences prefs;
	
	Map<String,String> cookies;
	
	//On déclare la HashMap qui contiendra les informations pour un item
    HashMap<String, String> map;
    
    ListView maListViewPerso;
    ArrayList<HashMap<String, String>> listItem;
	
    //@Override
    protected void onResume()
    {
    	update();
    	super.onResume();
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msglist);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
      //Récupération de la listview créée dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.malistviewperso);
 
        //Création de la ArrayList qui nous permettra de remplire la listView
        listItem = new ArrayList<HashMap<String, String>>();
 
        LinearLayout btnMailUpdate = (LinearLayout) findViewById(R.id.btnMailUpdate);
        btnMailUpdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				update();
			}
		});
        
        //Création d'une HashMap pour insérer les informations du premier item de notre listView
        map = new HashMap<String, String>();
        map.put("de", "Terminator");
        map.put("objet", "Sarah Connor ?");
        map.put("etat", String.valueOf(R.drawable.mail_unread));
        map.put("id", "123456543234");
        map.put("date", "31/07/2342");
        listItem.add(map);
        
        ImageView BackBtn = (ImageView)findViewById(R.id.inboxBack);
        BackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        
        // c'était un test, là on le vire avec la mise à jour ;)
        //update();
    }
	
	public void update() {
		dialog = ProgressDialog.show(this, this.getString(R.string.getMesages), this.getString(R.string.pleasewait), true, true);
		
        new mailFetcher().execute(); 
	}
	
	
	
	private class mailFetcher extends AsyncTask<Void, String[], Void>{

		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			listItem.clear();
			
			
			String username = prefs.getString("login", ""), password = prefs.getString("password", "");
			Log.v("Credentials :",username+"/"+password);
			
			Connection.Response res = null;
			Document doc = null;

				try {
					res = Jsoup.connect(CONNECTURL)
							.data("login",username,"password",password)
							.method(Method.POST)
							.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
							.timeout(0) //timeout illimité
							.execute();
					doc = res.parse();
					
					try{
					for (Element table : doc.select("table.mailbox tbody")) {
				        for (Element row : table.select("tr")) {
				            Elements tds = row.select("td");
				            //Log.e("Test TD = ",tds.get(1).text());
				            int mailStatus;
				            if(row.hasAttr("class"))
				            	mailStatus = R.drawable.mail_unread;
				            else
				            	mailStatus = R.drawable.mail_read;
				            Log.v("export de la ligne "+tds.get(2).text(),row.className());
				            map = new HashMap<String, String>();
					        map.put("de", tds.get(1).text());
					        map.put("objet", tds.get(2).text());
					        map.put("etat", String.valueOf(mailStatus));
					        map.put("id", tds.get(0).select("input").val());
					        map.put("date", tds.get(3).text());
					        listItem.add(map);
				        }
				    }
					}
					catch(Exception ex) {
						Log.e("Erreur test TD",ex.toString());
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("erreur",e.toString());
					Toast.makeText(getApplicationContext(), "Erreur lors de la récupération des messages...", Toast.LENGTH_SHORT).show();
				}
			dialog.cancel();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			//Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
	        SimpleAdapter mSchedule = new SimpleAdapter (messagesActivity.this.getBaseContext(), listItem, R.layout.item_msglist,
	               new String[] {"de", "objet", "etat", "date"}, new int[] {R.id.fromuser, R.id.mailsubject, R.id.mailstate, R.id.maildate});
	 
	        //On attribut à notre listView l'adapter que l'on vient de créer
	        maListViewPerso.setAdapter(mSchedule);
	 
	        //Enfin on met un écouteur d'évènement sur notre listView
	        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
				@Override
	        	@SuppressWarnings("unchecked")
	         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
					//on récupère la HashMap contenant les infos de notre item (titre, description, img)
	        		HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
	        		
	        		Intent myIntent = new Intent();
	        		myIntent.setClass(getApplicationContext(), readMailActivity.class);
	        		Bundle bundle = new Bundle();
	        		bundle.putString("id", map.get("id"));
	        		bundle.putString("de", map.get("de"));
	        		bundle.putString("objet", map.get("objet"));
	        		bundle.putString("date", map.get("date"));
	        		myIntent.putExtras(bundle);
	        		startActivity(myIntent);
	        	}
	         });	    
	        }
	}
}

