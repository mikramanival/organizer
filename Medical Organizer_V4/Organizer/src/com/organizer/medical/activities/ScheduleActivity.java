package com.organizer.medical.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

public class ScheduleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        
        CalendarView cw = (CalendarView) findViewById(R.id.schedule_calendar);
        
        cw.setOnDateChangeListener(new OnDateChangeListener() {
			
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				Dialog d = new Dialog(ScheduleActivity.this);
				
				d.setTitle("Events on This Data!");
				
				d.show();
				
				//Toast toast = Toast.makeText(ScheduleActivity.this, "This is what u clicked",1500);
				//toast.show();
				
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_schedule, menu);
        return true;
    }

    
}
