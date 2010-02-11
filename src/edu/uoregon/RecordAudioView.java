package edu.uoregon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

public class RecordAudioView extends Activity {

	// just for logging:
	private static final String TAG = "RecordAudioViewLog";
	// this is what we'll use to do our current recording:
	private final MediaRecorder recorder = new MediaRecorder();
	// this is what we'll use to play audio:
	private final MediaPlayer mp = new MediaPlayer();
	// this is our working directory for audio:
	private static final String audioDir = IGeoDB.audioFilePath;

	// DB
	private IGeoDB con;

	private String convertAudioToFile(String fileName, byte[] audio) {

		//see if our directory exists:
		final File audioD = new File(audioDir); 
		if(!audioD.exists()){
			if(!audioD.mkdirs()){
				Log.d(TAG, "can't make directory: " + audioD.getAbsolutePath() + " (exiting activity)");
				finish();
			}
		}
		
		// first clean out the old files:
		for (File f : audioD.listFiles()) {
			if (!f.delete()) {
				// something went wrong...
				final String log = "Something went wrong, we couldn't delete the file: "
				        + f.getAbsolutePath();
				Log.d(TAG, log);
			}
		}

		if (audio != null && audio.length > 0) {
			File f = new File(getAudioFilePath(fileName));

			FileOutputStream fos = null;
			final String log = "Something went wrong writing the file ";
			try {
				fos = new FileOutputStream(f);
				IOUtils.write(audio, fos);
			} catch (FileNotFoundException e) {
				Log.d(TAG, log + e.toString());
			} catch (IOException e) {
				Log.d(TAG, log + e.toString());
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}

		// just return the file name we were given
		return fileName;

	}

	private static byte[] convertFileToAudio(String fileName) {
		byte[] ret = new byte[0];

		final String log = "Something went wrong writing the file ";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(getAudioFilePath(fileName));
			ret = IOUtils.toByteArray(fis);
		} catch (FileNotFoundException e) {
			Log.d(TAG, log + e.toString());
		} catch (IOException e) {
			Log.d(TAG, log + e.toString());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		return ret;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordaudioview);

		final int geoId = (Integer) getIntent().getSerializableExtra("geoId");
		//Log.d(TAG, "geoId is: " + geoId);

		// TODO close connection at some point...
		con = GeoDBConnector.open(this);
		final List<byte[]> rec = con.getRecordings(geoId);
		//Log.d(TAG, "size of rec: " + rec.size());
		// for now we'll just use one audio per location:
		final String fileName = convertAudioToFile("" + geoId,
		        rec.size() > 0 ? rec.get(0) : null);

		// our buttons:
		final Button backB = (Button) findViewById(R.id.backB);
		final Button recordButton = (Button) findViewById(R.id.recordB);

		// these are for switching the text on the record button:
		final String stopRecordingAudio = getString(R.string.audioStopRecording);
		final String startRecordingAudio = getString(R.string.audioStartRecording);
		final Button stopStartB = (Button) findViewById(R.id.playStopB);

		backB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// return to our calling activity
				finish();
			}
		});

		recordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// start recording if our text is not stop taking audio:
				if (!recordButton.getText().equals(stopRecordingAudio)) {
					// for now, we'll only have one audio per stamp
					if (!setUpAudio(fileName)) {
						Toast.makeText(RecordAudioView.this,
						        "error with the audio, check with support",
						        Toast.LENGTH_LONG);
					} else {
						//hide play/pause:
						stopStartB.setVisibility(View.INVISIBLE);
						
						
						//now record
						recorder.start();
						recordButton.setText(stopRecordingAudio);
					}
				} else {
					// stop recording
					recorder.stop();
					recorder.reset();

					recordButton.setText(startRecordingAudio);

					// let's do a save:
					if(con.addRecordingToGeoStamp(geoId, convertFileToAudio(fileName))){
						Log.d(TAG, 
								"Didn't save the audio as expected... geoId: "
								+ geoId);
					}
					
					// If saved successfully make this true to display a checkmark
					// TODO: Need to check if saved in db
					// David: Static views are evil. I have restructured the code in RecordTabView
					// so this call should be unnecessary.
//					edu.uoregon.RecordTabView.recordCheck
//							.setVisibility(View.VISIBLE);

					// now we want to be able to play the audio:
					showAudio(fileName, stopStartB);

				}
			}

			
		});

		// play/stop button:
		final String stopPlay = getString(R.string.audioStopPlay);
		final String startPlay = getString(R.string.audioStartPlay);

		// default text:
		stopStartB.setText(startPlay);

		// TODO: get audio out of db and put it in file form

		// now we want to be able to play the audio:
		showAudio(fileName, stopStartB);

		stopStartB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// see if we are in play mode:
				if (stopStartB.getText().equals(startPlay)) {
					// TODO: then play it:
					try {
						// let's hide our record button:
						recordButton.setVisibility(View.INVISIBLE);
						
						mp.reset();
						mp.setDataSource(getAudioFilePath(fileName));
						mp.prepare();
						mp.start();

						stopStartB.setText(stopPlay);

					} catch (Exception e) {
						Log.e(TAG, "couldn't play the sound: " + e.toString());
					}

				} else {

					try {
						mp.stop();
					} catch (IllegalStateException e) {
						// something went wrong with playing it?
						Log.e(TAG, "on stop: " + e.toString());
					}

					stopStartB.setText(startPlay);
					
					// let's put our record button back on the map:
					recordButton.setVisibility(View.VISIBLE);
				}
			}
		});

		// this little bit just lets us switch our text to "play" when the audio
		// is done
		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				// we should switch over to play mode:
				stopStartB.setText(startPlay);
				
				// let's put our record button back on the map:
				recordButton.setVisibility(View.VISIBLE);
			}

		});
	}

	@Override
	protected void onDestroy() {
		con.close();
		super.onDestroy();
	}
	
	

	/**
	 * this is a helper for getting back the file object for the audio recording
	 * we have
	 */
	private static String getAudioFilePath(String fileName) {

		return audioDir + fileName + ".3gp";
	}

	/**
	 * sets up our recorder, not sure if this is a good way to do it...
	 * 
	 * @return false if we know something went wrong
	 */
	private boolean setUpAudio(String fileName) {
		ContentValues values = new ContentValues(3);

		final String path = getAudioFilePath(fileName);
		try {
			File dir = new File(audioDir);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new IOException("couldn't make directory? "
				        + dir.getAbsolutePath());
			}

		} catch (IOException e) {
			Log.e(TAG, "sdcard access error: " + e.toString());
			return false;
		}

		values.put(MediaStore.MediaColumns.TITLE, "my title " + fileName);
		values.put(MediaStore.MediaColumns.DATE_ADDED, System
		        .currentTimeMillis() / 1000);

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(path);

		try {
			recorder.prepare();
		} catch (Exception e) {
			final String log = "Exception preparing record: " + e.toString();
			Log.d(TAG, log);
			// Toast.makeText(this, log, Toast.LENGTH_LONG);
			return false;
		}

		// if we get here we hope that we can go:
		return true;
	}

	/**
	 * just a helper for showing the audio button
	 */
	private void showAudio(String fileName, Button toShow) {
		if (new File(getAudioFilePath(fileName)).exists()) {
			// something is there, let's say we can play:
			toShow.setVisibility(View.VISIBLE);
		}
	}

}
