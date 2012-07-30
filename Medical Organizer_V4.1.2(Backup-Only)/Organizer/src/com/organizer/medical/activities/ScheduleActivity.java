package com.organizer.medical.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ImageButton;
import android.widget.TimePicker;

public class ScheduleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        
       final CalendarView cw = (CalendarView) findViewById(R.id.schedule_calendar);
        
        cw.setOnDateChangeListener(new OnDateChangeListener() {
			
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				Dialog d = new Dialog(ScheduleActivity.this);
				d.setTitle("Events on "+formatDate(cw.getDate()));
				d.setContentView(R.layout.event_list);
				ImageButton add_button = (ImageButton) d.findViewById(R.id.add_event);
				final Time time = new Time();
					add_button.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Dialog d = new Dialog(ScheduleActivity.this);
							d.setTitle("Set Events");
							d.setContentView(R.layout.add_event);
								
								TimePicker tp = (TimePicker) d.findViewById(R.id.timePicker1);
								time.hour = tp.getCurrentHour();
								time.minute = tp.getCurrentMinute();
								time.toMillis(false);
								
							
							d.show();
						}
					});
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

    public String formatDate(long millisec)
    {
    	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(millisec);
    	return format.format(cal.getTime());
    }
    
}
