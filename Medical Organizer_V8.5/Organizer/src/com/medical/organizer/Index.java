package com.medical.organizer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.medical.organizer.utilities.Helper;

public class Index extends Activity {
	private Helper help = new Helper(this);
	private  EditText email;
	private  EditText name;
	private TelephonyManager telMan;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
         help.connectDatabase();
         telMan = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
         
         if(help.checkDeviceIdIfExist(telMan.getDeviceId()))
         {
        	 Intent i = new Intent(this, Main.class);
        	 startActivity(i);
         }
         else
         {
        	 email = (EditText) findViewById(R.id.email);
             name = (EditText) findViewById(R.id.name);
         }
        
    }
    
 
    @Override
    protected void onPause() {
    	super.onPause();
    	finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_index, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	
    	case R.id.register:
    		ArrayList<View> views = new ArrayList<View>();
    		views.add(email);
    		views.add(name);
	    	if(help.checkInputs(views))
	    	{
	    		String em = email.getText().toString();
	    		String na = name.getText().toString();
	    		String dev = telMan.getDeviceId();
	    		help.registerLoginDetails(dev, em, na);
	    		Toast.makeText(this, "Account Registered", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(this, Main.class);
				startActivity(i);
	    	}
	    	else
	    	{
	    		Toast.makeText(this, "Please input all Fields!", Toast.LENGTH_SHORT).show();
	    	}
			return true;
			
		case R.id.back:
			finish();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
    	
    }
}
