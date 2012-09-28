package com.medical.organizer.utilities;


import com.medical.organizer.Main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NotifyReceiver extends BroadcastReceiver {
	private Context con;
	@Override
	public void onReceive(Context context, Intent intent) {
		final Helper help = new Helper(context);
		con = context;
		
		if(intent.hasExtra("schedule_id") && intent.getStringExtra("schedule_id") != null)
		{
			Log.d("RecieverCheck", "Alarm Fired! ");
			String id = intent.getStringExtra("schedule_id");
			final Schedule s = help.getSchedule(id);
				if(s != null)
				{
					Log.d("RecieverCheck", "Schedule ID being fired is: "+id);
					Log.d("RecieverCheck", "Time Set @ "+Schedule.formatDate(s.getTime(), "hh:mm a"));
					
					AlertDialog.Builder build = new AlertDialog.Builder(context);
						if(!s.getType().equals("rounds"))
						{
							String message = "Title: "+s.getTitle()
											 +"\nType: "+s.getType()
											 +"\nStarted: "+Schedule.formatDate(s.getTime(), "hh:mm a")
											 +"\nDismiss or Snooze?";
							build.setMessage(message);
					    	build.setCancelable(false);
							build.setPositiveButton("Snooze", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									help.scheduleAlarm(con, s.getSchedule_id(), s.getRequestCode(), System.currentTimeMillis()+Helper.TWENTY_FIVE_SEC);
								}
							});
							build.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									help.cancelAlarm(con, s.getRequestCode());
									s.setDone(true);
									s.setAlarm(false);
									
									Intent i = new Intent(NotifyReceiver.this.con, Main.class);
									i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									i.putExtra("STARTED_BY_REC", true);
									i.putExtra("type", s.getType());
								
									help.update(s, Helper.CHANGE_ALARM_STATUS);
									
									con.startActivity(i);
									//dialog.cancel();
								}
							});
						}
						else
						{
							String message = "Title: "+s.getTitle()
											 +"\nType: Medical "+Character.toUpperCase(s.getType().charAt(0))+s.getType().substring(1)
											 +"\nEvery: "+Schedule.formatDate(s.getTime(), "hh:mm a")
											 +"\nLocation: "+s.getLocation()
											 +"\nGot it?";
							build.setMessage(message);
					    	build.setCancelable(false);
							build.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
						
							
						}
					AlertDialog alert = build.create();
					alert.show();
				}
				else
				{
					Toast.makeText(context, "Event does not Exist!", Toast.LENGTH_SHORT).show();
				}
		}
		else
		{
			Log.d("RecieverCheck", "Error: Intent extras Empty!");
			Toast.makeText(context, "Receiver Error: Intent extras Empty!", Toast.LENGTH_SHORT).show();
		}
	}

}