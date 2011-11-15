package com.trigpointinguk.android;

import org.osmdroid.util.BoundingBoxE6;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

public class DbHelper {
	private static final String TAG					= "DbHelper";

	private static final int 	DATABASE_VERSION 	= 4;
	private static final String DATABASE_NAME		= "trigpointinguk";
	private static final String TRIG_TABLE			= "trig";
	public 	static final String TRIG_ID				= "_id";
	public 	static final String TRIG_NAME			= "name";
	public 	static final String TRIG_WAYPOINT		= "waypoint";
	public 	static final String TRIG_LAT			= "lat";
	public 	static final String TRIG_LON			= "lon";
	public 	static final String TRIG_TYPE			= "type";
	public 	static final String TRIG_CONDITION		= "condition";
	public 	static final String TRIG_LOGGED			= "logged";
	public 	static final String TRIG_CURRENT		= "current";
	public 	static final String TRIG_HISTORIC		= "historic";
	public 	static final String TRIG_FB				= "fb";
	public  static final String DEFAULT_MAP_COUNT   = "400";

	private static final String TRIG_CREATE = "create table trig (_id integer primary key, "
		+ "name text not null, waypoint text not null, "
		+ "lat real not null, lon real not null, " 
		+ "type integer not null, condition char(1) not null, logged condition char(1) not null, "
		+ "current integer not null, historic integer not null, fb text);";

	private DatabaseHelper mDbHelper;
	public SQLiteDatabase mDb;
    private SharedPreferences mPrefs; 
    	
	private final Context mCtx;


	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating database");
			db.execSQL(TRIG_CREATE);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS trig");
			onCreate(db);
		}
	}
	
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
	public DbHelper(Context ctx) {
		this.mCtx = ctx;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
	}

	/**
	 * Open the trigpointinguk database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an initialisation call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public DbHelper open() throws SQLException {
		Log.i(TAG, "Opening mDbHelper");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		Log.i(TAG, "Closing mDbHelper");
		mDbHelper.close();
	}


	/**
	 * Create a new trig using the title and body provided. If the trig is
	 * successfully created return the new rowId, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param id
	 * @return rowId or -1 if failed
	 */
	public long createTrig(int id, String name, String waypoint, Double lat, Double lon, Trig.Physical type, Condition condition, Condition logged, Trig.Current current, Trig.Historic historic, String fb) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(TRIG_ID			, id);
		initialValues.put(TRIG_NAME			, name);
		initialValues.put(TRIG_WAYPOINT		, waypoint);
		initialValues.put(TRIG_LAT			, lat);
		initialValues.put(TRIG_LON			, lon);
		initialValues.put(TRIG_TYPE			, type.code());
		initialValues.put(TRIG_CONDITION	, condition.code());
		initialValues.put(TRIG_LOGGED		, logged.code());
		initialValues.put(TRIG_CURRENT		, current.code());
		initialValues.put(TRIG_HISTORIC		, historic.code());
		initialValues.put(TRIG_FB			, fb);
		return mDb.insert(TRIG_TABLE, null, initialValues);
	}

	/**
	 * Delete all trigs
	 * 
	 * @return true if deleted, false otherwise
	 */
	public boolean updateTrigLog(int id, Condition logged) {
		ContentValues args = new ContentValues();
		args.put(TRIG_LOGGED, logged.code());
		return mDb.update(TRIG_TABLE, args, TRIG_ID + "=" + id, null) > 0;
	}


	/**
	 * Delete all trigs
	 * 
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteAll() {
		return mDb.delete(TRIG_TABLE, null, null) > 0;
	}

	
	/**
	 * Return a Cursor suitable for the triglist screen
	 * 
	 * @return Cursor 
	 */
	public Cursor fetchTrigList(Location loc) {
		String strOrder;	
   
		if (null != loc) {
			strOrder = String.format("(%3.3f-%s)*(%3.3f-%s) + %f * (%3.3f-%s)*(%3.3f-%s) LIMIT %s", loc.getLatitude(), TRIG_LAT, loc.getLatitude(), TRIG_LAT, 
					Math.pow(Math.cos(Math.toRadians(loc.getLatitude())),2), loc.getLongitude(), TRIG_LON, loc.getLongitude(), TRIG_LON, mPrefs.getString("listentries", "100"));
		} else {
			strOrder = TRIG_NAME + " LIMIT " +  mPrefs.getString("listentries", "100");
		}
		Log.i(TAG, strOrder);
		return mDb.query(TRIG_TABLE, new String[] {TRIG_ID, TRIG_NAME, TRIG_LAT, TRIG_LON, TRIG_TYPE, TRIG_CONDITION, TRIG_LOGGED}, null, null, null, null, strOrder);
	}
	
	
	
	/**
	 * Return a Cursor suitable for the map screen
	 * 
	 * @return Cursor 
	 */
	public Cursor fetchTrigMapList (BoundingBoxE6 box) {
		String strOrder = String.format("%s limit %s", TRIG_LAT, mPrefs.getString("mapcount", DEFAULT_MAP_COUNT));	
   
		String strWhere = String.format("%s between %3.6f and %3.6f  and  %s between %3.6f and %3.6f"
				, TRIG_LON, box.getLonWestE6()/1000000.0, box.getLonEastE6()/1000000.0, TRIG_LAT, box.getLatSouthE6()/1000000.0, box.getLatNorthE6()/1000000.0); 

		
		Log.i(TAG, strWhere);
		Log.i(TAG, strOrder);
		return mDb.query(TRIG_TABLE, new String[] {TRIG_ID, TRIG_NAME, TRIG_LAT, TRIG_LON, TRIG_TYPE, TRIG_CONDITION, TRIG_LOGGED}, strWhere, null, null, null, strOrder);
	}
	
	/**
	 * Return a Cursor suitable for the triglist screen
	 * 
	 * @return Cursor 
	 */
	public Cursor fetchTrigInfo (long id) {
		return mDb.query(TRIG_TABLE, new String[] {TRIG_ID, TRIG_NAME, TRIG_LAT, TRIG_LON, TRIG_TYPE, TRIG_CONDITION, TRIG_LOGGED, TRIG_CURRENT, TRIG_HISTORIC, TRIG_FB}, TRIG_ID + "="+id, null, null, null, null);
	}
	

	
}
