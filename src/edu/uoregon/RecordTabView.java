package edu.uoregon;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

/**
 * Tab to record location with a picture and voice memo.
 * 
 * @author Daniel Mundra
 * 
 */
public class RecordTabView extends MapActivity {

	private IGeoDB db;
	private GeoStamp geoStamp;
	private boolean prevSaved = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordtabview);

		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final Button recordButton = (Button) findViewById(R.id.recordButton);
		final Button captureButton = (Button) findViewById(R.id.pictureButton);
		final MapView mapThumbView = (MapView) findViewById(R.id.mapThumbView);

		// Create geo stamp with current location
		geoStamp = new GeoStamp(edu.uoregon.MapTabView.currentLocation);

		// Sets up a connection to the database.
		db = GeoDBConnector.open(this);
		
		// Get the list of geo stamps
		List<GeoStamp> stamps = db.getGeoStamps();
		
		// Checks if geo stamp has already been saved in the db.
		// If it has then we will get the same one back, else
		// we add it to the db.		
		if(stamps.contains(geoStamp)) {
			geoStamp = stamps.get(stamps.indexOf(geoStamp));
			
			// If the geo stamp was already saved we will use this flag
			// in the other code to update the stamp
			prevSaved = true;
		} else {
			db.addGeoStamp(geoStamp);			
		}

		// Load thumbnail map
		loadMapThumb(mapThumbView);

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
				Intent intent = new Intent(RecordTabView.this,
						TakePictureView.class);
				intent.putExtra("geoStampID", geoStamp.getDatabaseID());
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
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
	private void loadMapThumb(MapView mapThumbView) {
		// Standard view of the map(map/sat)
		mapThumbView.setSatellite(true);

		// Map Controller, we want the zoom to be close to street level
		MapController mapControl = mapThumbView.getController();
		mapControl.setZoom(18);

		// Initialize icon
		Drawable currLocIcon = null;
		if(prevSaved) {
			currLocIcon = getResources().getDrawable(R.drawable.green);			
		} else {
			currLocIcon = getResources().getDrawable(R.drawable.pin);			
		}
		
		currLocIcon.setBounds(0, 0, currLocIcon.getIntrinsicWidth(),
				currLocIcon.getIntrinsicHeight());

		MapOverlay curLocOverlay = new MapOverlay(currLocIcon);
		List<Overlay> listOfOverlays = mapThumbView.getOverlays();
		listOfOverlays.clear();

		// Drop pin for location about to be saved
		OverlayItem curLocItem = new OverlayItem(geoStamp.getGeoPoint(),
				"Current Location", null);
		curLocOverlay.addItem(curLocItem);
		listOfOverlays.add(curLocOverlay);

		// Animate to current location
		mapControl.animateTo(geoStamp.getGeoPoint());

		mapThumbView.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}
}
