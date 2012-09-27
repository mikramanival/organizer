package com.medical.organizer.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.utilities.Contacts;
import com.medical.organizer.utilities.Helper;

public class ImportContactFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	private static UUID CONTACT_ID;
	private Helper help = new Helper(getActivity());
	private ArrayList<Contacts> arrayOfContacts;
	public String[] Contacts = {};
	public int[] to = {};
	private SimpleCursorAdapter adapter;

	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
	 }
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.import_contact_list_fragment, menu);
	}
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		case R.id.clear:
			getListView().clearChoices();
			loadImportedContacts();
			return true;
		case R.id.save:
			saveImportedContacts();
			return true;	
		case R.id.back:
			FragmentManager fm = getFragmentManager();
			fm.popBackStack(Main.getBackStackid(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
		
		loadImportedContacts();
	}
	
	private void saveImportedContacts()
	{
		ListView v = getListView();
		int count = v.getCheckedItemCount();
		long[] ids = v.getCheckItemIds();
		ArrayList<Contacts> cList = new ArrayList<Contacts>();
			
			for(int i = 0; i < ids.length; i++)
			{
				Contacts c = getContactDetails(ids[i]);
				cList.add(c);
				Log.d("CheckItemCount","ID: "+ids[i]);
			}
	
			for(Contacts items: cList)
			{
				if(!isNameExistingOnList(items.getName()))
				{
				 	 CONTACT_ID = UUID.randomUUID();
				 	 items.setC_id(String.valueOf(CONTACT_ID));
				 	 items.setImported(true);
				 	 help.insert(items);
					 Log.d("CheckItemCount","Name: "+items.getName());
					 Log.d("CheckItemCount","Number: "+items.getNum());
				}
				else
				{
					 Log.d("CheckItemCount","ERROR CONTACT IS EXISTING IN DB");
				}
			}
			
			Toast.makeText(getActivity(), "Contacts Imported", Toast.LENGTH_SHORT).show();

		 	FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
    		ft.replace(R.id.fragment_container, new ContactListFragment()).commit();
	}
	
	private void loadImportedContacts()
	{
		Cursor c = getContacts();
		getActivity().startManagingCursor(c);		
		adapter = new SimpleCursorAdapter(getActivity(), 
							android.R.layout.simple_list_item_multiple_choice, 
							c, 
							Contacts = new String[] { ContactsContract.Contacts.DISPLAY_NAME },
				            to = new int[] { android.R.id.text1 });
         setListAdapter(adapter);
         setListShown(false);
         
         getLoaderManager().initLoader(0, null, this);
         
         ListView v = getListView();
         v.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
         v.setItemsCanFocus(false);
	}
	
	private Contacts getContactDetails(long id) {
	    Contacts con = null;
	    Cursor contactCursor = null;
	    contactCursor = queryContactsDetails(id);
	    if(contactCursor != null)
	    {
	    	if(contactCursor.getCount() != 0)
	    	{	
	    		contactCursor.moveToFirst();
	    		while(!contactCursor.isAfterLast())
	    		{
	    			con = new Contacts();
	    			con.setName(contactCursor.getString(contactCursor.getColumnIndex(Phone.DISPLAY_NAME)));
	    			con.setNum(contactCursor.getString(contactCursor.getColumnIndex(Phone.NUMBER)));
	    			contactCursor.moveToNext();
	    		}
	    	}
	    }
	    return con;
	}


	private Cursor queryContactsDetails(long contactId) {
	    ContentResolver cr = getActivity().getContentResolver();
	    Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
	            contactId);
	    Uri dataUri = Uri.withAppendedPath(baseUri,
	            ContactsContract.Contacts.Data.CONTENT_DIRECTORY);

	    Cursor c = cr.query(dataUri, 
				    		new String[] { Phone._ID, Phone.NUMBER,
				            Phone.IS_SUPER_PRIMARY, RawContacts.ACCOUNT_TYPE, Phone.TYPE,
				            Phone.LABEL , Phone.DISPLAY_NAME},
				            Data.MIMETYPE + "=?",
				            new String[] { Phone.CONTENT_ITEM_TYPE },
				            null);
	    if (c != null && c.moveToFirst()) {
	        return c;
	    }
	    return null;
	}

	
	private Cursor getContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";
        
        return getActivity().getContentResolver().query(uri, projection, selection, selectionArgs,
                sortOrder);
    }
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";
        
        return new CursorLoader(getActivity(), uri,
        		projection, selection, selectionArgs,
        		sortOrder);
	}
	
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data)
	{
		if(data.getCount() <= 0)
				Toast.makeText(getActivity(), "Phone Contacts Empty", Toast.LENGTH_LONG).show();
		
		adapter.swapCursor(data);
		if(isResumed())
		{
			setListShown(true);
		}
		else
		{
			setListShownNoAnimation(true);
		}
		
	}
	
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		adapter.swapCursor(null);
	}
	
	private ArrayList<Contacts> convertList(ArrayList<Object> o)
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
	
	private boolean isNameExistingOnList(String name)
	{
		int count = 0;
		arrayOfContacts = convertList(help.getData(new ContactListFragment(), null));
		
		for(Contacts item : arrayOfContacts)
		{
			if(item.getName().equals(name))
			{
				count = 1;
				break;
			}
			
		}
		return count == 1 ? true : false;
	}
	
}
