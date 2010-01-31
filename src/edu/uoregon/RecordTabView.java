package edu.uoregon;

import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Tab to record location with a picture and voice memo.
 * 
 * @author Daniel Mundra
 * 
 */
public class RecordTabView extends MapActivity {

	// public static IGeoDB db;
	private GeoStamp geoStamp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordtabview);

		final Button saveButton = (Button) findViewById(R.id.saveButton);
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final Button recordButton = (Button) findViewById(R.id.recordButton);
		final Button captureButton = (Button) findViewById(R.id.pictureButton);
		final MapView mapThumbView = (MapView) findViewById(R.id.mapThumbView);

		geoStamp = new GeoStamp(edu.uoregon.MapTabView.currentLocation);

		loadMapThumb(mapThumbView, geoStamp);

		// Sets up a connection to the database.
		// db = GeoDBConnector.open(this);

		recordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// take us to the record audio page:
				Intent intent = new Intent(RecordTabView.this,
						RecordAudioView.class);
				// TODO: pass in id
				// intent.putExtra("id", id);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		captureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO To open the camera to take picture

			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// Save geostamp
				// TODO: Since there is no db backend
				// I am getting the list from MapTabView
				geoStamp.setEdit();
				edu.uoregon.MapTabView.stamps.add(geoStamp);

				Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_LONG)
						.show();

				TabHost tabHost = edu.uoregon.Main.mTabHost;
				tabHost.setCurrentTab(0);
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TabHost tabHost = edu.uoregon.Main.mTabHost;
				tabHost.setCurrentTab(0);
			}
		});
	}

	/**
	 * Load the map thumb view
	 * 
	 * @param mapThumbView
	 * @param currLoc
	 */
	private void loadMapThumb(MapView mapThumbView, GeoStamp stamp) {
		// Standard view of the map(map/sat)
		mapThumbView.setSatellite(true);

		// Map Controller, we want the zoom to be close to street level
		MapController mapControl = mapThumbView.getController();
		mapControl.setZoom(18);

		List<Overlay> overlays = mapThumbView.getOverlays();

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

		// Initialize icon
		// TODO: This logic will be similar across the board to separate add
		// and edit
		Drawable icon;
		if (stamp.isEdit()) {
			icon = getResources().getDrawable(R.drawable.green);
		} else {
			icon = getResources().getDrawable(R.drawable.pin);
		}

		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());

		// Create my overlay and show it
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon, stamp);
		OverlayItem item = new OverlayItem(stamp.getGeoPoint(),
				"Current Location", null);
		overlay.addItem(item);
		mapThumbView.getOverlays().add(overlay);

		// Move to location
		mapThumbView.getController().animateTo(stamp.getGeoPoint());

		// Redraw map
		mapThumbView.postInvalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
