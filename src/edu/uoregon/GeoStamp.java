package edu.uoregon;

import android.location.Location;

/**
 * Class to represent GeoStamps which have location, picture, voice memo
 * associated with it.
 * 
 * @author 	Daniel Mundra
 * 
 * 1/27/2010 -- David -- Added database id.  
 */
public class GeoStamp {

	private int databaseID = -1;
	private final Location loc;
	// Need variables for pictures and voice memos

	/**
	 * Constructor for GeoStamp
	 * If the geostamp is created without a database id,
	 * it is currently unrelated to a database item.
	 */
	public GeoStamp(Location loc) {
		this.loc = loc;
	}
	
	/**
	 * Constructor for GeoStamp, including database id.
	 */
	public GeoStamp(Location loc, int databaseID) {
		this.loc = loc;
		this.databaseID = databaseID;
	}

	/**
	 * Return location of GeoStamp
	 * 
	 * @return - Location
	 */
	public Location getLoc() {
		return loc;
	}

	public String toString() {
		return "GeoStamp: (" + loc.getLatitude() + "," + loc.getLongitude()
				+ ")";
	}

	public int getDatabaseID() {
		return databaseID;
	}
}
