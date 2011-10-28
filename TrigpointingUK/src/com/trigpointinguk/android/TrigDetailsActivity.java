package com.trigpointinguk.android;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class TrigDetailsActivity extends TabActivity {

	private static final String TAG="TrigDetailsActivity";
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.trigdetails);

		Bundle extras = getIntent().getExtras();
	    
	    Resources res = getResources();
	    TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    Intent intent;

	    intent = new Intent().setClass(this, TrigInfoTab.class);
	    intent.putExtras(extras);
	    spec = tabHost.newTabSpec("info").setIndicator("",
	                    res.getDrawable(android.R.drawable.ic_menu_info_details))
	                    .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, TrigLogsTab.class);
	    intent.putExtras(extras);
	    spec = tabHost.newTabSpec("logs").setIndicator("",
	                      res.getDrawable(android.R.drawable.ic_menu_agenda))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, TrigAlbumTab.class);
	    intent.putExtras(extras);
	    spec = tabHost.newTabSpec("album").setIndicator("",
	                      res.getDrawable(android.R.drawable.ic_menu_gallery))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, TrigOSMapTab.class);
	    intent.putExtras(extras);
	    spec = tabHost.newTabSpec("map").setIndicator("",
	                      res.getDrawable(android.R.drawable.ic_menu_mapmode))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, TrigAlbumTab.class);
	    intent.putExtras(extras);
	    spec = tabHost.newTabSpec("mylog").setIndicator("",
	                      res.getDrawable(android.R.drawable.ic_menu_edit))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    
	    tabHost.setCurrentTab(0);
	    
	    // Change title
	    // get trig_id from extras
		if (extras == null) {return;}
		Long  trigId = extras.getLong(DbHelper.TRIG_ID);
		Log.i(TAG, "Trig_id = "+trigId);

		// get trig info from database
		DbHelper mDb = new DbHelper(this);
		try {
			mDb.open();		
			Cursor c = mDb.fetchTrigInfo(trigId);
			c.moveToFirst();
				
			String title = String.format("TrigpointingUK - %s" 
					, c.getString(c.getColumnIndex(DbHelper.TRIG_NAME))
			);
			this.setTitle(title);
	        mDb.close();
		} catch (Exception e) {
		} finally {
			mDb.close();
		}
	}
}