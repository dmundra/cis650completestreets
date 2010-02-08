package edu.uoregon;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

/**
 * Map Tab that will show the map and the phone's current location
 * 
 * @author Daniel Mundra
 * 
 */
public class MapTabView extends MapActivity {
	/** Called when the activity is first created. */
	private static LocationManager lm;
	private static LocationListener ll;
	private static boolean socketData;
	private MapView mapView;
	private MapController mapControl;
	private IGeoDB db;
	// Used for logging
	private static final String TAG = "MapTabViewLog";
	private static final String PREFS_NAME = "HelpPrefsFile";

	// Represents current location that we will save
	public static Location currentLocation;
	private static GeoPoint curLocPoint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maptabview);

		Log.i(TAG, "Map view started.");

		mapView = (MapView) findViewById(R.id.mapView);
		// Show zoom in/out buttons
		mapView.setBuiltInZoomControls(true);
		// Standard view of the map(map/sat)
		mapView.setSatellite(true);

		// Map Controller, we want the zoom to be close to street level
		mapControl = mapView.getController();
		mapControl.setZoom(18);

		// Load preferences for whether service started or not
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		socketData = settings.getBoolean("serviceStart", false);

		if (!socketData) {
			// Initialize location manager and get current location and
			// current location geo point. Should do this only once when
			// application starts and then the location manager should
			// manage the location changes.
			initLocationManager();
			Log.i(TAG, "Load location manager");
			currentLocation = lm
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			curLocPoint = new GeoPoint(
					(int) (currentLocation.getLatitude() * 1E6),
					(int) (currentLocation.getLongitude() * 1E6));
		} else {
			Log.i(TAG, "Load geopoint from server socket");
			curLocPoint = edu.uoregon.server.LocationServer.mapData;
		}

		Log.i(TAG, "Load current location: " + currentLocation);
		Log.i(TAG, "Load current geo point: " + curLocPoint);

		// Load map with all pins
		loadMap();
	}

	public void loadMap() {
		Log.i(TAG, "Load Map");

		// Sets up a connection to the database.
		db = GeoDBConnector.open(this);

		// Initialize icon
		Drawable currLocIcon = getResources().getDrawable(R.drawable.pin);
		currLocIcon.setBounds(0, 0, currLocIcon.getIntrinsicWidth(),
				currLocIcon.getIntrinsicHeight());

		Drawable saveLocIcon = getResources().getDrawable(R.drawable.green);
		currLocIcon.setBounds(0, 0, saveLocIcon.getIntrinsicWidth(),
				saveLocIcon.getIntrinsicHeight());

		MapOverlay curLocOverlay = new MapOverlay(currLocIcon);
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();

		// Load and display saved locations including current one if saved
		Iterator<GeoStamp> pins = db.getGeoStamps().iterator();
		boolean currLocNotSaved = true;
		while (pins.hasNext()) {
			GeoStamp next = pins.next();
			Log.i(TAG, "Load saved geostamp: " + next);
			MapOverlay nextOverlay = new MapOverlay(saveLocIcon);
			OverlayItem nextOverlayItem = new OverlayItem(next.getGeoPoint(),
					"Saved Location", null);
			nextOverlay.addItem(nextOverlayItem);
			listOfOverlays.add(nextOverlay);

			if (!socketData) {
				GeoStamp currLoc = new GeoStamp(currentLocation);
				if (next.equals(currLoc)) {
					currLocNotSaved = false;
				}
			}
		}

		// If current location is not save then it will display
		// with red pin
		if (currLocNotSaved) {
			OverlayItem curLocItem = new OverlayItem(curLocPoint,
					"Current Location", null);
			curLocOverlay.addItem(curLocItem);
			listOfOverlays.add(curLocOverlay);
		}

		// Animate to current location
		mapControl.animateTo(curLocPoint);

		mapView.invalidate();

		// Close db
		db.close();
	}

	/**
	 * Initialize the location manager
	 */
	private void initLocationManager() {
		Log.i(TAG, "Initialize location manager");

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ll = new LocationListener() {

			public void onLocationChanged(Location newLocation) {
				currentLocation = lm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				Log.i(TAG, "Location updated: " + currentLocation);
				curLocPoint = new GeoPoint(
						(int) (currentLocation.getLatitude() * 1E6),
						(int) (currentLocation.getLongitude() * 1E6));
				Log.i(TAG, "Geopoint updated: " + curLocPoint);
				loadMap();
			}

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};

		// Current location is updated when user moves 10 meters.
		// TODO: Test on phone
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, ll);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "Location listerner removed.");
		Log.i(TAG, "Map view closed.");

		// On destroy we stop listening to updates
		// TODO: Test on phone
		if (!socketData)
			lm.removeUpdates(ll);
		super.onDestroy();
	}
}
