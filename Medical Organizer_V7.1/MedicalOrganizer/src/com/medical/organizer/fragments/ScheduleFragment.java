package com.medical.organizer.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;


import com.medical.organizer.Main;
import com.medical.organizer.R;

public class ScheduleFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.schedule_fragment, container, false);
		CalendarView cw = (CalendarView) v.findViewById(R.id.calendar);
		
		cw.setDate(((Main) getActivity()).getDateInMills());
		
			cw.setOnDateChangeListener(new OnDateChangeListener() {
				
				public void onSelectedDayChange(CalendarView view, int year, int month,
						int dayOfMonth) {
					((Main) getActivity()).setDate(formatDate(view.getDate()));
					((Main) getActivity()).setDateInMills(view.getDate());
					FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.addToBackStack("ScheduleList");
		    		//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					ft.replace(R.id.fragment_container, new ScheduleListFragment()).addToBackStack("Agenda").commit();
					
				}
			});
	
		return v;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);

	}

    public String formatDate(long millisec)
    {
    	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(millisec);
    	return format.format(cal.getTime());
    }
}
