package edu.uoregon;

import java.text.DecimalFormat;

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

	private int databaseID = newGeoStamp;
	private final double latitude;
	private final double longitude;
	
	// Used to truncate the lat and lon information to only six decimals places.
	private final DecimalFormat df = new DecimalFormat("#.000000");

	/**
	 * Constructor for GeoStamp If the geostamp is created without a database
	 * id, it is currently unrelated to a database item.
	 */
	public GeoStamp(double lat, double lon) {		
		this.latitude = Double.parseDouble(df.format(lat));
		this.longitude = Double.parseDouble(df.format(lon));
	}

	/**
	 * Constructor for GeoStamp, including database id. This constructor should
	 * only be called from the database connector.
	 */
	public GeoStamp(double lat, double lon, int databaseID) {
		this.latitude = Double.parseDouble(df.format(lat));
		this.longitude = Double.parseDouble(df.format(lon));
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
		double EPSILON = 0.00001;		
		boolean isLatEQ = Math.abs(this.latitude - g.getLatitude()) < EPSILON;
		boolean isLonEQ = Math.abs(this.longitude - g.getLongitude()) < EPSILON;		
		return isLatEQ && isLonEQ;
	}
	
	public boolean isNew() {
		return (databaseID == newGeoStamp);
	}

	public static final int newGeoStamp = -1;
}
