package edu.uoregon;

import java.util.ArrayList;

public enum Border {

	NONE(0.0, 0.0, 0.0, 0.0, "None"),
	EUGENE(32.3214, -123.322, 32.123, -123.322, "Eugene"),
	SAN_FRAN(37.7889544, -122.418728, 37.786699, -122.41309583, "San Fran");

	public final double top;
	public final double left;
	public final double right;
	public final double bottom;
	public final String niceName;

	Border(double top, double left, double bottom, double right, String niceName) {
		this.top = top;
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.niceName = niceName;
	}
	
	public static String[] getNiceNames(){
		final String[] names = new String[Border.values().length];
		//final ArrayList<String> names = new ArrayList<String>();
//		for(Border b : Border.values()){
//			names.add(b.niceName);
//		}
		for(int i = 0; i < Border.values().length; ++i){
			names[i] = Border.values()[i].niceName;
		}
		return names;
	}
	
	public static Border getByNiceName(String niceName){
		for(Border b : Border.values()){
			if(niceName.equals(b.niceName)){
				return b;
			}
		}
		
		return NONE;
	}

}
