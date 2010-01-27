package edu.uoregon;

import android.app.Activity;
import android.os.Bundle;
/**
 * Tab that will display help and guidelines.
 * @author Daniel Mundra
 *
 */
public class HelpTabView extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helptabview);
    }

}
