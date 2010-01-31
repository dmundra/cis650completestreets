package edu.uoregon;

import java.io.File;
import java.io.IOException;
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
import android.widget.Toast;

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
		
		//our geo info
		final GeoStamp geoStamp = new GeoStamp(edu.uoregon.MapTabView.currentLocation);
		

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
					//for now, we'll only have one audio per stamp
					if(!setUpAudio("" + geoStamp.getDatabaseID())){
						Toast.makeText(RecordAudioView.this, "error with the audio, check with support", Toast.LENGTH_LONG);
					}else{
						recorder.start();
						recordButton.setText(stopRecordingAudio);
					}
				}else{
					//stop recording
					recorder.stop();
	                recorder.reset();
	                
	                recordButton.setText(startRecordingAudio);
					//TODO: show 'play' button if we successfully recorded something
				}
			}
		});
		
		
	}


	/**
     * sets up our recorder, not sure if this is a good way to do it...
     * @return false if we know something went wrong
     */
	private boolean setUpAudio(String fileName) {
		ContentValues values = new ContentValues(3);

        final String path;
        try 
        { 
        	final String[] paths = getAudioFilePath(fileName);
        	File dir = new File(paths[0]);
        	path = paths[1]; 
        	if(!dir.exists() && !dir.mkdirs()){
        		throw new IOException("couldn't make directory? " + dir.getAbsolutePath());
        	}
        	
        }
        catch (IOException e) 
        {
            Log.e(TAG,"sdcard access error: " + e.toString());
            return false;
        }
        
        values.put(MediaStore.MediaColumns.TITLE, "my title " + fileName);
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
    
	/**
	 * this is a helper for getting back the file object for the audio recording we have
	 */
	private static String[] getAudioFilePath(String fileName){
		final String[] ret = new String[2];
		
		ret[0] = Environment.getExternalStorageDirectory() + "/CompleteStreets/";
		ret[1] = ret[0] + fileName + ".3gp";
		
		return ret;
	}

}

