package edu.uoregon.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import edu.uoregon.GeoStamp;

public class GeoDBConnector implements IGeoDB {
	private final ArrayList<GeoStamp> stamps;
	private final Context context;
	
	/**
	 * Constructor.
	 */
	private GeoDBConnector(Context context) {
		this.context = context;
		stamps = new ArrayList<GeoStamp>();
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public void addGeoStamp(GeoStamp geoStamp) {
		// TODO Actual database connection
		stamps.add(geoStamp);
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public List<GeoStamp> getGeoStamps() {
		// TODO Actual database connection
		return stamps;
	}

	/**
	 * @see IGeoDB
	 */
	@Override
	public List<GeoStamp> getGeoStamps(double latitude, double longitude,
			double radiusMeters) {
		// TODO Actual database connection
		return stamps;
	}

	public static IGeoDB open(Context context) {
		GeoDBConnector db = new GeoDBConnector(context);
		// TODO Set up connection
		return db;
	}

}
