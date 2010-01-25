package edu.uoregon;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class MapTabView extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maptabview);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
