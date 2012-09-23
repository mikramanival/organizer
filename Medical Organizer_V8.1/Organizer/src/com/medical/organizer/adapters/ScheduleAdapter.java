package com.medical.organizer.adapters;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.medical.organizer.R;
import com.medical.organizer.utilities.Schedule;

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
		
		TextView event_rem = (TextView) lv.findViewById(R.id.title_header);
		event_rem.setText(s.toString());
		ImageView alarm = (ImageView) lv.findViewById(R.id.alarm_clock);
		TextView sched_id = (TextView) lv.findViewById(R.id.id);
		sched_id.setText(s.getSchedule_id());
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