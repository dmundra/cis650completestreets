/**
 * 
 */
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
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

/**
 * @author Daniel Mundra
 * 
 */
public class SettingTabView extends Activity {

	// Used for logging
	private static final String TAG = "MapTabViewLog";
	private static final String PREFS_NAME = "HelpPrefsFile";
	
	EditText portNumberText;
	EditText topText;
	EditText leftText;
	EditText bottomText;
	EditText rightText;

	/**
	 * This is used for toast messages
	 * 
	 * @return SettingTabView
	 */
	private SettingTabView getState() {
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingtabview);

		Log.i(TAG, "Settings view started.");

		Button clearGeoStamps = (Button) findViewById(R.id.clearAllButton);
		Button recreateTables = (Button) findViewById(R.id.recreateAllButton);
		Button savePortNo = (Button) findViewById(R.id.savePortNOButton);
		Button saveBorder = (Button) findViewById(R.id.saveBorderButton);
		CheckBox socketService = (CheckBox) findViewById(R.id.socketCheck);
		portNumberText = (EditText) findViewById(R.id.portNumberText);
		topText = (EditText) findViewById(R.id.topText);
		leftText = (EditText) findViewById(R.id.leftText);
		bottomText = (EditText) findViewById(R.id.bottomText);
		rightText = (EditText) findViewById(R.id.rightText);

		// Load preferences for whether service started or not
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		socketService.setChecked(settings.getBoolean("serviceStart", false));
		portNumberText.setText(settings.getString("portnumber", "4444"));
		topText.setText(settings.getString("topcoord", "0.0"));
		leftText.setText(settings.getString("leftcoord", "0.0"));
		bottomText.setText(settings.getString("bottomcoord", "0.0"));
		rightText.setText(settings.getString("rightcoord", "0.0"));		
		
		// Used to save port no
		savePortNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				// Save port number to preferences
				String portno = portNumberText.getText().toString();
				editor.putString("portnumber", portno);
				Log.i(TAG, "Port No " + portno + " saved.");
				editor.commit();			
			}
		});
		
		// Used to save border coords
		saveBorder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				// Save border coordinates to preferences
				String topcoord = topText.getText().toString();
				String leftcoord = leftText.getText().toString();
				String bottomcoord = bottomText.getText().toString();
				String rightcoord = rightText.getText().toString();
				editor.putString("topcoord", topcoord);
				editor.putString("leftcoord", leftcoord);
				editor.putString("bottomcoord",bottomcoord);
				editor.putString("rightcoord", rightcoord);
				Log.i(TAG, "Coord (" + topcoord + "," + leftcoord + "," + bottomcoord + "," + rightcoord + ") saved.");	
				editor.commit();			
			}
		});

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

					Toast.makeText(getState().getBaseContext(), "Service on!",
							Toast.LENGTH_LONG).show();
				} else {
					Log.i(TAG, "Socket service off.");
					serviceStart = false;

					Toast.makeText(getState().getBaseContext(), "Service off!",
							Toast.LENGTH_LONG).show();
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
}
