package com.medical.organizer.utilities;

import java.io.IOException;

import com.medical.organizer.Main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class NotifyReciever extends BroadcastReceiver {
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		final Helper help = new Helper(context);
		this.context = context;
		if(intent.hasExtra("schedule") && intent.getStringExtra("schedule") != null)
		{
			String serializedObject = intent.getStringExtra("schedule");
			
				Schedule sched=null;
				try {
					sched = (Schedule) Serializer.deserialize(serializedObject);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(sched!=null)
				{	
					final Schedule s = sched;
					Log.d("ItemCHeck", "Recieved Time: "+Schedule.formatDate(s.getTime(), "hh:mm a"));
					AlertDialog.Builder build = new AlertDialog.Builder(context);
					String message = "Title: "+s.getTitle()
									 +"\nType: "+s.getType()
									 +"\nStarted: "+Schedule.formatDate(s.getTime(), "hh:mm a")
									 +"\nDismiss or Snooze?";
					build.setMessage(message);
			    	build.setCancelable(false);
					build.setPositiveButton("Snooze", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							NotifyReciever.this.context.unregisterReceiver(NotifyReciever.this);
							//Helper.scheduleAlarm(NotifyReciever.this.context, s, s.getRequestCode(), System.currentTimeMillis()+Helper.TWENTY_FIVE_SEC);
						    
						}
					});
					build.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
						
						//	Helper.cancelAlarm(NotifyReciever.this.context, s.getRequestCode());
							s.setDone(true);
							s.setAlarm(false);
							
							Intent i = new Intent(NotifyReciever.this.context, Main.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							i.putExtra("STARTED_BY_REC", true);
							i.putExtra("type", s.getType());
						
							help.update(s, Helper.CHANGE_ALARM_STATUS);
							
							NotifyReciever.this.context.startActivity(i);
							//dialog.cancel();
						}
					});
					
					AlertDialog alert = build.create();
					alert.show();
				}
		}
	}

}
