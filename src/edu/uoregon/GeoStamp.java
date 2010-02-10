package edu.uoregon;

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
	private double latitude;
	private double longitude;

	// Need variables for pictures and voice memos

	/**
	 * Constructor for GeoStamp If the geostamp is created without a database
	 * id, it is currently unrelated to a database item.
	 */
	public GeoStamp(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}

	/**
	 * Constructor for GeoStamp, including database id. This constructor should
	 * only be called from the database connector.
	 */
	public GeoStamp(double lat, double lon, int databaseID) {
		this.latitude = lat;
		this.longitude = lon;
		this.databaseID = databaseID;
	}

	/**
	 * Convenience method for callers that might want just the latitude.
	 * 
	 * @return The latitude of this GeoStamp
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * Convenience method for callers that might want just the longitude.
	 * 
	 * @return The longitude of this GeoStamp
	 */
	public double getLongitude() {
		return this.longitude;
	}

	/**
	 * Return a string representation of location GeoStamp:
	 * (id,latitude,longitude)
	 * 
	 * @return - String
	 */
	public String toString() {
		return "GeoStamp: (" + databaseID + "," + this.latitude + ","
				+ this.longitude + ")";
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
	 * 
	 * @param id
	 *            the new id
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
		return new GeoPoint((int) (this.latitude * 1E6),
				(int) (this.longitude * 1E6));
	}

	@Override
	public boolean equals(Object o) {
		GeoStamp g = (GeoStamp) o;
		boolean isLatEQ = this.latitude == g.getLatitude();
		boolean isLonEQ = this.longitude == g.getLongitude();
		return isLatEQ || isLonEQ;
	}

	public static final int newGeoStamp = -1;
}
