package edu.uoregon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import edu.uoregon.server.LocationServer;

/**
 * Location service that will start the location server
 * 
 * @author Daniel Mundra
 */
public class LocationSocket extends Service {

	// Used for logging
	private static final String TAG = "LocationSocketLog";

	@Override
	public void onCreate() {
		Log.i(TAG, "Starting Location socket service.");

		new Thread(new LocationServer()).start();
		try {
			Thread.sleep(500);

		} catch (InterruptedException ie) {
			Log.e(TAG, "Interrupted: " + ie.getMessage());
		}

	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Stopping Location socket service.");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
