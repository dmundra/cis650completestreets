package edu.uoregon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

		Log.i(TAG, "Help view started.");

		TextView helpText = (TextView) findViewById(R.id.helpText);

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
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "Help view closed.");
		super.onDestroy();
	}
}
