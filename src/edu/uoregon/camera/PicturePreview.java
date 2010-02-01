package edu.uoregon.camera;

import java.io.IOException;

import edu.uoregon.db.GeoDBConnector;
import edu.uoregon.db.IGeoDB;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PicturePreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {
    SurfaceHolder mHolder;
    Camera mCamera;
    IGeoDB db;
    int geoStampID;

    public PicturePreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        db = GeoDBConnector.open(context);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
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
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		db.addPictureToGeoStamp(geoStampID, data);
	}
	
	public void takePicture(int geoStampID) {
		this.geoStampID = geoStampID;
		mCamera.takePicture(null, null, this);
	}
}