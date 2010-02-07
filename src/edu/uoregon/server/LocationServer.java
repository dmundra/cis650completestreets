package edu.uoregon.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

/**
 * Server that gets location data
 * 
 * @author Daniel Mundra
 */
public class LocationServer implements Runnable {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private final int PORTNO = 4444;
	// Used for logging
	private static final String TAG = "LocationServerLog";

	@Override
	public void run() {
		try {
			try {
				Log.d(TAG, "Create server socket.");
				serverSocket = new ServerSocket(PORTNO, 0, InetAddress
						.getLocalHost());
				Log.d(TAG, "IP: " + serverSocket.getInetAddress());
			} catch (IOException e) {
				System.err.println("Could not listen on port: 4444.");
				System.exit(1);
			}

			try {
				Log.d(TAG, "Accepting data.");
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				Log.e(TAG, "Accept failed.");
			}

			Log.e(TAG, "Get data from client.");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String inputLine = "";

			while ((inputLine = in.readLine()) != null) {
				Log.d(TAG, "Got data: " + inputLine);
			}

			in.close();
			clientSocket.close();
			serverSocket.close();

		} catch (IOException ioe) {
			Log.e(TAG, "Location server IO exception.");
		}
	}
}
