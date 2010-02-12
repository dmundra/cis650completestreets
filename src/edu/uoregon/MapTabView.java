package edu.uoregon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

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

	private static LocationManager lm;
	private static LocationListener ll;
	private static boolean socketData;
	private MapView mapView;
	private final int ZOOMLEVEL = 20;
	private final int LLDISTANCE = 10;
	private final double DEFAULT_LAT = 37.422006;
	private final double DEFAULT_LON = -122.084095;
	private MapController mapControl;
	private IGeoDB db;

	// Used for logging
	private static final String TAG = "MapTabViewLog";
	private static final String PREFS_NAME = "HelpPrefsFile";

	// Represents current location that we will save
	public static GeoStamp curGeoStamp;
	private static GeoPoint curLocPoint;

	// Used for socket server
	private static boolean defaultStamp = true;
	private ServerSocket serverSocket;
	private final int PORTNO = 4444;

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
		mapControl.setZoom(ZOOMLEVEL);

		// Load preferences file
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// Get preference for whether service started or not
		socketData = settings.getBoolean("serviceStart", false);

		if (!socketData) {
			// Initialize location manager and get current location and
			// current location geo point. Should do this only once when
			// application starts and then the location manager should
			// manage the location changes.
			initLocationManager();
			Log.i(TAG, "Load location manager");

			Location currentLocation = lm
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Log.i(TAG, "Load current location: " + currentLocation);
			curGeoStamp = new GeoStamp(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			curLocPoint = curGeoStamp.getGeoPoint();
			Log.i(TAG, "Load current geo point: " + curLocPoint);
		} else {
			try {
				// Loads default geo stamp
				if (defaultStamp) {
					curGeoStamp = new GeoStamp(DEFAULT_LAT, DEFAULT_LON);
					curLocPoint = curGeoStamp.getGeoPoint();
					defaultStamp = false;
				}

				// Get port number from preferences
				String portNumber = settings.getString("portnumber", "");
				int portno = PORTNO;
				if (!portNumber.equals("")) {
					portno = Integer.parseInt(portNumber);
				}
				Log.i(TAG, "Got port number: " + portno);

				// Creates a server socket and starts the socket listener
				serverSocket = new ServerSocket(portno);
				new Thread(new SocketLocationListener(serverSocket)).start();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		// Load map with all pins
		loadMap(false);
	}

	public void loadMap(boolean nonUI) {
		Log.i(TAG, "Load Map");

		// Sets up a connection to the database.
		db = GeoDBConnector.open(this);

		// Initialize icons, current is red, saved is green, current saved is
		// purple
		Drawable currLocIcon = getResources().getDrawable(R.drawable.pin);
		currLocIcon.setBounds(0, 0, currLocIcon.getIntrinsicWidth(),
				currLocIcon.getIntrinsicHeight());

		Drawable saveLocIcon = getResources().getDrawable(R.drawable.green);
		currLocIcon.setBounds(0, 0, saveLocIcon.getIntrinsicWidth(),
				saveLocIcon.getIntrinsicHeight());

		Drawable saveCurLocIcon = getResources().getDrawable(R.drawable.purple);
		currLocIcon.setBounds(0, 0, saveCurLocIcon.getIntrinsicWidth(),
				saveCurLocIcon.getIntrinsicHeight());

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

			GeoStamp currLoc = new GeoStamp(curGeoStamp.getLatitude(),
					curGeoStamp.getLongitude());
			if (next.equals(currLoc)) {
				currLocNotSaved = false;
			}
		}

		// If current location is not save then it will display
		// with red pin else load a purple pin
		if (currLocNotSaved) {
			MapOverlay curLocOverlay = new MapOverlay(currLocIcon);
			OverlayItem curLocItem = new OverlayItem(curLocPoint,
					"Current Location", null);
			curLocOverlay.addItem(curLocItem);
			listOfOverlays.add(curLocOverlay);
		} else {
			MapOverlay saveCurrOverlay = new MapOverlay(saveCurLocIcon);
			OverlayItem saveCurLocItem = new OverlayItem(curLocPoint,
					"Saved Current Location", null);
			saveCurrOverlay.addItem(saveCurLocItem);
			listOfOverlays.add(saveCurrOverlay);
		}

		// Animate to current location
		mapControl.animateTo(curLocPoint);
		checkBorder();
		Log.d(TAG, "Animate to current geo point: " + curLocPoint);

		if (!nonUI) {
			mapView.invalidate();
		} else {
			// This is when the socket listener calls a map update
			mapView.postInvalidate();
		}

		// Close db
		db.close();
	}

	/**
	 * Method to check whether current location is outside the border.
	 * If border is crossed the phone will show a popup and vibrate.
	 */
	private void checkBorder() {
		Log.i(TAG, "Check if border has been crossed.");
		
		// Load preferences file
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String topText = settings.getString("topcoord", "0.0");
		String leftText = settings.getString("leftcoord", "0.0");
		String bottomText = settings.getString("bottomcoord", "0.0");
		String rightText = settings.getString("rightcoord", "0.0");

		if (topText.equals("0.0") && leftText.equals("0.0")
				&& bottomText.equals("0.0") && rightText.equals("0.0")) {
			Log.i(TAG, "Border coords were all set to 0.0 which means no border");
		} else {
			int top = (int) (Double.parseDouble(topText) * 1E6);
			int left = (int) (Double.parseDouble(leftText) * 1E6);
			int bottom = (int) (Double.parseDouble(bottomText) * 1E6);
			int right = (int) (Double.parseDouble(rightText) * 1E6);

			if (curLocPoint.getLatitudeE6() > top
					|| curLocPoint.getLongitudeE6() > left
					|| curLocPoint.getLatitudeE6() < bottom
					|| curLocPoint.getLongitudeE6() < right) {
				Log.i(TAG, "Border cross alerted.");
				Toast.makeText(getApplicationContext(), "Outside the border",
						Toast.LENGTH_LONG).show();
				((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
			}
		}
	}

	/**
	 * Initialize the location manager
	 */
	private void initLocationManager() {
		Log.i(TAG, "Initialize location manager");

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ll = new LocationListener() {

			public void onLocationChanged(Location newLocation) {
				Location currentLocation = lm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				Log.i(TAG, "Location updated: " + currentLocation);
				curGeoStamp = new GeoStamp(currentLocation.getLatitude(),
						currentLocation.getLongitude());
				curLocPoint = curGeoStamp.getGeoPoint();
				Log.i(TAG, "Geopoint updated: " + curLocPoint);
				loadMap(false);
			}

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};

		// Current location is updated when user moves 10 meters.
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, LLDISTANCE,
				ll);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "Map view closed.");

		// On destroy we stop listening to updates
		if (!socketData) {
			Log.i(TAG, "Location listerner removed.");
			lm.removeUpdates(ll);
		} else {
			// We close the socket
			try {
				Log.i(TAG, "Close socket server.");
				serverSocket.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		super.onDestroy();
	}

	/**
	 * Location listener that gets location data from a socket connection.
	 * 
	 * @author Daniel Mundra
	 * 
	 */
	private class SocketLocationListener implements Runnable {

		private ServerSocket serverSocket;
		private Socket clientSocket;
		private final String TAG = "ServerListenerLog";
		private final int SIZE = 30;

		private SocketLocationListener(ServerSocket serv) {
			this.serverSocket = serv;
		}

		@Override
		public void run() {
			Log.i(TAG, "Load geopoint from server socket");
			try {
				while (true) {
					Log.i(TAG, "Accepting data from socket.");
					clientSocket = serverSocket.accept();

					BufferedReader in = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()),
							SIZE);
					String inputLine = "";

					while ((inputLine = in.readLine()) != null) {
						Log.i(TAG, "Got data from socket: " + inputLine);

						// We are assuming we get data as "lat,lon"
						String[] data = inputLine.split(",");
						double lat = Double.parseDouble(data[0]);
						double lon = Double.parseDouble(data[1]);

						curGeoStamp = new GeoStamp(lat, lon);
						curLocPoint = curGeoStamp.getGeoPoint();

						Log.i(TAG, "Load current geo point: " + curLocPoint);
						loadMap(true);
					}

					in.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}