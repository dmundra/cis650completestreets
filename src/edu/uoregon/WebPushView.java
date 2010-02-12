package edu.uoregon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;

//
// this code is base on the code found at
// http://www.androidsnippets.org/snippets/36/
//

public class WebPushView extends Activity{
	 @Override 
     public void onCreate(Bundle icicle) { 
          super.onCreate(icicle); 

//          /* Create a new HTTP-RequestQueue. */ 
//          android.net.http.RequestQueue rQueue = new RequestQueue(this); 
//           
//          /* Prepare the Post-Text we are going to send. */ 
//          String POSTText = null; 
//          try { 
//               POSTText = "mydata=" + URLEncoder.encode("HELLO, ANDROID HTTPPostExample - by anddev.org", "UTF-8"); 
//          } catch (UnsupportedEncodingException e) { 
//               return; 
//          } 
//          /* And put the encoded bytes into an BAIS, 
//           * where a function later can read bytes from. */ 
//          byte[] POSTbytes = POSTText.getBytes(); 
//          ByteArrayInputStream baos = new ByteArrayInputStream(POSTbytes); 
//           
//          /* Create a header-hashmap */ 
//          Map<String, String> headers = new HashMap<String, String>(); 
//          /* and put the Default-Encoding for html-forms to it. */ 
//          headers.put("Content-Type", "application/x-www-form-urlencoded"); 
//           
//          /* Create a new EventHandler defined above, to handle what gets returned. */ 
//          MyEventHandler myEvH = new MyEventHandler(this); 
//
//          /* Now we call a php-file I prepared. It is exactly this: 
//           * <?php 
//           *        echo "POSTed data: '".$_POST['data']."'"; 
//           * ?>*/ 
//          rQueue.queueRequest("http://www.anddev.org/postresponse.php", "POST", 
//                    headers, myEvH, baos, POSTbytes.length,false); 
//
//          /* Wait until the request is complete.*/ 
//          rQueue.waitUntilComplete(); 
     } 
	 
	 
	 public void postData() {  
		    // Create a new HttpClient and Post Header  
		    HttpClient httpclient = new DefaultHttpClient();  
		    HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");  
		  
		    try {  
		        // Add your data  
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		        nameValuePairs.add(new BasicNameValuePair("id", "12345"));  
		        nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));  
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
		  
		        // Execute HTTP Post Request  
		        HttpResponse response = httpclient.execute(httppost);  
		          
		    } catch (ClientProtocolException e) {  
		        // TODO Auto-generated catch block  
		    } catch (IOException e) {  
		        // TODO Auto-generated catch block  
		    }  
		}   
      
     // =========================================================== 
     // Worker Class 
     // =========================================================== 
      
//     private class MyEventHandler implements EventHandler { 
//          private static final int RANDOM_ID = 0x1337; 
//
//          /** Will hold the data returned by the URLCall. */ 
//          ByteArrayBuffer baf = new ByteArrayBuffer(20); 
//           
//          /** Needed, as we want to show the results as Notifications. */ 
//          private Activity myActivity; 
//
//          MyEventHandler(Activity activity) { 
//               this.myActivity = activity;  } 
//
//          public void data(byte[] bytes, int len) { 
//               baf.append(bytes, 0, len);  } 
//
//          public void endData() { 
//               String text = new String(baf.toByteArray()); 
//               myShowNotificationAndLog("Data loaded: \n" + text);  } 
//
//          public void status(int arg0, int arg1, int arg2, String s) { 
//               myShowNotificationAndLog("status [" + s + "]");  } 
//
//          public void error(int i, String s) { 
//               this.myShowNotificationAndLog("error [" + s + "]");  } 
//
//          public void handleSslErrorRequest(int arg0, String arg1, SslCertificate arg2) { } 
//          public void headers(Iterator arg0) { } 
//          public void headers(Headers arg0) { } 
//
//          private void myShowNotificationAndLog(String msg) { 
//               /* Print msg to LogCat and show Notification. */ 
//               Log.d(DEBUG_TAG, msg); 
//               NotificationManager nm = (NotificationManager) this.myActivity 
//                         .getSystemService(Activity.NOTIFICATION_SERVICE); 
//               nm.notifyWithText(RANDOM_ID, msg, NotificationManager.LENGTH_LONG, null); 
//          } 
//     } 
}