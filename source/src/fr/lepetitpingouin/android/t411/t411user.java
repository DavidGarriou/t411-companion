package fr.lepetitpingouin.android.t411;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class t411user {
	
	String username;
	double upload, download, lastUpload, lastDownload;
	int messages;
	SharedPreferences prefs;
	Editor edit;
	Context context;
	
	public t411user(Context context) {
		prefs = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		edit = prefs.edit();
		this.context = context;
		refresh();
	}
	
	public void refresh() {
		this.username = prefs.getString("login", context.getString(R.string.waiting_for_update));
		this.upload = getBytesFromData(prefs.getString("lastUpload", "Data not found."));
		this.download = getBytesFromData(prefs.getString("lastDownload", "Data not found."));
	}
	
	private double getBytesFromData(String Data) {
		String[] array = Data.split(" ");
		String unit = array[2];
		String value = array[1];
		double result = 0;
		
		try {
			if(unit.equals("TB"))
				result = Double.valueOf(value)*1024*1024*1024*1024;
			if(unit.equals("GB"))
				result = Double.valueOf(value)*1024*1024*1024;
			if(unit.equals("MB"))
				result = Double.valueOf(value)*1024*1024;
		}
		catch (Exception ex) {
			Log.e("getBytesFromData returned an error :",ex.toString());
		}
				
		return result;
	}
	
	public void setUploadValue(double uploadValue) {
		edit.putLong("lastUpload", (long) this.upload);
		this.upload = uploadValue;
		edit.putLong("upload", (long) upload);
		edit.commit();
	}
	
	public double getUploadValue() {
		return this.upload;
	}
	
	public String getUploadAsText() {
		String sUpload = String.valueOf(this.upload)+"o";
		
		return sUpload;
	}
	
	public void setDownloadValue(double downloadValue) {
		edit.putLong("lastDownload", (long) this.download);
		this.download = downloadValue;
		edit.putLong("download", (long) download);
		edit.commit();
	}
	
	public double getDownloadValue() {
		return this.download;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getRatioAsText() {
		return String.format("%.2f", (this.upload/this.download));
	}

}
