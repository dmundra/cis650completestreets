package edu.uoregon;

import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Tab to record location with a picture and voice memo.
 * 
 * @author Daniel Mundra
 * 
 */
public class RecordTabView extends Activity {

	GeoStamp geoStamp;
	IGeoDB db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordtabview);

		final Button saveButton = (Button) findViewById(R.id.saveButton);
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final Button recordButton = (Button) findViewById(R.id.recordButton);
		final Button captureButton = (Button) findViewById(R.id.pictureButton);

		geoStamp = new GeoStamp(edu.uoregon.MapTabView.currentLocation);
		
		// Sets up a connection to the database. 
		db = GeoDBConnector.open(this);

		recordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//take us to the record audio page:
				Intent intent = new Intent(RecordTabView.this, RecordAudioView.class);
				//TODO: pass in id
				//intent.putExtra("id", id);
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

				// save geostamp

				Toast.makeText(getBaseContext(), geoStamp.toString(),
						Toast.LENGTH_LONG).show();
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

}
