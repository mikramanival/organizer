package com.organizer.medical.activities;

import android.os.Bundle;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class Main extends android.app.TabActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    			MedicalHelper dbHelper = new MedicalHelper(this);
    			
    			try {
    				dbHelper.createDataBase();
    				dbHelper.close();
				} catch (Exception e) {
					throw new Error("Unable to create database");
				}
    				

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
    
    public void initializeDatabase(){
    	Log.d("dbcheck", "Creating Database!...");
    	
    	SQLiteDatabase connect = openOrCreateDatabase("MedicalOrganizer", MODE_PRIVATE, null);
    	
    	Log.d("dbcheck", "Database Created!...");
    	Log.d("dbcheck", "Creating Table!...");
    	
    	String createPatientTable = "CREATE TABLE IF NOT EXISTS Patients(firstname VARCHAR,middleinitial VARCHAR,lastname VARCHAR,address VARCHAR,age int(3),med_history VARCHAR,status VARCHAR);";
    	connect.rawQuery(createPatientTable, null);
    	
    	Log.d("dbcheck", "Database Table Created!...");
    	connect.close();
    }
}
