package edu.uoregon;

import java.io.IOException;
import java.net.ServerSocket;

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

	private ServerSocket serverSocket;
	private final int PORTNO = 4444;

	// Used for logging
	private static final String TAG = "LocationSocketLog";

	@Override
	public void onCreate() {
		try {
			Log.i(TAG, "Starting Location socket service.");

			serverSocket = new ServerSocket(PORTNO);

			new Thread(new LocationServer(serverSocket)).start();
			try {
				Thread.sleep(500);

			} catch (InterruptedException ie) {
				Log.e(TAG, "Interrupted: " + ie.getMessage());
			}
		} catch (IOException e) {
			Log.e(TAG, "IO Exception: " + e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		try {
			Log.i(TAG, "Stopping Location socket service.");
			serverSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "IO Exception: " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
