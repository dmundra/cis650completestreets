package edu.uoregon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

/**
 * Tab that will display help and guidelines. Also have some setting buttons.
 * 
 * @author Daniel Mundra
 * 
 */
public class HelpTabView extends Activity {
	private IGeoDB db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helptabview);

		Button clearGeoStamps = (Button) findViewById(R.id.clearAllButton);
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

		// Used to clear all geo stamps
		clearGeoStamps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				db = GeoDBConnector.open(getApplicationContext());

				db.deleteAllGeoStamps();
				Log.d("HelpTabView", "Deleted all geo stamps!");

				Toast.makeText(getApplicationContext(), "Geo stampes cleared!",
						Toast.LENGTH_LONG).show();

				db.close();
			}
		});
	}
}
