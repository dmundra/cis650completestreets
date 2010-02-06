package edu.uoregon;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
 * David -- 2/6/2010 -- Added picture functionality.
 * 						Moved some code from onCreate to onStart.
 */
public class RecordTabView extends MapActivity {

	private IGeoDB db;
	private GeoStamp geoStamp;
	private boolean prevSaved = false;
	private ImageView recordCheck;
	private ImageView pictureCheck;
	private ImageView pictureThumb;
	private MapView mapThumbView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordtabview);

		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final Button recordButton = (Button) findViewById(R.id.recordButton);
		final Button captureButton = (Button) findViewById(R.id.pictureButton);
		mapThumbView = (MapView) findViewById(R.id.mapThumbView);

		// Checkmarks are displayed when user saves audio and picture
		recordCheck = (ImageView) findViewById(R.id.recordCheck);
		pictureCheck = (ImageView) findViewById(R.id.pictureCheck);
		pictureThumb = (ImageView) findViewById(R.id.thumbImage);

		// Create geo stamp with current location
		geoStamp = new GeoStamp(edu.uoregon.MapTabView.currentLocation);
		Log.d("RecordTabViewLog", "Geostamp loaded: " + geoStamp);

		// Sets up a connection to the database.

		recordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// take us to the record audio page:
				Intent intent = new Intent(RecordTabView.this,
						RecordAudioView.class);
				intent.putExtra("geoId", new Integer(geoStamp.getDatabaseID()));
				Log.d("RecordTabViewLog", "Sending to get audio, id: "
						+ geoStamp.getDatabaseID());
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
				Log.d("RecordTabViewLog", "Sending to get picture, id: "
						+ geoStamp.getDatabaseID());
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				db.deleteGeoStamp(geoStamp);
				Log.d("RecordTabViewLog", "Deleted geo stamp, id: "
						+ geoStamp.getDatabaseID());
				TabHost tabHost = edu.uoregon.Main.mTabHost;
				tabHost.setCurrentTab(0);
			}
		});
	}
	
	/**
	 * onResume is called every time an activity resumes focus.
	 * Therefore, we do not need static views.
	 * Static variables are bad :-)
	 */
	protected void onResume() {
		super.onResume();
		Log.d("RecordTabViewLog", "Resumed the activity");
		
		db = GeoDBConnector.open(this);
		
		// Check of the stamp already exists.
//		GeoStamp stamp = db.getGeoStamp(geoStamp.getLatitude(),geoStamp.getLongitude());
		List<GeoStamp> stamps = db.getGeoStamps();

		// If it exists, we will get the same one back, else
		// we add it to the db.
		if (stamps.contains(geoStamp)) {
			geoStamp.setDatabaseID(stamps.get(stamps.indexOf(geoStamp)).getDatabaseID());
			Log.d("RecordTabViewLog", "Geostamp already in database, id: "
					+ geoStamp.getDatabaseID());
			
			// If the geo stamp was already saved we will use this flag
			// in the other code to update the stamp
			prevSaved = true;
		}
		else {
			db.addGeoStamp(geoStamp);
			Log.d("RecordTabViewLog", "Geostamp added to database, id: "
					+ geoStamp.getDatabaseID());
		}

		// Check if record was saved before, if yes then put check mark
		int recordSaved = db.getRecordings(geoStamp.getDatabaseID()).size();
		Log.d("RecordTabViewLog", "Geostamp Recordings Saved: "
				+ recordSaved);
		if (recordSaved > 0)
			recordCheck.setVisibility(View.VISIBLE);

//		List<byte[]> pictures= db.getPictures(geoStamp.getDatabaseID());
		List<String> pictures= db.getPictureFilePaths(geoStamp.getDatabaseID());
		Log.d("RecordTabViewLog", "Geostamp Picture Saved: " + pictures.size());
		if (pictures.size() > 0) {
			pictureCheck.setVisibility(View.VISIBLE);
			try {
				Bitmap bm = BitmapFactory.decodeFile(pictures.get(0));
//				Bitmap bm = BitmapFactory.decodeByteArray(pictures.get(0), 0, pictures.get(0).length);
				pictureThumb.setImageBitmap(bm);
			} catch (OutOfMemoryError e) {
				Log.d("RecordTabViewLog", "I want more memory!");
			}
		}
			
		// Load thumbnail map
		loadMapThumb(mapThumbView);
	}

	/**
	 * Load the map thumb view
	 * 
	 * @param mapThumbView
	 * @param currLoc
	 */
	private void loadMapThumb(MapView mapThumbView) {
		Log.d("RecordTabViewLog", "Load Map");

		// Standard view of the map(map/sat)
		mapThumbView.setSatellite(true);

		// Map Controller, we want the zoom to be close to street level
		MapController mapControl = mapThumbView.getController();
		mapControl.setZoom(18);

		// Initialize icon
		Drawable currLocIcon = null;
		if (prevSaved) {
			currLocIcon = getResources().getDrawable(R.drawable.green);
			Log.d("RecordTabViewLog", "Load green pin!");
		} else {
			currLocIcon = getResources().getDrawable(R.drawable.pin);
			Log.d("RecordTabViewLog", "Load red pin!");
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
	
	protected void onPause() {
		super.onPause();
		db.close();
		Log.d("RecordTabViewLog", "I was paused");
	}
	
	protected void onDestroy() {
		super.onDestroy();
		Log.d("RecordTabViewLog", "I was destroyed!");
	}
}
