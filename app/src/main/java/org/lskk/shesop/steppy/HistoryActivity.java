package org.lskk.shesop.steppy;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import org.lskk.teamshesop.steppy.R;

public class HistoryActivity extends Activity {
	WebView browser; 
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle historystep) { 
        super.onCreate(historystep); 
        setContentView(R.layout.activity_history); 
        browser=(WebView) findViewById(R.id.webview); 
        browser.loadUrl("http://id.shesop.org/steppy"); 
        // alamat url yang akan dibuka 
    }
}