package org.lskk.shesop.steppy;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import org.lskk.teamshesop.steppy.R;

public class MapsActivity extends Activity {
	WebView browser; 
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle maps) { 
        super.onCreate(maps); 
        setContentView(R.layout.activity_map); 
        browser=(WebView) findViewById(R.id.action_maps); 
        browser.loadUrl("https://www.google.co.id/maps"); 
        // alamat url yang akan dibuka 
    }
}
