package edu.uoregon;

import com.google.android.maps.GeoPoint;

import android.location.Location;

/**
 * Class to represent GeoStamps which have location, picture, voice memo
 * associated with it.
 * 
 * @author Daniel Mundra
 * 
 *         1/27/2010 -- David -- Added database id.
 */
public class GeoStamp {

	// Variables used by GeoStamp
	private int databaseID = -1;
	private boolean edit;
	private final Location loc;

	// Need variables for pictures and voice memos

	/**
	 * Constructor for GeoStamp If the geostamp is created without a database
	 * id, it is currently unrelated to a database item.
	 */
	public GeoStamp(Location loc) {
		this.loc = loc;
		this.edit = false;
	}

	/**
	 * Constructor for GeoStamp, including database id.
	 */
	public GeoStamp(Location loc, int databaseID) {
		this.loc = loc;
		this.databaseID = databaseID;
		this.edit = false;
	}

	/**
	 * Return location of GeoStamp
	 * 
	 * @return - Location
	 */
	public Location getLoc() {
		return loc;
	}

	/**
	 * Return whether stamp is new or not
	 * 
	 * @return - boolean
	 */
	public boolean isEdit() {
		return edit;
	}

	/**
	 * Set edit to true
	 */
	public void setEdit() {
		this.edit = true;
	}

	/**
	 * Return a string representation of location
	 * 
	 * @return - String
	 */
	public String toString() {
		return "GeoStamp: (" + loc.getLatitude() + "," + loc.getLongitude()
				+ ")";
	}

	/**
	 * Return the database id
	 * 
	 * @return - int
	 */
	public int getDatabaseID() {
		return databaseID;
	}

	/**
	 * Return GeoPoint for this stamp
	 * @return - GeoPoint
	 */
	public GeoPoint getGeoPoint() {
		return new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc
				.getLongitude() * 1E6));
	}
	
	@Override
	public boolean equals(Object o) {
		GeoStamp g = (GeoStamp) o;
		return this.loc.distanceTo(g.getLoc()) == 0;
	}
}
