package com.organizer.medical.activities;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.support.v4.app.NavUtils;

public class PatientActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        
        ImageButton imb = (ImageButton) findViewById(R.id.add_pat);
        
        imb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog d = new Dialog(PatientActivity.this);
				d.setContentView(R.layout.add_patient);
				d.setTitle("Patient Info");
				
				d.show();
			}
		});
    }


    
}
