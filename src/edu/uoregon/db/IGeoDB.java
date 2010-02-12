package edu.uoregon.db;

import java.util.List;

import android.os.Environment;
import edu.uoregon.GeoStamp;

public interface IGeoDB {
	public static final String pictureFilePath = Environment.getExternalStorageDirectory() + "/CompleteStreets/pictures/";
	public static final String audioFilePath = Environment.getExternalStorageDirectory() + "/CompleteStreets/audio/";
	
	/**
	 * Returns all GeoStamps that are currently
	 * stored in the database.
	 * @return
	 */
	public abstract List<GeoStamp> getGeoStamps();

	/**
	 * Returns all GeoStamps that are currently
	 * stored in the database within a certain
	 * radius of the given latitude and longitude.
	 * @return
	 * @deprecated Because we do not want to overcomplicate things
	 */
	public abstract List<GeoStamp> getGeoStamps(double latitude, double longitude, double radiusMeters);
	
	/**
	 * Returns the GeoStamp that matches the
	 * specified latitude and longitude.
	 * @return A GeoStamp if it exists, null otherwise.
	 */
	public abstract GeoStamp getGeoStamp(double latitude, double longitude);
	
	/**
	 * Adds a geostamp to the database.
	 * This method is also used when updating a GeoStamp.
	 * @param geoStamp
	 * @return true if the GeoStamp was added, false otherwise.
	 */
	public abstract boolean addGeoStamp(GeoStamp geoStamp);
	
	/**
	 * Adds a picture relation to the specified GeoStamp.
	 * @param geoStamp
	 * 		The GeoStamp that we want to add a picture to 
	 * @param picture 
	 * 		The bytes of the finished JPEG picture.
	 * @return
	 * 		True of the insertion succeeds, false otherwise 
	 */
	public abstract boolean addPictureToGeoStamp(GeoStamp geoStamp, byte[] picture);
	
	/**
	 * Adds a picture relation to the GeoStamp with the specified ID. 
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we want to add a picture to.
	 * @param picture
	 * 		The bytes of the finished JPEG picture.
	 * @return
	 * 		True of the insertion succeeds, false otherwise
	 */
	public abstract boolean addPictureToGeoStamp(int geoStampID, byte[] picture);
	
	/**
	 * Adds a recording relation to the specified GeoStamp.
	 * @param geoStamp
	 * 		The GeoStamp that we want to add a recording to 
	 * @param picture 
	 * 		The bytes of a recording.
	 * @return
	 * 		True if the insertion succeeds, false otherwise 
	 * @deprecated because we prefer fileNames
	 */
	public abstract boolean addRecordingToGeoStamp(GeoStamp geoStamp, byte[] recording);
	
	/**
	 * Adds a recording relation to the GeoStamp with the specified ID. 
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we want to add a recording to.
	 * @param picture
	 * 		The bytes of a recording.
	 * @return
	 * 		True if the insertion succeeds, false otherwise
	 * @deprecated because we prefer fileNames
	 */
	public abstract boolean addRecordingToGeoStamp(int geoStampID, byte[] recording);
	
	/**
	 * Adds a recording filepath to the GeoStamp with specified ID.
	 * The database might rename the recording so it fits the standards of the database better.
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we want to add a recording to.
	 * @param recordingFilePath
	 * 		The filepath for the recording
	 * @return
	 * 		True if the insertion succeeds, false otherwise
	 */
	public abstract boolean addRecordingToGeoStamp(int geoStampID, String recordingFilePath);
	
	/**
	 * Returns a list of recording from the db that match the given ID
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we will use to match against recording
	 * @return
	 * 		The list of recordings that match the given geoStampID
	 * @deprecated Use getRecordingFilePaths instead and extract recordings from that.
	 */
	public abstract List<byte[]> getRecordings(int geoStampID);
	
	/**
	 * Returns a list of recording file paths from the db that match the given ID
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we will use to match against recording
	 * @return
	 * 		The list of recording file paths that match the given geoStampID
	 */
	public abstract List<String> getRecordingFilePaths(int geoStampID);

	/**
	 * Returns a list of pictures from the db that match the given GeoStamp ID
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we will use to match against recording
	 * @return
	 * 		The list of pictures for the given geoStamp
	 * @deprecated Use getPictureFilePaths instead and extract the pictures from that.
	 */
	public abstract List<byte[]> getPictures(int geoStampID);
	
	/**
	 * Returns a list of picture file paths from the db that match the given GeoStamp ID
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we will use to match against recording
	 * @return
	 * 		The list of picture file paths for the given geoStamp
	 */
	public abstract List<String> getPictureFilePaths(int geoStampID);
	
	/**
	 * Closes the database
	 */
	public abstract void close();

	/**
	 * Recreates all the tables in the database. This can be useful
	 * if there is a corruption in the database.
	 */
	public abstract void recreateTables(); 
	
	/**
	 * Deletes all geostamps from the databse.
	 */
	public abstract void deleteAllGeoStamps();
	
	/**
	 * Deletes a specific GeoStamp from the database.
	 * @param geoStamp
	 * 			The GeoStamp to delete.
	 */
	public abstract void deleteGeoStamp(GeoStamp geoStamp);
	
	/**
	 * Deletes a specific GeoStamp from the databse.
	 * @param geoStampID
	 * 			The ID of the GeoStamp to delete from the database.
	 */
	public abstract void deleteGeoStamp(int geoStampID);
}
