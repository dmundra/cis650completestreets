package edu.uoregon.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.uoregon.GeoStamp;
import edu.uoregon.log.CSLog;

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
	
	private final String TAG = "GeoDBConnector"; 
	
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
				list.add(new GeoStamp(lat,lon,id));
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
			return new GeoStamp(lat,lon, id);
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
		CSLog.i(TAG, "Adding picture to geostamp: " + geoStampID);
		
		// We do not want to add a picture to a geostamp
		// that does not exist in the database.
		if (geoStampID == GeoStamp.newGeoStamp)
			return false;
		
		// First, write picture to a file.
		String picturePath = convertToFile(picture,geoStampID,TYPE_PICTURE);
		
		ContentValues values = new ContentValues();
		values.put(KEY_GEOSTAMP_ID, geoStampID);
		values.put(PICTURE_KEY_PICTUREPATH, picturePath);
		
		if (db.insert(TABLE_GEOSTAMP_PICTURE, null, values) != -1)
			return true;
		return false;
	}
	
	/**
	 * @see IGeoDB
	 * @deprecated
	 */
	@Override
	public boolean addRecordingToGeoStamp(int geoStampID, byte[] recording) {
		CSLog.i(TAG, "Adding recording to geostamp: " + geoStampID);
		// We do not want to add a recording to a geostamp
		// that does not exist in the database.
		if (geoStampID == GeoStamp.newGeoStamp)
			return false;
		
		// First, write recording to a file.
		String recordingPath = convertToFile(recording,geoStampID,TYPE_RECORDING);
		
		ContentValues values = new ContentValues();
		values.put(KEY_GEOSTAMP_ID, geoStampID);
		values.put(RECORDING_KEY_RECORDINGPATH, recordingPath);
		
		if (db.insert(TABLE_GEOSTAMP_RECORDING, null, values) != -1)
			return true;
		return false;
	}
	
	/**
	 * @see IGeoDB
	 */
	@Override
	public boolean addRecordingToGeoStamp(int geoStampID, String recordingFilePath) {
		CSLog.i(TAG, "Adding recording to geostamp: " + geoStampID);
		// We do not want to add a recording to a geostamp
		// that does not exist in the database.
		if (geoStampID == GeoStamp.newGeoStamp)
			return false;
		
		recordingFilePath = recordingPathFix(recordingFilePath, geoStampID);
		
		ContentValues values = new ContentValues();
		values.put(KEY_GEOSTAMP_ID, geoStampID);
		values.put(RECORDING_KEY_RECORDINGPATH, recordingFilePath);
		
		if (db.insert(TABLE_GEOSTAMP_RECORDING, null, values) != -1)
			return true;
		return false;
	}
	
	/**
	 * @see IGeoDB
	 * @deprecated
	 */
	@Override
	public List<byte[]> getRecordings(int geoStampID){
		Cursor cur = db.query(TABLE_GEOSTAMP_RECORDING, new String[]{RECORDING_KEY_RECORDINGPATH}, KEY_GEOSTAMP_ID + " = " + geoStampID, null, null, null, KEY_ROWID);
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		
		// If there are some recordings in there
		if (cur.moveToFirst()) {
			
			// Go through all the recordings 
			while (!cur.isAfterLast()) {
				list.add(getFile(cur.getString(cur.getColumnIndex(RECORDING_KEY_RECORDINGPATH))));
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
	public List<String> getRecordingFilePaths(int geoStampID) {
		Cursor cur = db.query(TABLE_GEOSTAMP_RECORDING, new String[]{RECORDING_KEY_RECORDINGPATH}, KEY_GEOSTAMP_ID + " = " + geoStampID, null, null, null, KEY_ROWID);
		ArrayList<String> list = new ArrayList<String>();
		
		// If there are some recordings in there
		if (cur.moveToFirst()) {
			
			// Go through all the recordings 
			while (!cur.isAfterLast()) {
				list.add(cur.getString(cur.getColumnIndex(RECORDING_KEY_RECORDINGPATH)));
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
	protected static final String RECORDING_KEY_RECORDINGPATH = "recording_path";
	
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
		RECORDING_KEY_RECORDINGPATH + " TEXT NOT NULL)";
	
	private static final String RECORDING_DELETE =
		"DROP TABLE IF EXISTS " + TABLE_GEOSTAMP_RECORDING;
	
	/**
	 * Converts and saves a file to the filesystem
	 * @param file
	 * 		Byte array representing the file.
	 * @return The path to the file
	 */
	private String convertToFile(byte[] file, int ID, int type) {
		String dir;
		switch (type) {
		case TYPE_PICTURE:
			dir = IGeoDB.pictureFilePath;
			break;
		case TYPE_RECORDING:
			dir = IGeoDB.audioFilePath;
			break;
		default:
			CSLog.d(TAG, "Wrong type!");
			return "";
		}
		
		// See if the directory exists
		File fDir = new File(dir); 
		if(!fDir.exists()){
			if(!fDir.mkdirs()){
				CSLog.d(TAG, "Can't make directory: " + fDir.getAbsolutePath());
			}
		}

		Calendar c = Calendar.getInstance();
		String fileName = "ID" + ID + "_" + 
			c.get(Calendar.YEAR) + 
			c.get(Calendar.MONTH) + 
			c.get(Calendar.DAY_OF_MONTH) + "_" +
			c.get(Calendar.HOUR_OF_DAY) + 
			c.get(Calendar.MINUTE) +
			c.get(Calendar.SECOND);
		
		switch (type) {
		case TYPE_PICTURE:
			fileName += ".jpg";
			break;
		case TYPE_RECORDING:
			fileName += ".3gp";
			break;
		}
		
		File f = new File(fDir.getAbsolutePath() + "/" + fileName);
		try {
			PrintStream fos = new PrintStream(f);
			fos.write(file);
			fos.close();
		} catch (FileNotFoundException e) {
			CSLog.e(TAG, e.getMessage());
		} catch (IOException e) {
			
		}

		// Return the path to the file. 
		return f.getAbsolutePath();
	}
	
	private static final int TYPE_PICTURE = 0;
	private static final int TYPE_RECORDING = 1;
	
	/**
	 * Reads a recording and saves it in the proper spot.
	 * @param filepath
	 * @param ID
	 * @return
	 */
	private String recordingPathFix(String filepath, int ID) {
		byte[] b = getFile(filepath);
		return convertToFile(b, ID, TYPE_RECORDING);
	}
	
	/**
	 * Retrieves a file and converts it to a byte array.
	 * @param filepath
	 * @return
	 */
	private byte[] getFile(String filepath) {
		byte[] res = new byte[0];
		try {
			File f = new File(filepath);
			res = new byte[(int)f.length()];
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
	        int offset = 0;
	        int numRead = 0;
	        while (offset < res.length && (numRead=is.read(res, offset, res.length-offset)) >= 0) {
	            offset += numRead;
	        }
			is.close();
		} catch (FileNotFoundException e) {
			CSLog.e(TAG, e.getMessage());
		} catch (IOException e) {
			CSLog.e(TAG, e.getMessage());
		}

		return res;
	}
}
