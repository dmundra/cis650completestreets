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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
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
		Button savePortNo = (Button) findViewById(R.id.savePortNOButton);
		CheckBox socketService = (CheckBox) findViewById(R.id.socketCheck);
		final Button pushToWeb = (Button) findViewById(R.id.pushToWebButton);		
		portNumberText = (EditText) findViewById(R.id.portNumberText);
		final Spinner borderS = (Spinner) findViewById(R.id.borderS);

		// Load preferences for whether service started or not
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		socketService.setChecked(settings.getBoolean("serviceStart", false));
		portNumberText.setText(settings.getString("portnumber", "4444"));
		
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
//		final ArrayAdapter adapter = new ArrayAdapter(
//	            this, android.R.layout.simple_spinner_item, Border.getNiceNames());
//		for(String bs : Border.getNiceNames()){
//			adapter.add(bs);
//		}
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Border.getNiceNames());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    borderS.setAdapter(adapter);
	    
	    final String target = settings.getString("border", "NONE");
	    int pos = adapter.getCount();
	    CSLog.i(TAG, "---" + target + "---" + pos);
	    CSLog.i(TAG, adapter.getItem(pos - 1).toString());
//	    CSLog.i(TAG, Border.valueOf(adapter.getItem(pos).toString().toUpperCase()).name());
	    while(pos > 0 && !Border.getByNiceName(adapter.getItem(--pos).toString()).name().equals(target)){
	    	//work done in condition
	    	//CSLog.i(TAG, Border.valueOf(adapter.getItem(pos).toString().toUpperCase()).name());
	    }
	    borderS.setSelection(pos);
	    
	    borderS.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				final Border currentB = Border.getByNiceName(adapter.getItem(borderS.getSelectedItemPosition()).toString());
				
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				
				editor.putString("border", currentB.name());
				editor.commit();			
				
				CSLog.i(TAG, "Border: " + currentB.name() + " saved.");	
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//I don't think we need to do anything.
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
