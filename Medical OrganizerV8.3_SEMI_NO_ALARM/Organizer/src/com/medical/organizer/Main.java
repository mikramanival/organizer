package com.medical.organizer;

import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.medical.organizer.fragments.ContactListFragment;
import com.medical.organizer.fragments.PatientListFragment;
import com.medical.organizer.fragments.ScheduleFragment;
import com.medical.organizer.fragments.ScheduleListFragment;
import com.medical.organizer.utilities.Helper;

public class Main extends Activity {
	private int status = 0;
	private String type;
	private static int backStackid;
	public static final Calendar CALENDAR = Calendar.getInstance();
	public static OnDateSetListener dateListener;
	public static OnTimeSetListener timeListener;
	public static BroadcastReceiver reciever;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Helper help = new Helper(this);
        CALENDAR.setTime(new Date(System.currentTimeMillis()));
        help.connectDatabase();
        
        ActionBar acBar = getActionBar();
        acBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab sched_tab = acBar.newTab().setText("Schedule");
        ActionBar.Tab patient_tab = acBar.newTab().setText("Patients");
        ActionBar.Tab contact_tab = acBar.newTab().setText("Contact");

        sched_tab.setTabListener(new MyTabListener(this, new ScheduleFragment()));
        patient_tab.setTabListener(new MyTabListener(this, new PatientListFragment()));
        contact_tab.setTabListener(new MyTabListener(this, new ContactListFragment()));
        
        acBar.addTab(sched_tab);
        acBar.addTab(patient_tab);
        acBar.addTab(contact_tab);
        
        acBar.setDisplayShowHomeEnabled(false);
        acBar.setDisplayShowTitleEnabled(false);
    }
    @Override
    protected void onNewIntent(Intent intent) {
    	// TODO Auto-generated method stub
    	super.onNewIntent(intent);
    	Log.d("OnNewIntent", "onNewIntent() was called");
    	/*this.intent = intent;
    	if(this.intent.hasExtra("type"))
    		setType(this.intent.getStringExtra("type"));*/
    
    	if(intent.getBooleanExtra("STARTED_BY_REC", false))
    	{
    		String type = intent.getStringExtra("type");
    		ActionBar ac = getActionBar();
    		Tab selected = ac.getSelectedTab();
    		setType(type);
    		if(selected.getText().equals("Schedule"))
    		{
    			FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
	    		//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.addToBackStack("SchedList");
				int stackId = ft.replace(R.id.fragment_container, new ScheduleListFragment()).commit();
				setStatus(stackId);
    		}
    	}
    	else
    	{
    		Log.d("ResumeCheck", "onResume() was NOT called by Reciever");
    	}
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	getActionBar().show();
		FragmentManager fm = getFragmentManager();
		fm.popBackStack(Main.getBackStackid(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    
	public static int getBackStackid() {
		return backStackid;
	}


	public static void setBackStackid(int backStackid) {
		Main.backStackid = backStackid;
	}

	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}

class MyTabListener implements ActionBar.TabListener{
	private Fragment gFrag;
	private Activity gAct;
	
	public MyTabListener(Activity act, Fragment frag){
		this.gFrag = frag;
		this.gAct = act;
	}
	
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Toast.makeText(gAct, "Reselected!", Toast.LENGTH_LONG).show();
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.fragment_container, gFrag);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		 ft.remove(gFrag);
		
	}
	
}
