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
	 * @param geoStamp
	 */
	public abstract void addGeoStamp(GeoStamp geoStamp);
	
	// TODO I need to figure out what kind of object a picture and a recording is saved as. 
//	public abstract void addPictureToGeoStamp(GeoStamp geoStamp, Picture picture);
//	public abstract void addRecordingToGeoStamp(GeoStamp geoStamp, Recording recording);
}
