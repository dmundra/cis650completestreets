package edu.uoregon.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * Server that gets location data
 * 
 * @author Daniel Mundra
 */
public class LocationServer implements Runnable {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	// Used for logging
	private static final String TAG = "LocationServerLog";
	
	// TODO: Just for testing
	public static GeoPoint mapData = new GeoPoint((int) (37.422006 * 1E6),(int) (-122.084095 * 1E6));;
	
	public LocationServer(ServerSocket serv) {
		this.serverSocket = serv;
	}

	@Override
	public void run() {
		try {			
			while (true) {
				Log.d(TAG, "Accepting data.");
				clientSocket = serverSocket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				String inputLine = "";

				while ((inputLine = in.readLine()) != null) {
					Log.d(TAG, "Got data: " + inputLine);
					
					String[] data = inputLine.split(",");
					double lat = Double.parseDouble(data[0]);
					double lon = Double.parseDouble(data[1]);				
										
					mapData = new GeoPoint((int) (lat * 1E6),(int) (lon * 1E6));
					
					Log.d(TAG, "Data as location: " + mapData.toString());
				}

				in.close();
			}			

		} catch (IOException ioe) {
			Log.e(TAG, "Location server IO exception: " + ioe.getMessage());
			mapData = null;
		}
	}
}