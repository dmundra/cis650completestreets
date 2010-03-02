package edu.uoregon;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;
import edu.uoregon.log.CSLog;

//
// this code is base on the code found at
// http://www.androidsnippets.org/snippets/36/
//

public class WebPushView extends Activity {
	private static final String TAG = "WebPushView";
	private IGeoDB db;
	private static TextView text;
	private Handler handler;
	private String messageToToast = "";
	private static final String URL_D = "https://www.coglink.com:8080/AndroidGPSTest/Post";
	private static final String URL_G = "https://www.coglink.com:8080/AndroidGPSTest/PostGeo";
	
	private static final int NUM_DATA_RETRIES = 99;
	private boolean stopSendingData = false;

	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.webpushview);

		init();
		
		final String userName = getIntent().getSerializableExtra("userName").toString();

		final Button button = (Button) findViewById(R.id.webpushviewB);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopSendingData = true;
				finish();
			}
		});

		text = (TextView) findViewById(R.id.webpushviewT);
		
		
		 handler = new Handler(new Callback() {
			 
			             public boolean handleMessage(Message msg) {
			            	 
			            	 if(msg.arg1 == 99 ){
			            		 text.setText("done working");
			            	 }else if(msg.arg1 == 98){
			            		 text.setText("something went wrong");
//			            	 }else if(msg.arg1 == 97){
//			            		 text.setText(text.getText() + "\n" + messageToToast);
			            	 }else{
			            		 text.setText(text.getText() + "\n" + messageToToast);
			            		 
			            		 Toast.makeText(WebPushView.this, messageToToast,
								        Toast.LENGTH_SHORT).show();
			            	 }
			
			                 return true;
			             }
			         });
		
		
		

		doThing(userName);

		// text.setText("done working");

	}

	private void doThing(final String userNickName) {

		
		new Thread(new Runnable() {
			
			
			
			
            public void run() {
				
				stopSendingData = false;

				//we'll leave this at false - meaning that we won't delete anything (as I would like users to double check
				//before we clear the data)
				boolean deleteDatabase = false;
				
				// this will be the id we send the server as the user id:
				final long userId = System.currentTimeMillis();

				// now let's send off all of our geo points in one post:

				// Sets up a connection to the database.
				db = GeoDBConnector.open(WebPushView.this);

				final ArrayList<Integer> geoIds = new ArrayList<Integer>();
				final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				int counter = 0;
				for (GeoStamp g : db.getGeoStamps()) {
					nameValuePairs.add(new BasicNameValuePair("geo"
					        + (++counter), userId + "," + g.getDatabaseID()
					        + "," + g.getLatitude() + "," + g.getLongitude()));

					geoIds.add(g.getDatabaseID());
				}
				
				//let's put our nick name in:
				nameValuePairs.add(new BasicNameValuePair("userNickName", userId + "," + userNickName));

				// let's also send our log along:
				nameValuePairs
				        .add(new BasicNameValuePair("log", CSLog.getLog()));
				nameValuePairs.add(new BasicNameValuePair("logUserId", ""
				        + userId));

				
				toastMe("sending geopoints");
				try {
					postMe(nameValuePairs);
					// now delete log:
					//let's not just yet...make the user do it
					//CSLog.saveLog();
					toastMe("geopoints sent");
				} catch (Exception e) {
					final String msg = "something went wrong sending geopoints: "
		                + e.toString();
					toastMe(msg);
					CSLog.i(TAG, msg);
					deleteDatabase = false;
					//we should quit:
					badThing();
					return;
				}

				// send audio:
				counter = 0;
				for (int gId : geoIds) {
					for (String fName : db.getRecordingFilePaths(gId)) {

						toastMe("sending audio: " + (++counter));
						if (postMeData(userId, gId, "audio", fName, NUM_DATA_RETRIES) ) {
							toastMe("audio " + counter + " sent");
						} else {
							toastMe("something went wrong with audio "
					                + counter);
							
							deleteDatabase = false;
							//we should quit:
							badThing();
							return;
						}
					}
				}

				// send images:
				counter = 0;
				for (int gId : geoIds) {
					for (String fName : db.getPictureFilePaths(gId)) {

						toastMe("sending image: " + (++counter));
						if (postMeData(userId, gId, "image", fName, NUM_DATA_RETRIES)) {
							toastMe("image " + counter + " sent");
						} else {
							toastMe("something went wrong with image "
							                + counter);
							deleteDatabase = false;
							//we should quit:
							badThing();
							return;
						}
					}
				}

				if (deleteDatabase) {
					// they all made it, so clear:
					db.recreateTables();
				}
				
				//send message that we are done:
				final Message m = new Message();
				m.arg1 = 99;
				handler.sendMessage(m);
			}

			private void badThing() {
				final Message m = new Message();
				m.arg1 = 98;
				handler.sendMessage(m);
				
			}
			
			
			
		}).start();
		
		

	}
	
	private void toastMe(String text){
		messageToToast = text;
		handler.sendMessage(new Message());
	}

	private void postMe(List<NameValuePair> nameValuePairs) throws Exception {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();

		final URL url = new URL(URL_G);
		final HttpPost httppost = new HttpPost(url.toURI());

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute HTTP Post Request
		final HttpResponse re = httpclient.execute(httppost);
		
		//we know that the server sends us a HttpServletResponse.SC_CREATED (201) if things go well
		if(re.getStatusLine().getStatusCode() != 201){
			throw new Exception("server didn't like us");
		}

		// } catch (Exception e) {
		// String msg = "exception pushing data: " + e.toString();
		// text.setText(msg);
		// CSLog.e(TAG, msg);
		// }

	}

	/** Trust manager that trusts everyone. */
	private static class PromiscuousTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
		        throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
		        throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

	}

	private transient SSLSocketFactory promiscuouSockets;

	private void init() {

		try {
			TrustManager[] myTM = { new PromiscuousTrustManager() };
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, myTM, null);
			promiscuouSockets = ctx.getSocketFactory();
		} catch (Exception ex) {
			CSLog.e(TAG, ex.toString());
		}
	}

	public boolean postMeData(long uId, int geoId, String type, String fName, int timesToTry) {
		
		//see if someone wants us to stop:
		if(stopSendingData){
			return false;
		}
		
		int result = -1;
		try {
			URL url = new URL(URL_D);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setSSLSocketFactory(promiscuouSockets);
			conn.connect();
			try {
				OutputStream out = conn.getOutputStream();
				DataOutputStream outWriter = new DataOutputStream(out);
				outWriter.writeUTF("" + uId);
				outWriter.writeUTF("" + geoId);
				outWriter.writeUTF(type);


				final BufferedInputStream buf = new BufferedInputStream(new FileInputStream(new File(fName)));
				byte[] buffer = new byte[1000];
				for (int i = buf.read(buffer); i >= 0; i = buf.read(buffer)) {
					outWriter.write(buffer, 0, i);
				}
//				outWriter.write(data);
				
				outWriter.close();
				out.close();
				result = conn.getResponseCode();
			} finally {
				conn.disconnect();
			}
		} catch (MalformedURLException ex) {
			CSLog.e(TAG, ex.toString());
			result = -1;
		} catch (IOException ex) {
			CSLog.e(TAG, ex.toString());
			result = -2;

		} catch (Exception e) {
			CSLog.e(TAG, e.toString());
			result = -3;

		}
		
		//we know that the server sends us a HttpServletResponse.SC_CREATED (201) if things go well
		if (result != 201){
			//see if we have any times left to try:
			if(timesToTry > 0){
				toastMe("retrying...");
				return postMeData(uId, geoId, type, fName, --timesToTry);
			}else{
				return false;
			}
		}else{
			return true;
		}
	}

}