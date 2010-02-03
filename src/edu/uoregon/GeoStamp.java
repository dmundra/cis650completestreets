package edu.uoregon;

import android.location.Location;

import com.google.android.maps.GeoPoint;

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
	private int databaseID = newGeoStamp;
	private final Location loc;

	// Need variables for pictures and voice memos

	/**
	 * Constructor for GeoStamp If the geostamp is created without a database
	 * id, it is currently unrelated to a database item.
	 */
	public GeoStamp(Location loc) {
		this.loc = loc;
	}

	/**
	 * Constructor for GeoStamp, including database id. This constructor should
	 * only be called from the database connector.
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

	/**
	 * Convenience method for callers that might want just the latitude.
	 * 
	 * @return The latitude of this GeoStamp
	 */
	public double getLatitude() {
		return loc.getLatitude();
	}

	/**
	 * Convenience method for callers that might want just the longitude.
	 * 
	 * @return The longitude of this GeoStamp
	 */
	public double getLongitude() {
		return loc.getLongitude();
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
	 * sets the db id
	 * @param id
	 * 		the new id
	 */
	public void setDatabaseID(int id) {
		databaseID = id;
	}

	/**
	 * Return GeoPoint for this stamp
	 * 
	 * @return - GeoPoint
	 */
	public GeoPoint getGeoPoint() {
		return new GeoPoint((int) (this.getLoc().getLatitude() * 1E6),
				(int) (this.getLoc().getLongitude() * 1E6));
	}

	@Override
	public boolean equals(Object o) {
		GeoStamp g = (GeoStamp) o;
		boolean isequal = this.getLoc().distanceTo(g.getLoc()) == 0;
		return isequal;
	}

	public static final int newGeoStamp = -1;
}
