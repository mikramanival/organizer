package com.medical.organizer.fragments;


import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ActionBar;
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
import android.widget.DatePicker;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.utilities.Helper;
import com.medical.organizer.utilities.Schedule;

public class ScheduleFragment extends Fragment {
	private Helper help = new Helper(getActivity());	
	private CalendarView cw;

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
		cw.setDate(Main.CALENDAR.getTimeInMillis());
		
		cw.setOnDateChangeListener( new OnDateChangeListener() {
			
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				Main.CALENDAR.set(year, month, dayOfMonth);
				((Main) getActivity()).setType("rounds");
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
				DateDialogFragment dateFragAdd = new DateDialogFragment();
				Main.dateListener = new OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						Main.CALENDAR.set(year, monthOfYear, dayOfMonth);
						FragmentManager fm = getFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						ActionBar ac = getActivity().getActionBar();
			    		ac.hide();
			    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
						int stackId = ft.replace(R.id.fragment_container, new AddScheduleFragment()).addToBackStack("AddPatient").commit();
						Main.setBackStackid(stackId);
						
					}
				};
				dateFragAdd.show(getFragmentManager(), "add_datepicker");
				return true;
			case R.id.show_event_on:
				Toast.makeText(getActivity(), "Show!", Toast.LENGTH_SHORT).show();
				DateDialogFragment dateFragShow = new DateDialogFragment();
				Main.dateListener = new OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						Main.CALENDAR.set(year, monthOfYear, dayOfMonth);
						FragmentManager fm = getFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						ft.addToBackStack("ScheduleList");
			    		//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
						ft.replace(R.id.fragment_container, new ScheduleListFragment()).commit();
					}
				};
				dateFragShow.show(getFragmentManager(), "show_datepicker");
				return true;
			case R.id.today:
				cw.setDate(System.currentTimeMillis());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}

