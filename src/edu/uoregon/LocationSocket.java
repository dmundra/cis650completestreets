/**
 * 
 */
package edu.uoregon;

import java.io.IOException;
import java.net.ServerSocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Location sockets will be a server that gets location changes through sockets
 * 
 * @author Daniel Mundra
 */
public class LocationSocket extends Service{

	private ServerSocket serverSocket;
	private final int PORTNO = 4444;
	// Used for logging
	private static final String TAG = "LocationSocketLog";

	@Override
	public void onCreate() {
		Log.i(TAG, "Starting Location socket service.");

		try {
			Log.i(TAG, "Create server socket.");
			serverSocket = new ServerSocket(PORTNO);
			Log.i(TAG, "IP: " + serverSocket.getInetAddress());
		} catch (IOException e) {
			Log.e(TAG, "Could not listen on port: " + PORTNO + ".");
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Stopping Location socket service.");
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
