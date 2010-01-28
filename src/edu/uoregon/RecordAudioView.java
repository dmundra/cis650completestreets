package edu.uoregon;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordAudioView extends Activity{
	
	//just for logging:
	private static final String TAG = "RecordTabView";
	//this is what we'll use to do our current recording:
	private final MediaRecorder recorder = new MediaRecorder();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordaudioview);
		
		//sets up our audio
		if(!setUpAudio()){
			//something went wrong...
			finish();
		}

		//our buttons:
		final Button backB = (Button) findViewById(R.id.backB);
		final Button recordButton = (Button) findViewById(R.id.recordB);
		
		//these are for switching the text on the record button:
		final String stopRecordingAudio = getString(R.string.audioStopRecording);
		final String startRecordingAudio = getString(R.string.audioStartRecording);
		
		
		backB.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//just return to our caller
				finish();
			}
		});
		
		recordButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//start recording if our text is not stop taking audio:
				if(!recordButton.getText().equals(stopRecordingAudio)){
					recorder.start();
			        
					recordButton.setText(stopRecordingAudio);
				}else{
					//stop recording
					recorder.stop();
	                recorder.release();
	                
					recordButton.setText(startRecordingAudio);
					//TODO: show 'play' button if we successfully recorded something
				}
			}
		});
		
		
	}

	private boolean setUpAudio() {
		ContentValues values = new ContentValues(3);

        final String rand = "" + (new Random().nextInt(10000));
        File sampleDir = Environment.getExternalStorageDirectory();
        final String path;
        try 
        { 
           path = File.createTempFile("AudioTest_" + rand, ".3gp", sampleDir).getAbsolutePath();
        }
        catch (IOException e) 
        {
            Log.e(TAG,"sdcard access error: " + e.toString());
            return false;
        }
        
        values.put(MediaStore.MediaColumns.TITLE, "my title " + rand);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(path);
        
        try{
    		recorder.prepare();	
        }catch(Exception e){
        	final String log = "Exception preparing record: " + e.toString();
        	Log.d(TAG, log);
        	//Toast.makeText(this, log, Toast.LENGTH_LONG);
        	return false;
        }
        
        //if we get here we hope that we can go:
        return true;
    }

}

//protected void saveFileText() {
//    FileOutputStream fos;
//    DataOutputStream dos;
//    
//    try {          
//         
//      boolean success = new File("/data/data/PACKAGE_NAME/files/subdir").mkdir();
//
//      if (!success) {
//          Log.i(this.toString(), "no success");
//      }
//      File file =  new File("/data/data/PACKAGE_NAME/files/subdir/MyFile.txt");
//      file.createNewFile();
//      if(!file.exists()){
//         file.createNewFile();
//         Log.i(this.toString(), "File created...");
//      }
//      fos = new FileOutputStream(file);
//      dos=new DataOutputStream(fos);
//      dos.writeChars("helloworld...");      
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//} 
