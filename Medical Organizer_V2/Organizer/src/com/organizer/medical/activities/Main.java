package com.organizer.medical.activities;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class Main extends android.app.TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

  
				// TODO Auto-generated method stub
				setContentView(R.layout.main);
				
				TabHost tabHost = getTabHost();
		        TabHost.TabSpec sp;
		        Intent in;
		        
		        in = new Intent().setClass(Main.this, ScheduleActivity.class);
		        sp = tabHost.newTabSpec("Tab1").setIndicator("Schedule").setContent(in);
		        tabHost.addTab(sp);
		        
		        in = new Intent().setClass(Main.this, PatientActivity.class);
		        sp = tabHost.newTabSpec("Tab2").setIndicator("Patient").setContent(in);
		        tabHost.addTab(sp);
		        
		        in = new Intent().setClass(Main.this, ContactActivity.class);
		        sp = tabHost.newTabSpec("Tab3").setIndicator("Contact").setContent(in);
		        tabHost.addTab(sp);
		        
		        tabHost.setCurrentTab(0);	
    }
}
