package com.organizer.medical.others;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.Set;

import com.organizer.medical.activities.PatientActivity;
import com.organizer.medical.activities.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ScheduleAdapter extends ArrayAdapter<Schedule> implements SectionIndexer {
	int resource;
	String response;
	Context context;
	  //private HashMap<String, Integer> alphaIndexer;
	  //private String[] sections;
	  
	public ScheduleAdapter(Context context, int resource,	ArrayList<Schedule> schedule) {
		super(context, resource, schedule);
		/*alphaIndexer = new HashMap<String, Integer>();
        for (Schedule s : schedule)
        { 
        	int i = 0;
            String st = s.getFname().substring(0, 1).toUpperCase();
            if (!alphaIndexer.containsKey(st))
               alphaIndexer.put(s, i);
            i++;
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        for (int i = 0; i < sectionList.size(); i++)
            sections[i] = sectionList.get(i);   
		 */
		this.context = context;
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FrameLayout lv;
		Schedule s = getItem(position);
		if(convertView == null)
		{
			lv = new FrameLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater) getContext().getSystemService(inflater);
			li.inflate(resource, lv, true);
			
		}
		else
		{
			lv = (FrameLayout) convertView;
		}
		
		TextView event_rem = (TextView) lv.findViewById(R.id.event_rem_item);
		event_rem.setText(s.toString());
		ImageView alarm = (ImageView) lv.findViewById(R.id.alarm_clock);
		TextView sched_id = (TextView) lv.findViewById(R.id.sched_id);
		sched_id.setText(Integer.toString(s.getId()));
		if(s.isAlarm())
			alarm.setVisibility(ImageButton.VISIBLE);

			

		return lv;
	}

	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
