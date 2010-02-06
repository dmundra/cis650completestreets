package edu.uoregon;

import java.io.IOException;

import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;

/**
 * Inspired by the API demos.
 */
public class TakePictureView extends Activity {
	private CameraPreview mPreview;
	private IGeoDB db;
	private int geoStampID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set up the db
		db = GeoDBConnector.open(this);

		// Get the geoStampID from the intent
		geoStampID = getIntent().getIntExtra("geoStampID", -1);

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this);
		setContentView(mPreview);
	}

	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	private class CameraPreview extends SurfaceView implements
			SurfaceHolder.Callback, Camera.PictureCallback,
			View.OnClickListener {
		SurfaceHolder mHolder;
		Camera mCamera;

		CameraPreview(Context context) {
			super(context);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			this.setOnClickListener(this);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it
			// where
			// to draw.
			mCamera = Camera.open();
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException exception) {
				mCamera.release();
				mCamera = null;
				// TODO: add more exception handling logic here
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// Surface will be destroyed when we return, so stop the preview.
			// Because the CameraDevice object is not a shared resource, it's
			// very
			// important to release it when the activity is paused.
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// Now that the size is known, set up the camera parameters and
			// begin
			// the preview.
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(w, h);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (geoStampID != -1) {
				db.addPictureToGeoStamp(geoStampID, data);
			}
			finish();
		}

		@Override
		public void onClick(View v) {
			mCamera.takePicture(null, null, this);
		}

	}
}