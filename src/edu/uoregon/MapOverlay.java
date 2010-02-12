/**
 * 
 */
package edu.uoregon;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * @author Daniel Mundra
 */
public class MapOverlay extends ItemizedOverlay<OverlayItem> {
	private final List<OverlayItem> items;

	public MapOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		boundCenterBottom(defaultMarker);
		items = new ArrayList<OverlayItem>();
	}

	public void addItem(OverlayItem item) {
		items.add(item);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return (items.get(i));
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

	@Override
	public int size() {
		return items.size();
	}
}
