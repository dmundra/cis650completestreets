package edu.uoregon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

/**
 * Tab that will display help and guidelines. Also have some setting buttons.
 * 
 * @author Daniel Mundra
 * 
 *         David -- 2/6/2010 -- Added recreate tables button.
 * 
 */
public class HelpTabView extends Activity {

	// Used for logging
	private static final String TAG = "HelpTabViewLog";
	private static final String PREFS_NAME = "HelpPrefsFile";

	/**
	 * This is used for toast messages
	 * 
	 * @return HelpTabView
	 */
	private HelpTabView getState() {
		return this;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helptabview);

		Log.i(TAG, "Help view started.");

		Button clearGeoStamps = (Button) findViewById(R.id.clearAllButton);
		Button recreateTables = (Button) findViewById(R.id.recreateAllButton);
		CheckBox socketService = (CheckBox) findViewById(R.id.socketCheck);
		TextView helpText = (TextView) findViewById(R.id.helpText);

		// Load preferences for whether service started or not
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean checked = settings.getBoolean("serviceStart", false);
		socketService.setChecked(checked);

		// Load help/guidelines text
		// TODO: Right now just placeholder text
		helpText
				.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
						+ "Donec tristique semper justo at posuere. In vitae tortor ac "
						+ "velit tristique vehicula. Donec nec libero ut urna aliquet "
						+ "pharetra eget sed velit. Phasellus ut purus et enim iaculis "
						+ "porta sit amet ac est. Sed pharetra suscipit quam, vel ornare "
						+ "eros laoreet nec. Aenean adipiscing risus ac mi tincidunt "
						+ "pharetra. Sed turpis lacus, elementum a pretium non, interdum "
						+ "vitae justo. Etiam dignissim feugiat vulputate. Quisque hendrerit, "
						+ "est non ultrices viverra, orci odio consectetur est, ut ultricies "
						+ "odio turpis at dui. Ut hendrerit imperdiet nisi eget fringilla. "
						+ "Pellentesque non nibh diam. Fusce sagittis, orci et commodo auctor, "
						+ "pharetra. Sed turpis lacus, elementum a pretium non, interdum "
						+ "vitae justo. Etiam dignissim feugiat vulputate. Quisque hendrerit, "
						+ "est non ultrices viverra, orci odio consectetur est, ut ultricies "
						+ "odio turpis at dui. Ut hendrerit imperdiet nisi eget fringilla. "
						+ "Pellentesque non nibh diam. Fusce sagittis, orci et commodo auctor, "
						+ "pharetra. Sed turpis lacus, elementum a pretium non, interdum "
						+ "vitae justo. Etiam dignissim feugiat vulputate. Quisque hendrerit, "
						+ "est non ultrices viverra, orci odio consectetur est, ut ultricies "
						+ "odio turpis at dui. Ut hendrerit imperdiet nisi eget fringilla. "
						+ "Pellentesque non nibh diam. Fusce sagittis, orci et commodo auctor, "
						+ "nisi sem luctus erat, vel egestas lorem mauris eget ligula. ");

		// Used to clear all geo stamps
		clearGeoStamps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Clear button clicked.");

				IGeoDB db = GeoDBConnector.open(getApplicationContext());

				db.deleteAllGeoStamps();
				Log.i(TAG, "Deleted all geo stamps!");

				Toast.makeText(getApplicationContext(), "Geo stampes cleared!",
						Toast.LENGTH_LONG).show();

				db.close();
			}
		});

		recreateTables.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Recreate button clicked.");

				IGeoDB db = GeoDBConnector.open(getApplicationContext());

				db.recreateTables();
				Log.i(TAG, "Recreated all the tables!");

				Toast.makeText(getApplicationContext(), "Tables recreated!",
						Toast.LENGTH_LONG).show();

				db.close();
			}
		});

		// Checkbox to start and stop the service
		socketService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.i(TAG, "Socket checkbox selected.");				
				boolean serviceStart;

				if (isChecked) {
					Log.i(TAG, "Socket service on.");					
					serviceStart = true;

					Toast.makeText(getState().getBaseContext(),
							"Service on!", Toast.LENGTH_LONG).show();
				} else {
					Log.i(TAG, "Socket service off.");					
					serviceStart = false;

					Toast.makeText(getState().getBaseContext(),
							"Service off!", Toast.LENGTH_LONG).show();
				}

				// Save user preferences of service starting
				Log.i(TAG, "Socket state saved to file.");
				
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("serviceStart", serviceStart);
				editor.commit();
			}
		});
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "Help view closed.");
		super.onDestroy();
	}
}
