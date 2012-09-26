package com.medical.organizer.utilities;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class NotifyRecieverRepeating extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.hasExtra("schedule") && intent.hasExtra("patients"))	
		{
			Log.d("AlarmCHeck", "Alarm is Fired! all extras are not Null!");
			String serializedSchedule = intent.getStringExtra("schedule");
			String serializedPatient = intent.getStringExtra("patients");
			Patients pat = null;
			Schedule sched = null;
					try {
						pat = (Patients) Serializer.deserialize(serializedPatient);
						sched = (Schedule) Serializer.deserialize(serializedSchedule);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			final Patients p = pat;
			final Schedule s = sched;
			Log.d("ItemCHeck", "Recieved Type: "+s.getType());
			Log.d("ItemCHeck", "Recieved Time: "+Schedule.formatDate(s.getTime(), "hh:mm a"));
			AlertDialog.Builder build = new AlertDialog.Builder(context);
			
			String message = "Title: "+s.getTitle()
							 +"\nType: Medical "+Character.toUpperCase(s.getType().charAt(0))+s.getType().substring(1)
							 +"\nStarted: "+Schedule.formatDate(s.getTime(), "hh:mm a")
							 +"\nLocation: "+sched.getLocation()
							 +"\nGot it?";
			build.setMessage(message);
	    	build.setCancelable(false);
			build.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		
			AlertDialog alert = build.create();
			alert.show();
		}
	}

}
