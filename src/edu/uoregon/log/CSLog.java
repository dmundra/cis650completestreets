package edu.uoregon.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.os.Environment;
import android.util.Log;

/**
 * Class for logging events and information
 */
public class CSLog {
	private static ConcurrentLinkedQueue<String> logList = new ConcurrentLinkedQueue<String>();
	public static final String logFilePath = Environment.getExternalStorageDirectory() + "/CompleteStreets/logs/";
	
	public static void i(String tag, String msg) {
		Log.i(tag, msg);
		String log = "I:" + createEntry(tag, msg);
		logList.add(log);
	}
	
	public static void e(String tag, String msg) {
		Log.e(tag, msg);
		String log = "E:" + createEntry(tag, msg);
		logList.add(log);
	}
	
	public static void d(String tag, String msg) {
		Log.d(tag, msg);
		String log = "D:" + createEntry(tag, msg);
		logList.add(log);
	}
	
	private static String createEntry(String tag, String msg) {
		String entry = getDate();
		entry += "\t" + tag + "\t" + msg;
		return entry;
	}
	
	public static void saveLog() {
		File logDir = new File(logFilePath);
		if(!logDir.exists()){
			if(!logDir.mkdirs()){
				CSLog.e("CSLog", "Can't make directory: " + logDir.getAbsolutePath());
			}
		}
		String fileName = logDir.getAbsolutePath() + "/" + getDate() + ".log";
		try {
			PrintStream out = new PrintStream(fileName);
			for (String s : logList) {
				out.println(s);
			}
			out.close();
			logList.clear();
		}
		catch (FileNotFoundException e) {
			CSLog.e("CSLog", e.getMessage());
		}
	}
	
	private static String getDate() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "-" +
		c.get(Calendar.MONTH) + "-" +
		c.get(Calendar.DAY_OF_MONTH) + "_" +
		c.get(Calendar.HOUR_OF_DAY) + 
		c.get(Calendar.MINUTE) + 
		c.get(Calendar.SECOND);		
	}
}
