package edu.uoregon.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import edu.uoregon.GeoStamp;

/**
 * This class connects to the database storing the all GeoStamps.
 * 
 * 1/31/2010 -- David -- Now connects to the database to retrieve GeoStamps.
 * 2/2/2010 -- David -- simplified the database design.
 * 2/6/2010 -- David -- Recoded how pictures are stored.
 */
public class GeoDBConnector implements IGeoDB {
	
	// Database that this connector is connected to.
	private SQLiteDatabase db;
	
	/**
	 * Constructor.
	 */
	private GeoDBConnector(SQLiteDatabase db) {
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
			final long newId = db.insert(TABLE_GEOSTAMP, null, values);
			if (newId != -1){
				geoStamp.setDatabaseID((int)newId);
				return true;
			}
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
				cur.moveToNext();
			}
		}
		cur.close();
		return list;
	}
	
	/**
	 * @see IGeoDB
	 * @deprecated
	 */
	@Override
	public List<GeoStamp> getGeoStamps(double latitude, double longitude, double radiusMeters) {
		return getGeoStamps();
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public GeoStamp getGeoStamp(double latitude, double longitude) {
		Cursor cur = db.query(TABLE_GEOSTAMP, null, GEOSTAMP_KEY_LATITUDE + " = " + latitude + " AND " + 
													GEOSTAMP_KEY_LONGITUDE + " = " + longitude, null, null, null, null);
		// If there is a GeoStamps in there
		if (cur.moveToFirst()) {
			int id = cur.getInt(cur.getColumnIndex(KEY_ROWID));
			double lat = cur.getDouble(cur.getColumnIndex(GEOSTAMP_KEY_LATITUDE));
			double lon = cur.getDouble(cur.getColumnIndex(GEOSTAMP_KEY_LONGITUDE));
			cur.close();
			Location l = new Location("db");
			l.setLatitude(lat);
			l.setLongitude(lon);
			return new GeoStamp(l, id);
		}
		return null;
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public boolean addPictureToGeoStamp(GeoStamp geoStamp, byte[] picture) {
		return addPictureToGeoStamp(geoStamp.getDatabaseID(), picture);
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public boolean addRecordingToGeoStamp(GeoStamp geoStamp, byte[] recording) {
		return addRecordingToGeoStamp(geoStamp.getDatabaseID(), recording);
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public boolean addPictureToGeoStamp(int geoStampID, byte[] picture) {
		Log.d(this.toString(), "Adding picture to geostamp: " + geoStampID);
		
		// We do not want to add a picture to a geostamp
		// that does not exist in the database.
		if (geoStampID == GeoStamp.newGeoStamp)
			return false;
		
		// First, write picture to a file.
		String picturePath = convertPictureToFile(picture);
		
		ContentValues values = new ContentValues();
		values.put(KEY_GEOSTAMP_ID, geoStampID);
		values.put(PICTURE_KEY_PICTUREPATH, picturePath);
		
		if (db.insert(TABLE_GEOSTAMP_PICTURE, null, values) != -1)
			return true;
		return false;
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public boolean addRecordingToGeoStamp(int geoStampID, byte[] recording) {
		Log.d(this.toString(), "Adding recording to geostamp: " + geoStampID);
		// We do not want to add a recording to a geostamp
		// that does not exist in the database.
		if (geoStampID == GeoStamp.newGeoStamp)
			return false;
		
		ContentValues values = new ContentValues();
		values.put(KEY_GEOSTAMP_ID, geoStampID);
		values.put(RECORDING_KEY_RECORDING, recording);
		
		if (db.insert(TABLE_GEOSTAMP_RECORDING, null, values) != -1)
			return true;
		return false;
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public List<byte[]> getRecordings(int geoStampID){
		Cursor cur = db.query(TABLE_GEOSTAMP_RECORDING, new String[]{RECORDING_KEY_RECORDING}, KEY_GEOSTAMP_ID + " = " + geoStampID, null, null, null, KEY_ROWID);
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		
		// If there are some recordings in there
		if (cur.moveToFirst()) {
			
			// Go through all the recordings 
			while (!cur.isAfterLast()) {
				list.add(cur.getBlob(cur.getColumnIndex(RECORDING_KEY_RECORDING)));
				cur.moveToNext();
			}
		}
		cur.close();
		return list;
	}
	
	/**
	 * @see IGeoDB
	 * @deprecated
	 */
	@Override
	public List<byte[]> getPictures(int geoStampID){
		Cursor cur = db.query(TABLE_GEOSTAMP_PICTURE, new String[]{PICTURE_KEY_PICTUREPATH}, KEY_GEOSTAMP_ID + " = " + geoStampID, null, null, null, KEY_ROWID);
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		
		// If there are some recordings in there
		if (cur.moveToFirst()) {
			
			// Go through all the recordings 
			while (!cur.isAfterLast()) {
				list.add(getFile(cur.getString(cur.getColumnIndex(PICTURE_KEY_PICTUREPATH))));
				cur.moveToNext();
			}
		}
		cur.close();
		return list;
	}
	/**
	 * @see IGeoDB
	 */
	@Override
	public List<String> getPictureFilePaths(int geoStampID){
		Cursor cur = db.query(TABLE_GEOSTAMP_PICTURE, new String[]{PICTURE_KEY_PICTUREPATH}, KEY_GEOSTAMP_ID + " = " + geoStampID, null, null, null, KEY_ROWID);
		ArrayList<String> list = new ArrayList<String>();
		
		// If there are some recordings in there
		if (cur.moveToFirst()) {
			
			// Go through all the recordings 
			while (!cur.isAfterLast()) {
				list.add(cur.getString(cur.getColumnIndex(PICTURE_KEY_PICTUREPATH)));
				cur.moveToNext();
			}
		}
		cur.close();
		return list;
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public void deleteAllGeoStamps() {
		// Just delete everything in there.
		db.delete(TABLE_GEOSTAMP, null, null);
		db.delete(TABLE_GEOSTAMP_PICTURE, null, null);
		db.delete(TABLE_GEOSTAMP_RECORDING, null, null);
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public void deleteGeoStamp(GeoStamp geoStamp) {
		deleteGeoStamp(geoStamp.getDatabaseID());
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public void deleteGeoStamp(int geoStampID) {
		// Delete the geostamp
		db.delete(TABLE_GEOSTAMP, KEY_ROWID + " = " + geoStampID, null);

		// Delete relations with pictures and recordings
		db.delete(TABLE_GEOSTAMP_PICTURE, KEY_GEOSTAMP_ID + " = " + geoStampID, null);
		db.delete(TABLE_GEOSTAMP_RECORDING, KEY_GEOSTAMP_ID + " = " + geoStampID, null);
		
		// XXX Should this also delete picture and recording files?
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public void close() {
		db.close();
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public void recreateTables() {
		db.beginTransaction();
		try {
			db.execSQL(GEOSTAMP_DELETE);
			db.execSQL(PICTURE_DELETE);
			db.execSQL(RECORDING_DELETE);
			db.execSQL(GEOSTAMP_CREATE);
			db.execSQL(PICTURE_CREATE);
			db.execSQL(RECORDING_CREATE);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
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
		GeoDBConnector dbConnector = new GeoDBConnector(SQLdatabase);
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
			db.execSQL(PICTURE_CREATE);
			db.execSQL(RECORDING_CREATE);
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
	protected static final String PICTURE_KEY_PICTUREPATH = "picture_path";
	
	// Recording fields
	protected static final String RECORDING_KEY_RECORDING = "recording";
	
	// Relation fields
	protected static final String KEY_GEOSTAMP_ID = "geostamp_id";

	private static final String DB_NAME = "COMPLETE_STREETS";
	
	// Tables
	private static final String TABLE_GEOSTAMP = "geo_stamp";
	
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
	
	private static final String GEOSTAMP_DELETE =
		"DROP TABLE IF EXISTS " + TABLE_GEOSTAMP;
	
	private static final String PICTURE_CREATE = 
		"CREATE TABLE " + TABLE_GEOSTAMP_PICTURE +
		" (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_GEOSTAMP_ID + " INTEGER NOT NULL, " +
		PICTURE_KEY_PICTUREPATH + " TEXT NOT NULL)";
	
	private static final String PICTURE_DELETE =
		"DROP TABLE IF EXISTS " + TABLE_GEOSTAMP_PICTURE;

	private static final String RECORDING_CREATE = 
		"CREATE TABLE " + TABLE_GEOSTAMP_RECORDING +
		" (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_GEOSTAMP_ID + " INTEGER NOT NULL, " +
		RECORDING_KEY_RECORDING + " BLOB NOT NULL)";
	
	private static final String RECORDING_DELETE =
		"DROP TABLE IF EXISTS " + TABLE_GEOSTAMP_RECORDING;
	
	public String toString() {
		return "Database GeoDBConnector";
	}
	
	/**
	 * Converts and saves a picture to the filesystem
	 * @param picture
	 * 		Byte array representing the JPEG picture.
	 * @return The file path to the picture
	 */
	private String convertPictureToFile(byte[] picture) {
		// See if the picture directory exists
		File pictureDir = new File(Environment.getExternalStorageDirectory() + "/CompleteStreets/pictures/"); 
		if(!pictureDir.exists()){
			if(!pictureDir.mkdirs()){
				Log.d(this.toString(), "Can't make directory: " + pictureDir.getAbsolutePath());
			}
		}

		Calendar c = Calendar.getInstance();
		String pictureFileName = "" + c.get(Calendar.YEAR) + 
			c.get(Calendar.MONTH) + 
			c.get(Calendar.DAY_OF_MONTH) + "_" +
			c.get(Calendar.HOUR_OF_DAY) + 
			c.get(Calendar.MINUTE) +
			c.get(Calendar.SECOND) + ".jpg";
		
		File pictureFile = new File(pictureDir.getAbsolutePath() + "/" + pictureFileName);
		FileOutputStream fos = null;
		final String log = "Something went wrong writing the file ";
		try {
			fos = new FileOutputStream(pictureFile);
			IOUtils.write(picture, fos);
		} catch (FileNotFoundException e) {
			Log.d(this.toString(), log + e.toString());
		} catch (IOException e) {
			Log.d(this.toString(), log + e.toString());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		// Return the path to the file. 
		return pictureFile.getAbsolutePath();
	}
	
	/**
	 * Retrieves a file and converts it to a byte array.
	 * @param filepath
	 * @return
	 */
	private byte[] getFile(String filepath) {
		byte[] ret = new byte[0];

		final String log = "Something went wrong writing the file ";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filepath);
			ret = IOUtils.toByteArray(fis);
		} catch (FileNotFoundException e) {
			Log.d(this.toString(), log + e.toString());
		} catch (IOException e) {
			Log.d(this.toString(), log + e.toString());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		return ret;
	}
}
