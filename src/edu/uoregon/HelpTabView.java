package edu.uoregon;

import edu.uoregon.log.CSLog;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helptabview);

		CSLog.i(TAG, "Help view started.");

		TextView helpText = (TextView) findViewById(R.id.helpText);

		// Load help/guidelines text
		helpText.setTextSize(15);
		helpText
				.setText("Guidelines: \n"
						+ "* Explore the 9 square block area. \n\n"
						+ "* In each linear block, please find and note an establishment "
						+ "that sells and/or serves food (e.g. restaurants, cafes, markets). \n\n"
						+ "* If there are no such establishments on a block, please note that lack. "
						+ "To make a note, please take a picture of the building or a representative sign or record an "
						+ "audio clip describing the establishment (e.g. name, description of the sign, "
						+ "statement of how it was identified as selling/serving food). \n\n"
						+ "* During the period of the trial, please take at least one picture and make at least one audio note. \n\n"
						+ "[Notes on the application (e.g. “The camera is accessed on the record tab”).]");
	}

	@Override
	protected void onDestroy() {
		CSLog.i(TAG, "Help view closed.");
		super.onDestroy();
	}
}
