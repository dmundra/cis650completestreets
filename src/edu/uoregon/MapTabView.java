package edu.uoregon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TabHost;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Map Tab that will show the map and the phone's
 * current location
 * @author Daniel Mundra
 *
 */
public class MapTabView extends MapActivity {
	/** Called when the activity is first created. */
	public static LocationManager lm;
	public static LocationListener ll;
	public static Location currentLocation;
	private MapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maptabview);

		mapView = (MapView) findViewById(R.id.mapView);
		// Show zoom in/out buttons
		mapView.setBuiltInZoomControls(true);
		// Standard view of the map(map/sat)
		mapView.setSatellite(true);

		// Initialize the location manager
		initLocationManager();
		// Set current location
		currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// Drop pin on current location
		createAndShowMyItemizedOverlay(currentLocation);
	}

	/**
	 * Initialize the location manager
	 */
	private void initLocationManager() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ll = new LocationListener() {

			public void onLocationChanged(Location newLocation) {
				createAndShowMyItemizedOverlay(newLocation);
			}

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};

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

		// First remove old overlay
		if (overlays.size() > 0) {
			for (Iterator<Overlay> iterator = overlays.iterator(); iterator
					.hasNext();) {
				iterator.next();
				iterator.remove();
			}
		}

		// Transform the location to a geopoint
		GeoPoint geopoint = new GeoPoint(
				(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
						.getLongitude() * 1E6));

		// Initialize icon
		Drawable icon = getResources().getDrawable(R.drawable.pin);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());

		// Create my overlay and show it
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon);
		OverlayItem item = new OverlayItem(geopoint, "Current Location", null);
		overlay.addItem(item);
		mapView.getOverlays().add(overlay);

		// Move to location
		mapView.getController().animateTo(geopoint);

		// Redraw map
		mapView.postInvalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * This class will be used to overlay icons on the map view
	 */
	protected class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		private final List<OverlayItem> items;
		private final Drawable marker;

		/**
		 * Constructor to overlay icon on map
		 * 
		 * @param defaultMarker
		 */
		public MyItemizedOverlay(Drawable defaultMarker) {
			super(defaultMarker);
			items = new ArrayList<OverlayItem>();
			marker = defaultMarker;
		}

		@Override
		protected OverlayItem createItem(int index) {
			return (OverlayItem) items.get(index);
		}

		@Override
		public int size() {
			return items.size();

		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}

		/**
		 * Adds overlay item to the map
		 * 
		 * @param item - OverlayItem
		 */
		public void addItem(OverlayItem item) {
			items.add(item);
			populate();
		}

		@Override
		protected boolean onTap(int i) {
			// Load the record tab
			TabHost tabHost = edu.uoregon.Main.mTabHost;
			tabHost.setCurrentTab(1);

			return true;
		}
	}
}
