package edu.uoregon.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.location.Location;
import android.util.Log;

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
	
	public static Location mapData = null;
	
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
										
					mapData = new Location(edu.uoregon.MapTabView.currentLocation);
					
					mapData.setLatitude(lat);
					mapData.setLongitude(lon);
					
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
