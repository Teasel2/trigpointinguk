package com.trigpointinguk.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.trigpointinguk.android.common.StringLoader;

public class TrigDetailsAlbumTab extends ListActivity {
	private long mTrigId;
	private static final String TAG="TrigDetailsAlbumTab";
	private StringLoader 			mStrLoader;
    private ArrayList<TrigPhoto> 	mTrigPhotos;
    private TrigDetailsAlbumAdapter mTrigAlbumAdapter;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trigalbum);
        
        // get trig_id from extras
        Bundle extras = getIntent().getExtras();
		if (extras == null) {return;}
		mTrigId = extras.getLong(DbHelper.TRIG_ID);
		Log.i(TAG, "Trig_id = "+mTrigId);

        // attach the array adapter
		mTrigPhotos = new ArrayList<TrigPhoto>();
        mTrigAlbumAdapter = new TrigDetailsAlbumAdapter(TrigDetailsAlbumTab.this, R.layout.trigalbumrow, mTrigPhotos); 
		setListAdapter(mTrigAlbumAdapter);

		// get list of photos
        new PopulatePhotosTask().execute();	    
    }
    
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String url = mTrigPhotos.get(position).getPhotoURL();
        Intent i = new Intent(this, DisplayBitmapActivity.class);
        i.putExtra("URL", url);
        Log.i(TAG, "Clicked photo at URL: " +url);
        startActivity(i);
    }
	
	
    
    
    
	private class PopulatePhotosTask extends AsyncTask<Void, Integer, Integer> {
		protected Integer doInBackground(Void... arg0) {
			int count=0;
			mTrigPhotos.clear();
			
	        String url = String.format("http://www.trigpointinguk.com/trigs/down-android-trigphotos.php?t=%d", mTrigId);
	        String list = mStrLoader.getString(url, false);
	        if (list == null || list.trim().length()==0) {
	    		Log.i(TAG, "No photos for "+mTrigId);        	
	    		return count;
	        }

	        TrigPhoto tp;

	        String[] lines = list.split("\n");
			Log.i(TAG, "Photos found : "+lines.length);
			
	        for (String line : lines) {
	        	if (!(line.trim().equals(""))) { 
	        		String[] csv = line.split("\t");
	        		try {
	        			tp = new TrigPhoto(
	        					csv[2],		//name 
	        					csv[3], 	//descr
	        					csv[1],		//photo
	        					csv[0],		//icon
	        					csv[5],		//user
	        					csv[4]);	//date
	        			mTrigPhotos.add(tp);
	        			count++;
	        		} catch (Exception e) {
	        			System.out.println(e);
	        		}
	        	}
	        }
	        return count;
		}
		protected void onPreExecute() {
			// create string loader class
	        mStrLoader = new StringLoader(TrigDetailsAlbumTab.this);
		}
		protected void onProgressUpdate(Integer... progress) {
		}
		protected void onPostExecute(Integer arg0) {
	        mTrigAlbumAdapter.notifyDataSetChanged();
		}
	}

    
}