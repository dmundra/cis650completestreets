package edu.uoregon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
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
	//this is what we'll use to play audio:
	private final MediaPlayer mp = new MediaPlayer();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordaudioview);
		
		//our geo info
		final GeoStamp geoStamp = new GeoStamp(edu.uoregon.MapTabView.currentLocation);
		
		//for now we'll just use one file at a time:
		final String fileName = "" + geoStamp.getDatabaseID();

		//our buttons:
		final Button backB = (Button) findViewById(R.id.backB);
		final Button recordButton = (Button) findViewById(R.id.recordB);
		
		//these are for switching the text on the record button:
		final String stopRecordingAudio = getString(R.string.audioStopRecording);
		final String startRecordingAudio = getString(R.string.audioStartRecording);
		final Button stopStartB = (Button) findViewById(R.id.playStopB);
		
		backB.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//first let's save our current audio file (if we have one):
				if(new File(getAudioFilePath(fileName)[1]).exists()){
					//something is there:
		        	//TODO: save to db
				}
				
				//now return to our caller
				finish();
			}
		});
		
		recordButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				//start recording if our text is not stop taking audio:
				if(!recordButton.getText().equals(stopRecordingAudio)){
					//for now, we'll only have one audio per stamp
					if(!setUpAudio(fileName)){
						Toast.makeText(RecordAudioView.this, "error with the audio, check with support", Toast.LENGTH_LONG);
					}else{
						recorder.start();
						recordButton.setText(stopRecordingAudio);
					}
				}else{
					//stop recording
					recorder.stop();
	                recorder.reset();
	                
	                //do we want to save right now?
	                
	                recordButton.setText(startRecordingAudio);

	                //now we want to be able to play the audio:   
	                showAudio(fileName, stopStartB);
	                
				}
			}
		});
		
		
		//play/stop button:
		final String stopPlay = getString(R.string.audioStopPlay);
		final String startPlay = getString(R.string.audioStartPlay);
		
		//default text:
		stopStartB.setText(startPlay);
		
		//TODO: get audio out of db and put it in file form
		
		//now we want to be able to play the audio:   
        showAudio(fileName, stopStartB);
		
		stopStartB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//see if we are in play mode:
				if(stopStartB.getText().equals(startPlay)){
					//TODO: then play it:
					try{
						mp.reset();
	                	mp.setDataSource(getAudioFilePath(fileName)[1]);
	                	mp.prepare();
	                	mp.start();
	                	
	                	stopStartB.setText(stopPlay);
	                	
					}catch(Exception e){
						Log.e(TAG, "couldn't play the sound: " + e.toString());
					}
	                
				}else{
					
					try{
						mp.stop();
					}catch(IllegalStateException e){
						//something went wrong with playing it?
						Log.e(TAG, "on stop: " + e.toString());
					}
					
					stopStartB.setText(startPlay);
				}
			}
		});
		
		//this little bit just lets us switch our text to "play" when the audio is done
	    mp.setOnCompletionListener(new OnCompletionListener(){

			@Override
            public void onCompletion(MediaPlayer arg0) {
	            //we should switch over to play mode:
				stopStartB.setText(startPlay);	            
            }
	    	
	    });
	}


	
	
	private static byte[] getAudioFileByes(String fileName) throws FileNotFoundException, IOException{
		return IOUtils.toByteArray(new FileReader(getAudioFilePath(fileName)[1]), "UTF8");
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
	 * just a helper for showing the audio button
	 */
	private void showAudio(String fileName, Button toShow) {
		if(new File(getAudioFilePath(fileName)[1]).exists()){
			//something is there, let's say we can play:
        	toShow.setVisibility(View.VISIBLE);
		}
    }

}

