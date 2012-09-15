package com.medical.organizer.fragments;

import java.util.ArrayList;
import java.util.UUID;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.utilities.Contacts;
import com.medical.organizer.utilities.Helper;

public class AddContactFragment extends Fragment {
	private static UUID CONTACT_ID;
	private Helper help = new Helper(getActivity());
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.input_contacts_entry, container, false);
		TextView header = (TextView) v.findViewById(R.id.header);
		header.setText("New Doctor Entry");
		
		final EditText last_name = (EditText) v.findViewById(R.id.lname_ca);
		final EditText first_name = (EditText) v.findViewById(R.id.fname_ca);
		final EditText specialty = (EditText) v.findViewById(R.id.spec_ca);
		final EditText address = (EditText) v.findViewById(R.id.address_ca);
		final EditText number = (EditText) v.findViewById(R.id.number_ca);

		final ArrayList<View> views = new ArrayList<View>();
			views.add(last_name);
			views.add(first_name);
			views.add(specialty);
			views.add(address);
			views.add(number);
		
		
		Button add = (Button) v.findViewById(R.id.add_edit_con_info);
		Button cancel = (Button) v.findViewById(R.id.cancel_ca);
		
			add.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(help.checkInputs(views))
					{
						Contacts contact = new Contacts();
						CONTACT_ID = UUID.randomUUID();
						contact.setC_id(String.valueOf(CONTACT_ID));
						contact.setFname(first_name.getText().toString());
						contact.setLname(last_name.getText().toString());
						
						contact.setNum(number.getText().toString());
						contact.setSpec(specialty.getText().toString());
						contact.setAddr(address.getText().toString());
						
						Toast.makeText(getActivity(), "Contact Details Saved!", Toast.LENGTH_LONG).show();
						help.insert(contact);
							
							ActionBar ac = getActivity().getActionBar();
				    		ac.show();
							FragmentManager fm = getFragmentManager();
							fm.popBackStack();
					}
					else
					{
						Toast.makeText(getActivity(), "Please Input all Fields!", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ActionBar ac = getActivity().getActionBar();
		    		ac.show();
					FragmentManager fm = getFragmentManager();
					fm.popBackStack(Main.getBackStackid(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
				}
			});
		
		
		
		return v;
	}
	
}
