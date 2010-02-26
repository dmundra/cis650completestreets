/**
 * 
 */
package edu.uoregon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;
import edu.uoregon.log.CSLog;

/**
 * Settings tab will house all preferences pertaining to this app
 * @author Daniel Mundra
 * 
 * David -- 2/20/2010 -- Added close button
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

		CSLog.i(TAG, "Settings view started.");

		Button clearGeoStamps = (Button) findViewById(R.id.clearAllButton);
		//Button recreateTables = (Button) findViewById(R.id.recreateAllButton);
		Button savePortNo = (Button) findViewById(R.id.savePortNOButton);
		//Button saveBorder = (Button) findViewById(R.id.saveBorderButton);
		CheckBox socketService = (CheckBox) findViewById(R.id.socketCheck);
//		Button saveLog = (Button) findViewById(R.id.saveLogButton);
		final Button pushToWeb = (Button) findViewById(R.id.pushToWebButton);		
		portNumberText = (EditText) findViewById(R.id.portNumberText);
		final Spinner borderS = (Spinner) findViewById(R.id.borderS);

//		topText = (EditText) findViewById(R.id.topText);
//		leftText = (EditText) findViewById(R.id.leftText);
//		bottomText = (EditText) findViewById(R.id.bottomText);
//		rightText = (EditText) findViewById(R.id.rightText);

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
				CSLog.i(TAG, "Port No " + portno + " saved.");
				editor.commit();			
			}
		});
		
		// Used to save border coords
		final ArrayAdapter adapter = ArrayAdapter.createFromResource(
	            this, R.array.borders, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    borderS.setAdapter(adapter);
	    //TODO set current value

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
				CSLog.i(TAG, "Coord (" + topcoord + "," + leftcoord + "," + bottomcoord + "," + rightcoord + ") saved.");	
				editor.commit();			
			}
		});

		// Used to clear all geo stamps
		clearGeoStamps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CSLog.i(TAG, "Clear button clicked.");

				IGeoDB db = GeoDBConnector.open(getApplicationContext());

				db.deleteAllGeoStamps();
				CSLog.i(TAG, "Deleted all geo stamps!");
				
				CSLog.saveLog();
				CSLog.i(TAG, "Saved the log!");
				
				db.recreateTables();
				CSLog.i(TAG, "Recreated all the tables!");

				Toast.makeText(getApplicationContext(), "Data cleared!",
						Toast.LENGTH_LONG).show();

				db.close();
			}
		});

//		recreateTables.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CSLog.i(TAG, "Recreate button clicked.");
//
//				IGeoDB db = GeoDBConnector.open(getApplicationContext());
//
//				db.recreateTables();
//				CSLog.i(TAG, "Recreated all the tables!");
//
//				Toast.makeText(getApplicationContext(), "Tables recreated!",
//						Toast.LENGTH_LONG).show();
//
//				db.close();
//			}
//		});
		
//		saveLog.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CSLog.i(TAG, "Save log button clicked.");
//
//				CSLog.saveLog();
//				CSLog.i(TAG, "Saved the log!");
//
//				Toast.makeText(getApplicationContext(), "Saved and empties the log!",
//						Toast.LENGTH_LONG).show();
//			}
//		});
		
		
		pushToWeb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CSLog.i(TAG, "Push to web button clicked.");

				Intent intent = new Intent().setClassName("edu.uoregon", "edu.uoregon.WebPushView");
		
				//get the user name:
				
				intent.putExtra("userName", ((EditText) findViewById(R.id.pushToWebName)).getText().toString());
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		// Checkbox to start and stop the service
		socketService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				CSLog.i(TAG, "Socket checkbox selected.");
				boolean serviceStart;

				if (isChecked) {
					CSLog.i(TAG, "Socket service on.");
					serviceStart = true;

					Toast.makeText(getState().getBaseContext(), "Service on!",
							Toast.LENGTH_LONG).show();
				} else {
					CSLog.i(TAG, "Socket service off.");
					serviceStart = false;

					Toast.makeText(getState().getBaseContext(), "Service off!",
							Toast.LENGTH_LONG).show();
				}

				// Save user preferences of service starting
				CSLog.i(TAG, "Socket state saved to file.");

				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("serviceStart", serviceStart);
				editor.commit();
			}
		});
		
		Button backButton = (Button) findViewById(R.id.closeSettingsButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// return to our calling activity
				finish();
			}
		});
	}
}
