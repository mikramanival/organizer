package com.medical.organizer.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.utilities.Helper;
import com.medical.organizer.utilities.PatientSchedule;

public class ScheduleFragment extends Fragment {
	Helper help = new Helper(getActivity());	
	CalendarView cw;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.schedule_fragment, container, false);
		cw = (CalendarView) v.findViewById(R.id.calendar);
		cw.setDate(Main.getGlobalDateInMills());
		
		cw.setOnDateChangeListener( new OnDateChangeListener() {
			
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				String date = new PatientSchedule().formatDate(cw.getDate(), "EEEEE, MMMMM, dd yyyy");
				Main.setGlobalDate(date);
				Main.setGlobalDateInMills(cw.getDate());
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.addToBackStack("ScheduleList");
	    		//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.replace(R.id.fragment_container, new ScheduleListFragment()).commit();
			}
		});
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.schedule_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add_event_on:
				Toast.makeText(getActivity(), "Add!", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.show_event_on:
				Toast.makeText(getActivity(), "Show!", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.today:
				cw.setDate(System.currentTimeMillis());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}

