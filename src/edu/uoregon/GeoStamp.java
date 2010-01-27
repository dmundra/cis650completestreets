package edu.uoregon;

import android.location.Location;

/**
 * Class to represent GeoStamps which have location, picture, voice memo
 * associated with it.
 * 
 * @author Daniel Mundra
 * 
 */
public class GeoStamp {

	private final Location loc;
	// Need variables for pictures and voice memos

	/**
	 * Constructor for GeoStamp
	 */
	public GeoStamp(Location loc) {
		this.loc = loc;
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
}
