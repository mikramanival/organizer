package com.organizer.medical.others;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.Set;

import com.organizer.medical.activities.R;
import com.organizer.medical.activities.R.id;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;



public class ContactAdapter extends ArrayAdapter<Contacts> implements SectionIndexer {
	int resource;
	String response;
	Context context;
	  private HashMap<String, Integer> alphaIndexer;
	  private String[] sections;
	  
	public ContactAdapter(Context context, int resource,ArrayList<Contacts> contacts) {
		super(context, resource, contacts);
		alphaIndexer = new HashMap<String, Integer>();
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
		 
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FrameLayout lv;
		Contacts p = getItem(position);
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

		TextView mark_id = (TextView) lv.findViewById(R.id.con_id);
		mark_id.setText(Integer.toString(p.getC_id()));
		TextView full_name = (TextView) lv.findViewById(R.id.full_name_c);
		full_name.setText(p.toString());
		TextView spec = (TextView) lv.findViewById(R.id.spec_c);
		spec.setText(p.getSpec());
		TextView address = (TextView) lv.findViewById(R.id.address_c);
		address.setText(p.getAddr());
		TextView num = (TextView) lv.findViewById(R.id.number_c);
		//num.setText(p.getNum());

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

