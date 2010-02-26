package edu.uoregon;

import java.io.DataOutputStream;
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
import android.widget.EditText;
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
			            	 }else{
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
			
			private void toastMe(String text){
				messageToToast = text;
				handler.sendMessage(new Message());
			}
			
			@SuppressWarnings("deprecation")
            public void run() {

				//we'll leave this at false - meaning that we won't delete anything (as I would like users to double check
				//before we clear the data)
				boolean deleteDatabase = false;
				
				//tells us that the last stage was ok:
				boolean keepWorking = true;

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
					for (byte[] b : db.getRecordings(gId)) {

						toastMe("sending audio: " + (++counter));
						if (postMeData(userId, gId, "audio", b) > -1) {
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
					for (byte[] b : db.getPictures(gId)) {

						toastMe("sending image: " + (++counter));
						if (postMeData(userId, gId, "image", b) > -1) {
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

	private void postMe(List<NameValuePair> nameValuePairs) throws Exception {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();

		final URL url = new URL(URL_G);
		final HttpPost httppost = new HttpPost(url.toURI());

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute HTTP Post Request
		httpclient.execute(httppost);

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

	public int postMeData(long uId, int geoId, String type, byte[] data) {
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
				outWriter.write(data);
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
		return result;
	}

}