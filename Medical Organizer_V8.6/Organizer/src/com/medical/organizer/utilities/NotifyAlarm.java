package com.medical.organizer.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotifyAlarm {
	public static Context CONTEXT;
	public static Intent INTENT;
	public static long TRIGGER_TIME;
	
	
	public static void startAlarm(int requestCode)
	{
		AlarmManager am = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(CONTEXT, requestCode, INTENT, 0);
		am.set(AlarmManager.RTC_WAKEUP, TRIGGER_TIME, pendingIntent);
		Log.d("AlarmCheck", "Alarm Started @ "+Schedule.formatDate(TRIGGER_TIME, "hh:mm a"));
	}
	
	
	public static void startAlarmRepeating(int requestCode, long timeInterval)
	{
		AlarmManager am = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(CONTEXT, requestCode, INTENT, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, TRIGGER_TIME,timeInterval, pendingIntent);
		Log.d("AlarmCheck", "Alarm Started @ "+Schedule.formatDate(TRIGGER_TIME, "hh:mm a")+", Interval of: "+(timeInterval/1000));
	}
	
	public static void stopAlarm(int requestCode)
	{
		AlarmManager am = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(CONTEXT, requestCode, INTENT, 0);
		am.cancel(pendingIntent);
		Log.d("ALARMSTOP","========ALARM WAS STOPPED AT THIS POINT==========");
	}
	
	

	
	
	
}
