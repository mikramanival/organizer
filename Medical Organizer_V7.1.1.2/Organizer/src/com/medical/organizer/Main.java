package com.medical.organizer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import com.medical.organizer.fragments.ContactListFragment;
import com.medical.organizer.fragments.PatientListFragment;
import com.medical.organizer.fragments.ScheduleFragment;
import com.medical.organizer.utilities.Helper;

public class Main extends Activity {
	private int status = 0;
	private static String  globalDate;
	private static int backStackid;
	private static long  globalDateInMills = System.currentTimeMillis();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Helper help = new Helper(this);
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


	public static String getGlobalDate() {
		return globalDate;
	}


	public static void setGlobalDate(String globalDate) {
		Main.globalDate = globalDate;
	}


	public static long getGlobalDateInMills() {
		return globalDateInMills;
	}


	public static void setGlobalDateInMills(long globalDateInMills) {
		Main.globalDateInMills = globalDateInMills;
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
