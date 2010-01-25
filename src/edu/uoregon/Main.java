package edu.uoregon;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Main extends TabActivity {
	
	TabHost mTabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Context ctx = this.getApplicationContext();
        
		//tab 1
		mTabHost = getTabHost();
		TabSpec tabSpec1 = mTabHost.newTabSpec("tab_test1");
		tabSpec1.setIndicator("Map");
		Intent i1 = new Intent(ctx, MapTabView.class);
		tabSpec1.setContent(i1);
		mTabHost.addTab(tabSpec1);
		
		//tab2
		mTabHost = getTabHost();
		TabSpec tabSpec2 = mTabHost.newTabSpec("tab_test1");
		tabSpec2.setIndicator("Record");
		Intent i2 = new Intent(ctx, RecordTabView.class);
		tabSpec2.setContent(i2);
		mTabHost.addTab(tabSpec2);
		
		//tab 3
		mTabHost = getTabHost();
		TabSpec tabSpec3 = mTabHost.newTabSpec("tab_test1");
		tabSpec3.setIndicator("Help");
		Intent i3 = new Intent(ctx, HelpTabView.class);
		tabSpec3.setContent(i3);
		mTabHost.addTab(tabSpec3);
    }
}