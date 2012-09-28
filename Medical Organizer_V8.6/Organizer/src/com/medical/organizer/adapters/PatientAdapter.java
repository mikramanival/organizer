package com.medical.organizer.adapters;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.medical.organizer.R;
import com.medical.organizer.utilities.Patients;

public class PatientAdapter extends ArrayAdapter<Patients> implements SectionIndexer {
	int resource;
	String response;
	Context context;
	private HashMap<String, Integer> alphaIndexer;
	  private String[] sections;
	  
	public PatientAdapter(Context context, int resource,	ArrayList<Patients> patients) {
		super(context, resource, patients);
		/*alphaIndexer = new HashMap<String, Integer>();
        for (Patients p : patients)
        { 
        	int i = 0;
            String s = p.getFname().substring(0, 1).toUpperCase();
            if (!alphaIndexer.containsKey(s))
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
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Patients p = getItem(position);
		
		if(convertView == null)
			convertView = (View) inflater.inflate(resource, parent, false);
		
		TextView full_name = (TextView) convertView.findViewById(R.id.full_name);
		TextView mark_id = (TextView) convertView.findViewById(R.id.id);
		TextView hosp_room = (TextView) convertView.findViewById(R.id.hospital_details);
		if(p.getPat_status() == 1)
			hosp_room.setText(p.getHosp_name()+", Room: "+p.getHosp_room());
		
		if(p.getPat_status() == 2)
			hosp_room.setText("Out-Patient");
		//mark_id.setText(p.getPatient_id());
		full_name.setText(p.toString());

		return convertView;
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
