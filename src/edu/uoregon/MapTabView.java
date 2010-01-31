package edu.uoregon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Map Tab that will show the map and the phone's current location
 * 
 * @author Daniel Mundra
 * 
 */
public class MapTabView extends MapActivity {
	/** Called when the activity is first created. */
	public static LocationManager lm;
	public static LocationListener ll;
	private MapView mapView;

	// TODO: Right now currentLocation is used to pass to other tabs
	// possible replace this with id, or better logic once the db
	// is in place
	public static Location currentLocation;
	public static boolean edit = false;

	// TODO: Remove once db is in place
	public static ArrayList<GeoStamp> stamps = new ArrayList<GeoStamp>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maptabview);

		// Sets up a connection to the database.

		// TODO: Load the db
		// db = GeoDBConnector.open(this);

		mapView = (MapView) findViewById(R.id.mapView);
		// Show zoom in/out buttons
		mapView.setBuiltInZoomControls(true);
		// Standard view of the map(map/sat)
		mapView.setSatellite(true);

		// Map Controller, we want the zoom to be close to street level
		MapController mapControl = mapView.getController();
		mapControl.setZoom(18);

		// Initialize the location manager
		initLocationManager();
		// Set current location
		currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// Drop pins on saved locations and current locations
		createAndShowMyItemizedOverlay(currentLocation);
	}

	/**
	 * Initialize the location manager
	 */
	private void initLocationManager() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ll = new LocationListener() {

			public void onLocationChanged(Location newLocation) {
				currentLocation = lm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				createAndShowMyItemizedOverlay(newLocation);
			}

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};

		// Current location interval time is set to zero so we should
		// see constant updates if the location changes
		// TODO: Test on real phone, drains a lot of battery
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	/**
	 * This method will be called whenever a change of the current position is
	 * submitted via the GPS.
	 * 
	 * @param newLocation
	 */
	protected void createAndShowMyItemizedOverlay(Location newLocation) {
		List<Overlay> overlays = mapView.getOverlays();

		// First remove old overlays.
		// This is inefficient but guarantees no leftover pins
		// specially when moving
		if (overlays.size() > 0) {
			for (Iterator<Overlay> iterator = overlays.iterator(); iterator
					.hasNext();) {
				iterator.next();
				iterator.remove();
			}
		}

		// Load all saved Geo points
		// TODO: Need to load the list from the db
		boolean currLoadSaved = true;
		Iterator<GeoStamp> iter = stamps.iterator();
		while (iter.hasNext()) {
			GeoStamp curr = iter.next();

			mapView.getOverlays().add(createOverlay(curr,curr.getGeoPoint()));

			if (curr.getLoc().distanceTo((newLocation)) == 0) {
				mapView.getController().animateTo(curr.getGeoPoint());
				currLoadSaved = false;
			}
		}

		// Drop pin on current location only if not saved already
		if (currLoadSaved) {
			// Transform the location to a geopoint
			GeoStamp currLocStamp = new GeoStamp(newLocation);
			mapView.getOverlays().add(createOverlay(currLocStamp, currLocStamp.getGeoPoint()));

			// Move to location
			mapView.getController().animateTo(currLocStamp.getGeoPoint());
		}

		// Redraw map
		mapView.postInvalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private MyItemizedOverlay createOverlay(GeoStamp stamp, GeoPoint geopoint) {
		int pin = 0;
		String txt = "";
		if (stamp.isEdit()) {
			pin = R.drawable.green;
			txt = "Saved Location";
		} else {
			pin = R.drawable.pin;
			txt = "Current Location";
		}

		// Initialize icon
		Drawable icon = getResources().getDrawable(pin);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());

		// Create my overlay and show it
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon, stamp);
		OverlayItem item = new OverlayItem(geopoint, txt, null);
		overlay.addItem(item);

		return overlay;
	}
}
