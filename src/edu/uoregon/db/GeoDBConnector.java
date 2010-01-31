package edu.uoregon.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import edu.uoregon.GeoStamp;

/**
 * This class connects to the database storing the all GeoStamps.
 * 
 * 1/31/2010 -- David -- Now connects to the database to retrieve GeoStamps.
 */
public class GeoDBConnector implements IGeoDB {
	// The context of this connector.
	private final Context context;
	
	// Database that this connector is connected to.
	private SQLiteDatabase db;
	
	/**
	 * Constructor.
	 */
	private GeoDBConnector(Context context, SQLiteDatabase db) {
		this.context = context;
		this.db = db;
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public boolean addGeoStamp(GeoStamp geoStamp) {
		if (geoStamp.getDatabaseID() == GeoStamp.newGeoStamp) {
			ContentValues values = new ContentValues();
			values.put(GEOSTAMP_KEY_LONGITUDE, geoStamp.getLongitude());
			values.put(GEOSTAMP_KEY_LATITUDE, geoStamp.getLatitude());
			
			// XXX Does not check for duplicate entries
			// which are possible because latitude and longitude
			// are not part of the primary key.
			if (db.insert(TABLE_GEOSTAMP, null, values) != -1)
				return true;
		}
		else
			return updateGeoStamp(geoStamp);
		return false;
	}
	
	private boolean updateGeoStamp(GeoStamp geoStamp) {
		ContentValues values = new ContentValues();
		values.put(GEOSTAMP_KEY_LONGITUDE, geoStamp.getLongitude());
		values.put(GEOSTAMP_KEY_LATITUDE, geoStamp.getLatitude());
		
		if (db.update(TABLE_GEOSTAMP, values, KEY_ROWID + " = " + geoStamp.getDatabaseID(), null) > 0)
			return true;
		else
			return false;
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public List<GeoStamp> getGeoStamps() {
		Cursor cur = db.query(TABLE_GEOSTAMP, null, null, null,	null, null, null);
		ArrayList<GeoStamp> list = new ArrayList<GeoStamp>();
		
		// If there is some GeoStamps in there
		if (cur.moveToFirst()) {
			
			// Go through all the GeoStamps 
			while (!cur.isAfterLast()) {
				int id = cur.getInt(cur.getColumnIndex(KEY_ROWID));
				double lat = cur.getDouble(cur.getColumnIndex(GEOSTAMP_KEY_LATITUDE));
				double lon = cur.getDouble(cur.getColumnIndex(GEOSTAMP_KEY_LONGITUDE));
				Location l = new Location("db");
				l.setLatitude(lat);
				l.setLongitude(lon);
				list.add(new GeoStamp(l,id));
			}
		}
		return list;
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public List<GeoStamp> getGeoStamps(double latitude, double longitude,
			double radiusMeters) {
		// TODO Ignores the latitude and longitude input right now.
		return getGeoStamps();
	}

	/**
	 * Establish connection to an SQLite database
	 * that stores all geostamps. 
	 * @param context
	 * @return
	 * An interface for manipulation the database
	 */
	public static IGeoDB open(Context context) {
		SQLiteDatabase SQLdatabase = new GeoDBOpenHelper(context).getWritableDatabase();
		GeoDBConnector dbConnector = new GeoDBConnector(context,SQLdatabase);
		return dbConnector;
	}

	/**
	 * The internal class is used to connect to the SQLite database.
	 * It is just something that "has" to be there.
	 */
	private static class GeoDBOpenHelper extends SQLiteOpenHelper {

		public GeoDBOpenHelper(Context c) {
			super(c, DB_NAME, null, DB_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(GEOSTAMP_CREATE);
			// TODO Include the other tables.
//			db.execSQL(PICTURE_CREATE);
//			db.execSQL(RECORDING_CREATE);
//			db.execSQL(GEOSTAMP_PICTURE_CREATE);
//			db.execSQL(GEOSTAMP_RECORDING_CREATE);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
	}

	protected static final String KEY_ROWID = "_id";
	
	// GeoStamp fields
	protected static final String GEOSTAMP_KEY_LATITUDE = "latitude";
	protected static final String GEOSTAMP_KEY_LONGITUDE = "longitude";
	
	// Picture fields
	protected static final String PICTURE_KEY_PICTURE = "picture";
	
	// Recording fields
	protected static final String RECORDING_KEY_RECORDING = "recording";
	
	// Relation fields
	protected static final String KEY_RECORDING_ID = "recording_id";
	protected static final String KEY_PICTURE_ID = "picture_id";
	protected static final String KEY_GEOSTAMP_ID = "geostamp_id";

	private static final String DB_NAME = "COMPLETE_STREETS";
	
	// Tables
	private static final String TABLE_GEOSTAMP = "geo_stamp";
	private static final String TABLE_GEOPICTURE = "geo_picture";
	private static final String TABLE_GEORECORDING = "geo_recording";
	
	// Relations
	private static final String TABLE_GEOSTAMP_PICTURE = "geo_stamp_picture";
	private static final String TABLE_GEOSTAMP_RECORDING = "geo_stamp_recording";
	
	private static final int DB_VERSION = 2;
	
	// Table creation
	private static final String GEOSTAMP_CREATE = 
		"CREATE TABLE " + TABLE_GEOSTAMP +
		" (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		GEOSTAMP_KEY_LATITUDE + " DOUBLE NOT NULL, " + 
		GEOSTAMP_KEY_LONGITUDE+ " DOUBLE NOT NULL)";
	
	private static final String PICTURE_CREATE = 
		"CREATE TABLE " + TABLE_GEOPICTURE +
		" (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		PICTURE_KEY_PICTURE + " BLOB NOT NULL)";
	
	private static final String RECORDING_CREATE = 
		"CREATE TABLE " + TABLE_GEORECORDING +
		" (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		PICTURE_KEY_PICTURE + " BLOB NOT NULL)";
	
	private static final String GEOSTAMP_PICTURE_CREATE = 
		"CREATE TABLE " + TABLE_GEOSTAMP_PICTURE +
		" (" + KEY_GEOSTAMP_ID + " INTEGER NOT NULL, " +
		KEY_PICTURE_ID + " INTEGER NOT NULL, " +
		"PRIMARY KEY (" + KEY_GEOSTAMP_ID + ", " + KEY_PICTURE_ID + "))";
	
	private static final String GEOSTAMP_RECORDING_CREATE = 
		"CREATE TABLE " + TABLE_GEOSTAMP_RECORDING +
		" (" + KEY_GEOSTAMP_ID + " INTEGER NOT NULL, " +
		KEY_RECORDING_ID + " INTEGER NOT NULL, " +
		"PRIMARY KEY (" + KEY_GEOSTAMP_ID + ", " + KEY_RECORDING_ID + "))";
}
