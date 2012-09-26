package com.medical.organizer.fragments;


import java.util.ArrayList;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.adapters.ContactAdapter;
import com.medical.organizer.utilities.Contacts;
import com.medical.organizer.utilities.Helper;

public class ContactListFragment extends ListFragment implements TextWatcher {
	private Helper help = new Helper(getActivity());
	private static boolean flag_text_change;
	private Button save;
	private ContentResolver cr;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadContactMasterList();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		final Contacts contact = (Contacts) getListAdapter().getItem(info.position);
		//Log.d("itemcheck", "=============================================");
		//Log.d("itemcheck", "Contact ID: "+contacts.getC_id());
		//Log.d("itemcheck", "Contact Name: "+contacts.toString());
		//Log.d("itemcheck", "Contact Number: "+contacts.getNum());
		//Log.d("itemcheck", "Contact Specialty: "+contacts.getSpec());
		//Log.d("itemcheck", "Contact Address: "+contacts.getAddr());
		//Log.d("itemcheck", "=============================================");
		MenuItem edit = menu.add("Edit");
		MenuItem delete = menu.add("Delete");
		
		edit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				editContact(contact);
				return true;
			}
		});
		
		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				deleteContact(contact);
				return true;
			}
		});
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.contact_list_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
				//Toast.makeText(getActivity(), "Add!", Toast.LENGTH_SHORT).show();
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ActionBar ac = getActivity().getActionBar();
	    		ac.hide();
	    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    		int stackId = ft.replace(R.id.fragment_container, new AddContactFragment()).addToBackStack("AddContact").commit();
				Main.setBackStackid(stackId);
				return true;
		case R.id.impt:
			FragmentManager fM = getFragmentManager();
			FragmentTransaction fT = fM.beginTransaction();
    		fT.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    		int stackID= fT.replace(R.id.fragment_container, new ImportContactFragment()).addToBackStack("ImportContact").commit();
    		Main.setBackStackid(stackID);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	private void viewContactDetails(final Contacts contact)
	{
		Dialog info = new Dialog(getActivity());
		info.setTitle("Doctor Details");
		info.setContentView(R.layout.contact_info);
		ImageButton call = (ImageButton) info.findViewById(R.id.call_button);
		TextView number = (TextView) info.findViewById(R.id.contactnumber_info);
		TextView name = (TextView) info.findViewById(R.id.name_info);
		TextView address = (TextView) info.findViewById(R.id.haddress_info);
		TextView specialty = (TextView) info.findViewById(R.id.specialty_info);
		
		name.setText(contact.getName());
		address.setText(contact.getAddr());
		number.setText(contact.getNum());
		specialty.setText(contact.getSpec());
		call.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:"+contact.getNum()));
				startActivity(intent);
			}
		});
		info.show();
	}
	
	private void deleteContact(final Contacts contact)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		build.setMessage("Do you really want to Delete this Entry?");
		build.setCancelable(false);
		build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Contacts con = contact;
				help.delete(con);
				Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
				loadContactMasterList();
			}
		});
		build.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog alert = build.create();
		alert.show();
	}
	
	private void editContact(final Contacts contact)
	{
		final Dialog d = new Dialog(getActivity());
		d.setTitle("Update Entry");
		d.setContentView(R.layout.input_contacts_entry);
			TextView header = (TextView) d.findViewById(R.id.header);
			header.setVisibility(TextView.GONE);
			final TextView Name = (TextView) d.findViewById(R.id.name_ca);
			final TextView address = (TextView) d.findViewById(R.id.address_ca);
			final TextView num = (TextView) d.findViewById(R.id.number_ca);
			final TextView specs = (TextView) d.findViewById(R.id.spec_ca);
			
			save = (Button) d.findViewById(R.id.add_edit_con_info);
			final Button cancel = (Button) d.findViewById(R.id.cancel_ca);
			
			save.setEnabled(false);
			
			save.setText("UPDATE");
			Name.setText(contact.getName());
			address.setText(contact.getAddr());
			num.setText(contact.getNum());
			specs.setText(contact.getSpec());
			
			Name.addTextChangedListener(this);
			address.addTextChangedListener(this);
			specs.addTextChangedListener(this);
			num.addTextChangedListener(this);
			
			save.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
					build.setMessage("Are all edits Correct?");	
			    	build.setCancelable(false);
					build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Contacts con = contact;
							con.setName(Name.getText().toString());
							con.setAddr(address.getText().toString());
							con.setNum(num.getText().toString());
							con.setSpec(specs.getText().toString());
							help.update(con, Helper.NORMAL);
							Toast.makeText(getActivity(), "Updated!", Toast.LENGTH_SHORT).show();
							loadContactMasterList();
							d.cancel();
						}
					});
					build.setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					
					AlertDialog alert = build.create();
					alert.show();
				}
			});
			cancel.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d.cancel();
				}
			});
		d.show();
	}
	
	private void loadContactMasterList()
	{	
		ArrayList<Contacts> master_list = new ArrayList<Contacts>();
		
		master_list = convertListtoContacts(help.getData(this, null));
		
		ContactAdapter contactAdapter = new ContactAdapter(getActivity(), 
										R.layout.contact_list_item_fragment,
										master_list);
		setListAdapter(contactAdapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		registerForContextMenu(getListView());
		
			getListView().setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Contacts contact = (Contacts) getListAdapter().getItem(arg2);
					viewContactDetails(contact);
				}
			});
	}
	
	private ArrayList<Contacts> convertListtoContacts(ArrayList<Object> o)
	{
		ArrayList<Contacts> list = new ArrayList<Contacts>();
		Iterator<Object> i = o.iterator();
		
			while(i.hasNext())
			{
				Object obj_item = i.next();
				if(obj_item instanceof Contacts)
				{
					Contacts c = (Contacts) obj_item;
					list.add(c);
				}
			}
		return list;
	}

	public void afterTextChanged(Editable s) {
		save.setEnabled(true);
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		save.setEnabled(true);
	}
	
	
}