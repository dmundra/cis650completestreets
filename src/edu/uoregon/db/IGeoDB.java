package edu.uoregon.db;

import java.util.List;
import edu.uoregon.GeoStamp;

public interface IGeoDB { 
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
	 */
	public abstract List<GeoStamp> getGeoStamps(double latitude, double longitude, double radiusMeters);
	
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
	 * 		True of the insertion succeeds, false otherwise 
	 */
	public abstract boolean addRecordingToGeoStamp(GeoStamp geoStamp, byte[] recording);
	
	/**
	 * Adds a recording relation to the GeoStamp with the specified ID. 
	 * @param geoStampID
	 * 		The ID of the GeoStamp that we want to add a recording to.
	 * @param picture
	 * 		The bytes of a recording.
	 * @return
	 * 		True of the insertion succeeds, false otherwise
	 */
	public abstract boolean addRecordingToGeoStamp(int geoStampID, byte[] recording);
	
	public abstract void close();
}
