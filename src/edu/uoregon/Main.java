package edu.uoregon;

import edu.uoregon.log.CSLog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * Class that manages the tabs in the app
 * @author Daniel Mundra
 *
 */
public class Main extends TabActivity {
	
	// Used to manage tabs
	public static TabHost mTabHost;
	// Used for logging
	private static final String TAG = "MainLog";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        CSLog.i(TAG, "Complete Streets Application started.");
        
		// Create and add Map Tab View
		mTabHost = getTabHost();
		TabSpec tabSpec1 = mTabHost.newTabSpec("tab_test1");
		tabSpec1.setIndicator("Map");
		Intent i1 = new Intent().setClassName("edu.uoregon", "edu.uoregon.MapTabView");
		tabSpec1.setContent(i1);
		mTabHost.addTab(tabSpec1);		

        CSLog.i(TAG, "Map tab created.");
		
		// Create and add Record Tab View
        /*
		mTabHost = getTabHost();
		TabSpec tabSpec2 = mTabHost.newTabSpec("tab_test1");
		tabSpec2.setIndicator("Record");
		Intent i2 = new Intent().setClassName("edu.uoregon", "edu.uoregon.RecordTabView");
		tabSpec2.setContent(i2);
		mTabHost.addTab(tabSpec2);		

        CSLog.i(TAG, "Record tab created.");
        */
		
		// Create and add Help Tab View
		mTabHost = getTabHost();
		TabSpec tabSpec3 = mTabHost.newTabSpec("tab_test1");
		tabSpec3.setIndicator("Help");
		Intent i3 = new Intent().setClassName("edu.uoregon", "edu.uoregon.HelpTabView");
		tabSpec3.setContent(i3);
		mTabHost.addTab(tabSpec3);
		
        CSLog.i(TAG, "Help tab created.");
		
		// Create and add Settings Tab View
		mTabHost = getTabHost();
		TabSpec tabSpec4 = mTabHost.newTabSpec("tab_test1");
		tabSpec4.setIndicator("Settings");
		Intent i4 = new Intent().setClassName("edu.uoregon", "edu.uoregon.SettingTabView");
		tabSpec4.setContent(i4);
		mTabHost.addTab(tabSpec4);
		
        CSLog.i(TAG, "Setting tab created.");
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        CSLog.i(TAG, "Complete Streets Application closed.");
    	CSLog.saveLog();
    }
}