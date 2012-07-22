package com.organizer.medical.activities;

import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.support.v4.app.NavUtils;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        Button button = (Button) findViewById(R.id.submit);
        
        button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setContentView(R.layout.main);
				
				TabHost tabs=(TabHost)findViewById(R.id.tabhost);
				
				tabs.setup();
				
				TabHost.TabSpec spec=tabs.newTabSpec("tag1");
				
				spec.setContent(R.id.Schedule);
				spec.setIndicator("Schedule");
				tabs.addTab(spec);
			
				spec=tabs.newTabSpec("tag2");
				spec.setContent(R.id.Patients);
				spec.setIndicator("Patients");
				tabs.addTab(spec);	
				
				spec=tabs.newTabSpec("tag3");
				spec.setContent(R.id.Contacts);
				spec.setIndicator("Contacts");
				tabs.addTab(spec);
				
			}
		});
        
    }

 

    
}
