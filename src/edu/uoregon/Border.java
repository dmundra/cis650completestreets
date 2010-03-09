package edu.uoregon;

import edu.uoregon.awt.Polygon;

public enum Border {

	NONE(0, 0, 0, 0, 0, 0, 0, 0, "None"), 
	EUGENE((int) (44.0476665390621 * 1E6), (int) (-123.0911260843277 * 1E6), /*11th & Oak*/
			(int) (44.05104985243286 * 1E6),(int) (-123.09113949537277 * 1E6),/*Oak & 8th*/ 
			(int) (44.051057563467644 * 1E6),(int) (-123.09585750102997 * 1E6),/*8th & Charnelton*/ 
			(int) (44.047660755454984 * 1E6),(int) (-123.09583336114883 * 1E6),/*Charnelton & 11th*/ 
	        "Eugene"), 
	SAN_FRAN((int) (37.78974716920605 * 1E6),
	        (int) (-122.41350889205933 * 1E6), (int) (37.78657411876266 * 1E6),
	        (int) (-122.41282224655151 * 1E6), (int) (37.78588081726776 * 1E6),
	        (int) (-122.4182939529419 * 1E6), (int) (37.78903891847674 * 1E6),
	        (int) (-122.41889476776123 * 1E6), "San Fran");

	public final String niceName;
	private final Polygon p;

	Border(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4,
	        String niceName) {

		this.niceName = niceName;

		p = new Polygon(new int[] { x1, x2, x3, x4 }, new int[] { y1, y2, y3,
		        y4 });

	}

	public static String[] getNiceNames() {
		final String[] names = new String[Border.values().length];

		for (int i = 0; i < Border.values().length; ++i) {
			names[i] = Border.values()[i].niceName;
		}
		return names;
	}

	public static Border getByNiceName(String niceName) {
		for (Border b : Border.values()) {
			if (niceName.equals(b.niceName)) {
				return b;
			}
		}

		return NONE;
	}

	public boolean contains(final double x, final double y){
		return p.contains((int)(x), (int)(y));
			
	}
}
