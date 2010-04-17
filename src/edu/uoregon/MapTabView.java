package edu.uoregon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;
import edu.uoregon.log.CSLog;

/**
 * Map Tab that will show the map and the phone's current location
 * 
 * @author Daniel Mundra
 * 
 *         David -- 2/20/2010 -- Added menu functionality and record button.
 *         This is now the main activity. See {@link Main}
 */
public class MapTabView extends MapActivity {

	private static LocationManager lm;
	private static LocationListener ll;
	private static boolean socketData;
	private MapView mapView;
	private final int ZOOMLEVEL = 20;
	private final int LLDISTANCE = 5;
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
	// private ServerSocket serverSocket;
	private HTTPListener httpListener = null;
	private final int HTTP_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maptabview);

		CSLog.i(TAG, "Map view started.");

		mapView = (MapView) findViewById(R.id.mapView);
		// Show zoom in/out buttons
		mapView.setBuiltInZoomControls(true);
		// Standard view of the map(map/sat)
		mapView.setSatellite(true);

		// Map Controller, we want the zoom to be close to street level
		mapControl = mapView.getController();
		mapControl.setZoom(ZOOMLEVEL);

		// Setup the record button
		Button record = (Button) findViewById(R.id.recordFromMapTab);
		record.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_RECORD);
			}
		});

	}

	public void loadMap(boolean nonUI) {

		CSLog.i(TAG, "Load Map");

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
			CSLog.i(TAG, "Load saved geostamp: " + next);
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
		CSLog.i(TAG, "Animate to current geo point: " + curLocPoint);
		checkBorder();

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
	 * Method to check whether current location is outside the border. If border
	 * is crossed the phone will show a popup and vibrate.
	 */
	private void checkBorder() {
		CSLog.i(TAG, "Check if border has been crossed.");

		// Load preferences file
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		final Border border = Border.valueOf(settings.getString("border",
		        "NONE"));

		if (border == Border.NONE) {
			CSLog.i(TAG, "Border is set to " + border.name()
			        + ", which means no border");
		} else {
			try {

				if (!border.contains(curLocPoint.getLatitudeE6(), curLocPoint
				        .getLongitudeE6())) {
					CSLog.i(TAG, "Border cross alerted.");

					Toast.makeText(MapTabView.this, "Outside the border",
					        Toast.LENGTH_LONG).show();

					((Vibrator) getSystemService(VIBRATOR_SERVICE))
					        .vibrate(1000);
				}
			} catch (Exception e) {
				final String msg = "Something wrong with border coords";

				Toast.makeText(MapTabView.this, msg, Toast.LENGTH_LONG).show();

				CSLog.i(TAG, msg);
			}
		}
	}

	/**
	 * Initialize the location manager
	 */
	private void initLocationManager() {
		CSLog.i(TAG, "Initialize location manager");

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ll = new LocationListener() {

			public void onLocationChanged(Location newLocation) {
				Location currentLocation = lm
				        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
				CSLog.i(TAG, "Location updated: " + currentLocation);
				curGeoStamp = new GeoStamp(currentLocation.getLatitude(),
				        currentLocation.getLongitude());
				curLocPoint = curGeoStamp.getGeoPoint();
				CSLog.i(TAG, "Geopoint updated: " + curLocPoint);
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
	protected void onPause() {

		CSLog.i(TAG, "Map view paused.");

		// On destroy we stop listening to updates
		if (!socketData) {
			lm.removeUpdates(ll);
		} else {
			CSLog.i(TAG, "Close http listener.");
			httpListener.letDie();
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		CSLog.e(TAG, "Map view resumed.");

		// Load preferences file
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// Get preference for whether service started or not
		socketData = settings.getBoolean("serviceStart", false);

		if (!socketData) {
			// Initialize location manager and get current location and
			// current location geo point. Should do this only once when
			// application starts and then the location manager should
			// manage the location changes.
			try {
				initLocationManager();
				CSLog.i(TAG, "Load location manager");

				Location currentLocation = lm
				        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
				CSLog.i(TAG, "Load current location: " + currentLocation);
				curGeoStamp = new GeoStamp(currentLocation.getLatitude(),
				        currentLocation.getLongitude());
				curLocPoint = curGeoStamp.getGeoPoint();
				CSLog.i(TAG, "Load current geo point: " + curLocPoint);
			} catch (NullPointerException ne) {
				Toast
				        .makeText(getApplicationContext(),
				                "Location was not set! Closing App!",
				                Toast.LENGTH_LONG).show();
				finish();
			}
		} else {
			// try {
			// Loads default geo stamp
			if (defaultStamp) {
				curGeoStamp = new GeoStamp(DEFAULT_LAT, DEFAULT_LON);
				curLocPoint = curGeoStamp.getGeoPoint();
				defaultStamp = false;
			}

			// Get port number from preferences
			final String httpIDS = settings.getString("portnumber", "");
			int httpID = HTTP_ID;
			if (!httpIDS.equals("")) {
				httpID = Integer.parseInt(httpIDS);
			}
			CSLog.i(TAG, "Got httpID: " + httpID);

			// Creates a server socket and starts the socket listener
			// serverSocket = new ServerSocket(portno);
			// new Thread(new SocketLocationListener(serverSocket)).start();
			httpListener = new HTTPListener();
			httpListener.execute(httpID);
			// } catch (IOException e) {
			// CSLog.e(TAG, e.getMessage());
			// }
		}

		// Load map with all pins
		loadMap(false);

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		CSLog.i(TAG, "Map view destroyed.");

		// save log:
		CSLog.saveLog();

		super.onDestroy();
	}

	/**
	 * Creates dialog boxes.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		// Record dialog
		case DIALOG_RECORD: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final CharSequence[] items = { "Take picture", "Record audio" };

			builder.setTitle("Record location").setItems(items,
			        new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int item) {
					        switch (item) {
					        case 0: { // Picture
						        Intent intent = new Intent().setClassName(
						                "edu.uoregon",
						                "edu.uoregon.TakePictureView");
						        CSLog.i(TAG, "Record picture clicked.");

						        // Update the current geoStamp
						        updateGeoStamp();

						        // Put the database id into the intent
						        intent.putExtra("geoStampID", curGeoStamp
						                .getDatabaseID());
						        CSLog.i(TAG, "Sending to get picture, id: "
						                + curGeoStamp.getDatabaseID());
						        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						        startActivity(intent);
						        break;
					        }
					        case 1: { // Audio
						        CSLog.i(TAG, "Record audio clicked.");

						        // Update the current geoStamp
						        updateGeoStamp();

						        // Put the database id into the intent
						        Intent intent = new Intent().setClassName(
						                "edu.uoregon",
						                "edu.uoregon.RecordAudioView");
						        intent.putExtra("geoId", new Integer(
						                curGeoStamp.getDatabaseID()));
						        CSLog.i(TAG, "Sending to get audio, id: "
						                + curGeoStamp.getDatabaseID());
						        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						        startActivity(intent);
						        break;
					        }
					        }

					        // Reload map after saving
					        loadMap(false);
				        }
			        }).setCancelable(false).setPositiveButton("Done",
			        new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int id) {
					        dialog.cancel();
				        }
			        });
			dialog = builder.create();
			break;
		}
			// Instructions dialog
		case DIALOG_HELP: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Instructions").setMessage(
			        getString(R.string.instructions)).setCancelable(false)
			        .setPositiveButton("Ok",
			                new DialogInterface.OnClickListener() {
				                public void onClick(DialogInterface dialog,
				                        int id) {
					                dialog.cancel();
				                }
			                });
			dialog = builder.create();
			break;
		}
		}
		return dialog;
	}

	private final int DIALOG_RECORD = 0;
	private final int DIALOG_HELP = 1;

	private void updateGeoStamp() {
		if (curGeoStamp.isNew()) {
			db = GeoDBConnector.open(this);
			db.addGeoStamp(curGeoStamp);
			db.close();
		}
	}

	/**
	 * Invoked during init to give the Activity a chance to set up its Menu.
	 * 
	 * @param menu
	 *            the Menu to which entries may be added
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_HELP, Menu.NONE, "Instructions");
		menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
		return true;
	}

	/**
	 * Invoked when the user selects an item from the Menu.
	 * 
	 * @param item
	 *            the Menu entry which was selected
	 * @return true if the Menu item was legit (and we consumed it), false
	 *         otherwise
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_HELP:
			showDialog(DIALOG_HELP);
			return true;
		case MENU_SETTINGS:
			Intent intent = new Intent().setClassName("edu.uoregon",
			        "edu.uoregon.SettingTabView");
			CSLog.i(TAG, "Opening settings");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		return false;
	}

	private static final int MENU_HELP = 0;
	private static final int MENU_SETTINGS = 1;

	private class HTTPListener extends AsyncTask<Integer, Double, Object> {

		private boolean letDie = false;
		private final String TAG = "MapTabView.HTTPListener";

		// called by ui:
		protected void onProgressUpdate(Double... values) {

			// we just want the last values:
			curGeoStamp = new GeoStamp(values[values.length - 2],
			        values[values.length - 1]);
			curLocPoint = curGeoStamp.getGeoPoint();

			CSLog.i(TAG, "Load current geo point: " + curLocPoint);
			loadMap(false);
		}

		@Override
		protected Object doInBackground(Integer... arg0) {

			CSLog.i(TAG, "Load geopoint from http listener");
			try {
				final URL url = new URL(
				        "https://www.coglink.com:8080/AndroidGPSTest/LocationTransfer?id="
				                + arg0[0]);

				// Get a SAXParser from the SAXPArserFactory
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				// Get the XMLReader of the SAXParser we created.
				XMLReader xr = sp.getXMLReader();

				// Create a new ContentHandler and apply it to the XML-Reader
				LocationHandler locHandler = new LocationHandler();
				xr.setContentHandler(locHandler);

				while (!letDie) {

					// we only want to get data every so often:
					try {
						Thread.sleep(2000);

						CSLog.i(TAG, "Checking http service");

						// Parse the xml-data from our URL
						InputStream mystream = url.openStream();

						InputSource is = new InputSource(mystream);
						xr.parse(is);

						// now get location from handler?
						publishProgress(locHandler.getLoc()[0], locHandler
						        .getLoc()[1]);

					} catch (Exception ex) {
						CSLog.e(TAG, "(inner loop exception, continuing) "
						        + ex.getMessage());
					}

				}
			} catch (Exception e) {
				CSLog.e(TAG, "(outer loop exception, exiting) "
				        + e.getMessage());
			}

			// we don't return anything:
			return null;
		}

		private void letDie() {
			letDie = true;
		}

	}

	/**
	 * this is our sax handler (for when we get our location info from http)
	 */
	private static class LocationHandler extends DefaultHandler {

		// here's an array of our lat, lon
		private double[] loc = new double[2];

		@Override
		public void startElement(String namespacesURI, String localName,
		        String qName, Attributes atts) throws SAXException {
			if (localName.equals("location")) {
				// we assume that the xml is valid (i.e. doubles are doubles and
				// floats are floats)
				loc[0] = (Double.parseDouble(atts.getValue("lat")));
				loc[1] = (Double.parseDouble(atts.getValue("lon")));
			}
		}

		@Override
		public void endElement(String namespaceURL, String localName,
		        String qName) throws SAXException {
			// do nothing
		}

		@Override
		public void characters(char ch[], int start, int length) {
			// do nothing
		}

		@Override
		public void startDocument() throws SAXException {
			// do nothing
		}

		@Override
		public void endDocument() throws SAXException {
			// do nothing
		}

		public double[] getLoc() {
			return loc;
		}

	}

}