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
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapTabView extends MapActivity {
	/** Called when the activity is first created. */
	private LocationManager lm;
	private LocationListener ll;
	private MapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maptabview);

		mapView = (MapView) findViewById(R.id.mapView);
		// show zoom in/out buttons
		mapView.setBuiltInZoomControls(true);
		// Standard view of the map(map/sat)
		mapView.setSatellite(true);

		initLocationManager();
		createAndShowMyItemizedOverlay(lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER));
	}

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
	 * This method will bea called whenever a cahnge of the current position is
	 * submitted via the GPS.
	 * 
	 * @param newLocation
	 */
	protected void createAndShowMyItemizedOverlay(Location newLocation) {
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		if (overlays.size() > 0) {
			for (Iterator<Overlay> iterator = overlays.iterator(); iterator
					.hasNext();) {
				iterator.next();
				iterator.remove();
			}
		}

		// transform the location to a geopoint
		GeoPoint geopoint = new GeoPoint(
				(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
						.getLongitude() * 1E6));

		// initialize icon
		Drawable icon = getResources().getDrawable(R.drawable.pin);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());

		// create my overlay and show it
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon);
		OverlayItem item = new OverlayItem(geopoint, "Current Location", null);
		overlay.addItem(item);
		mapView.getOverlays().add(overlay);

		// move to location
		mapView.getController().animateTo(geopoint);

		// redraw map
		mapView.postInvalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	protected class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> items;
		private Drawable marker;

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

		public void addItem(OverlayItem item) {
			items.add(item);
			populate();
		}

		@Override
		protected boolean onTap(int i) {
			Toast.makeText(getBaseContext(), items.get(i).getPoint().toString(),
					Toast.LENGTH_SHORT).show();

			return true;
		}
	}
}
