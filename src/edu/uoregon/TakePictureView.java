package edu.uoregon;

import edu.uoregon.camera.PicturePreview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Inspired by the API demos.
 */
public class TakePictureView extends Activity {
    private PicturePreview picturePreview;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Create our Preview view and set it as the content of our activity.
        setContentView(R.layout.takepictureview);
        picturePreview = (PicturePreview)findViewById(R.id.picturePreview);
        
		final Button takePictureButton = (Button) findViewById(R.id.takePictureButton);

		takePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				picturePreview.takePicture(getIntent().getIntExtra("geoStampID", -1));
			}
		});
    }
}
