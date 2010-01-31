package edu.uoregon;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.TabHost;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * This class will be used to overlay icons on the map view
 * 
 * @author Daniel Mundra
 */
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private final List<OverlayItem> items;
	private final Drawable marker;
	
	//TODO: Replace this with id
	private final GeoStamp stamp;

	/**
	 * Constructor to overlay icon on map
	 * 
	 * @param defaultMarker
	 */
	public MyItemizedOverlay(Drawable defaultMarker, GeoStamp location) {
		super(defaultMarker);
		items = new ArrayList<OverlayItem>();
		marker = defaultMarker;
		stamp = location;
	}

	@Override
	protected OverlayItem createItem(int index) {
		return (OverlayItem) items.get(index);
	}

	@Override
	public int size() {
		return items.size();

	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}

	/**
	 * Return GeoStamp
	 * @return - GeoStamp
	 */
	public GeoStamp getStamp() {
		return stamp;
	}

	/**
	 * Adds overlay item to the map
	 * 
	 * @param item
	 *            - OverlayItem
	 */
	public void addItem(OverlayItem item) {
		items.add(item);
		populate();
	}

	@Override
	protected boolean onTap(int i) {
		// Load the record tab
		TabHost tabHost = edu.uoregon.Main.mTabHost;
		tabHost.setCurrentTab(1);
		
		// TODO: Pass stamp info to record tab view

		return true;
	}
}
