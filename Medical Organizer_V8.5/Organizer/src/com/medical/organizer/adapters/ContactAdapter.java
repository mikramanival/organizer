package com.medical.organizer.adapters;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.medical.organizer.R;
import com.medical.organizer.utilities.Contacts;

public class ContactAdapter extends ArrayAdapter<Contacts> implements SectionIndexer {
	int resource;
	String response;
	Context context;
	  private HashMap<String, Integer> alphaIndexer;
	  private String[] sections;
	  
	public ContactAdapter(Context context, int resource,ArrayList<Contacts> contacts) {
		super(context, resource, contacts);
		/*alphaIndexer = new HashMap<String, Integer>();
        for (Contacts c : contacts)
        { 
        	int i = 0;
            String s = c.getFname().substring(0, 1).toUpperCase();
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
		Contacts c = getItem(position);
		
		if(convertView == null)
			convertView = (View) inflater.inflate(resource, parent, false);

	
		TextView full_name = (TextView) convertView.findViewById(R.id.contacts_full_name);
		full_name.setText(c.toString());
		TextView num = (TextView) convertView.findViewById(R.id.contacts_number);
		num.setText(c.getNum());
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

