package com.trigpointinguk.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TrigDetailsOSMapTab extends Activity {
	private static final String TAG = "TrigDetailsOSMapTab";

	private long mTrigId;
	private DbHelper mDb;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trigosmap);

		// get trig_id from extras
        Bundle extras = getIntent().getExtras();
		if (extras == null) {return;}
		mTrigId = extras.getLong(DbHelper.TRIG_ID);
		Log.i(TAG, "Trig_id = "+mTrigId);
		
		// get trig info from database
		mDb = new DbHelper(TrigDetailsOSMapTab.this);
		mDb.open();		
		Cursor c = mDb.fetchTrigInfo(mTrigId);
		c.moveToFirst();
		String[] urls = getURLs(mTrigId, c);
		c.close();
		
	    Gallery gallery = (Gallery) findViewById(R.id.trigosgallery);
	    gallery.setAdapter(new TrigDetailsOSMapAdapter(this, urls));

	    
	    
	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(TrigDetailsOSMapTab.this, "Image " + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	
	
	public String[] getURLs (Long trigid, Cursor c) {
		String url;
		List<String> URLs = new ArrayList<String>();
		
		Double lat = c.getDouble(c.getColumnIndex(DbHelper.TRIG_LAT));
		Double lon = c.getDouble(c.getColumnIndex(DbHelper.TRIG_LON));

		// OS 1:25000 maps
		url = String.format("%s/%s/%3.5f,%3.5f/%d?key=%s",
				"http://dev.virtualearth.net/REST/v1/Imagery/Map",
				"OrdnanceSurvey",
				lat, lon,
				13,
				"AmX-6eFz_aE2rrhkXUprU3HRV2BNMrCYQoKodIFdfNEcZosjAEbsNetB00GFktP5");
		URLs.add(url);

		url = String.format("%s/%s/%3.5f,%3.5f/%d?key=%s",
				"http://dev.virtualearth.net/REST/v1/Imagery/Map",
				"OrdnanceSurvey",
				lat, lon,
				15,
				"AmX-6eFz_aE2rrhkXUprU3HRV2BNMrCYQoKodIFdfNEcZosjAEbsNetB00GFktP5");
		URLs.add(url);

		// Aerial photos
		url = String.format("%s/%s/%3.5f,%3.5f/%d?key=%s",
				"http://dev.virtualearth.net/REST/v1/Imagery/Map",
				"Aerial",
				lat, lon,
				14,
				"AmX-6eFz_aE2rrhkXUprU3HRV2BNMrCYQoKodIFdfNEcZosjAEbsNetB00GFktP5");
		URLs.add(url);

		url = String.format("%s/%s/%3.5f,%3.5f/%d?key=%s",
				"http://dev.virtualearth.net/REST/v1/Imagery/Map",
				"Aerial",
				lat, lon,
				17,
				"AmX-6eFz_aE2rrhkXUprU3HRV2BNMrCYQoKodIFdfNEcZosjAEbsNetB00GFktP5");
		URLs.add(url);

		url = String.format("%s/%s/%3.5f,%3.5f/%d?key=%s",
				"http://dev.virtualearth.net/REST/v1/Imagery/Map",
				"Aerial",
				lat, lon,
				19,
				"AmX-6eFz_aE2rrhkXUprU3HRV2BNMrCYQoKodIFdfNEcZosjAEbsNetB00GFktP5");
		URLs.add(url);

		
		return URLs.toArray(new String[URLs.size()]);
	}
}
