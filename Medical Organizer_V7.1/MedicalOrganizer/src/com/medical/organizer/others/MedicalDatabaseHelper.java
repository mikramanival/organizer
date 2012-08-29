
package com.medical.organizer.others;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ContentHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class MedicalDatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.medical.organizer/databases/";
 
    private static String DB_NAME = "MedicalOrganizer";
 
    private SQLiteDatabase databaseConnect; 
 
    private final Context myContext;
	
	public MedicalDatabaseHelper(Context context) {
		
		super(context, DB_NAME, null, 1);
		
        this.myContext = context;
	}
	
	 /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(!dbExist){

    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	databaseConnect = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
 
    }
    
    @Override
   	public synchronized void close() {
    
       	    if(databaseConnect != null)
       		    databaseConnect.close();
    
       	    super.close();
    
   	}
    
/*=================READING VALUES FROM DB================*/    
    public Cursor retrieveAllData(String table_name) throws SQLException{
    	 //Retrieve all data!
    	openDataBase();
    	return databaseConnect.rawQuery("SELECT * FROM "+table_name, null);
    }

    public Cursor retrieveAllDataWhere(String tname, int status){
    	openDataBase();
   	 //Retrieve all data!
    	return databaseConnect.rawQuery("SELECT * FROM "+tname+" where status = "+status, null);

    }
    
    public Cursor retrieveAllEventsWhere(String tname,String date, String type){
    	openDataBase();
		return databaseConnect.rawQuery("SELECT * FROM "+tname+" where date='"+date+"' AND type = '"+type+"'", null);
    }
    
    public Cursor retrieveSpecificId(String tname, String id){
    	openDataBase();
    	return databaseConnect.rawQuery("SELECT * FROM "+tname+" where _id='"+id+"'", null);
    }
    
    public Cursor retriveRoundsInfo(String id){
    	openDataBase();
    	return databaseConnect.rawQuery("SELECT * FROM Schedule where patient_id='"+id+"'", null);
    }
    
    public int checkIdStatus(String tname, String id){
    	openDataBase();
    	Cursor c = databaseConnect.rawQuery("SELECT status FROM "+tname+" where _id='"+id+"'", null);
    	c.moveToFirst();
    	int status = c.getInt(c.getColumnIndex("status"));
    	Log.d("dbcheck","Status of Item Clicked is: "+status);
    	close();
    	return status;
    }
    
    public boolean checkIdAlarmStatus(int id){
    	openDataBase();
    	Cursor c = databaseConnect.rawQuery("SELECT alarm FROM Schedule where _id="+id, null);
    	c.moveToFirst();
    	boolean status = (c.getInt(c.getColumnIndex("alarm")) != 0);
    	Log.d("dbcheck","Alarm Status of Item Clicked is: "+status);
    	close();
    	return status;
    }
    
    public Cursor retrieveSpecificId(String tname, int id){
    	openDataBase();
    	return databaseConnect.rawQuery("SELECT * FROM "+tname+" where _id= "+id, null);
    }
    
/*=====================INSERTING VALUES INTO DB=====================*/    
    public void insertIntoDatabase(String table_name, ContentValues values) throws SQLException{

    	openDataBase();
   	 	try {
   	 		long ret = databaseConnect.insert(table_name, null, values);
   	 		Log.d("dbcheck", "Insert was successful insert() returned: "+Long.toString(ret));
		} catch (SQLException e) {
			e.printStackTrace();
		}
   	 	close();
   	 	

    }
    
/*=====================UPDATING VALUES INTO DB=====================*/      
    public void updateSpecificId(ContentValues values, String tname, String id){
    	openDataBase();
       	String where  = "_id = '"+id+"'";
    	try {
    		 databaseConnect.update(tname, values, where, null);
    		
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	close();
     }
    
    public void updateRoundSchedule(ContentValues values, String tname, String id){
    	openDataBase();
       	String where  = "patient_id = '"+id+"'";
    	try {
    		 databaseConnect.update(tname, values, where, null);
    		
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	close();
     }
    
    public void setAlarm(boolean alarm, int id)
    {
    	openDataBase();
    	ContentValues values = new ContentValues();
    	String where  = "_id = '"+id+"'";
	    values.put("alarm", alarm);
	    int i;
	    
	    	try {
	    		i = databaseConnect.update("Schedule", values, where, null);
	    		Log.d("dbcheck","Number of Rows Affected:: "+Integer.toString(i));
	        	
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    close(); 	
    }
    
    public void updateSpecificId(ContentValues values, String tname, int id){
    	openDataBase();
       	String where  = "_id = "+id;
    	try {
    		 databaseConnect.update(tname, values, where, null);
    		
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	close();
     }
    
     
    public void changePatientStatus(String tname, int status, String id)
    {
    	openDataBase();
    	ContentValues values = new ContentValues();
    	String where  = "_id = '"+id+"'";
	    values.put("status", status);
	    int i;
	    
	    	try {
	    		i = databaseConnect.update(tname, values, where, null);
	    		Log.d("dbcheck","Number of Rows Affected:: "+Integer.toString(i));
	        	
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    close(); 	
    }

/*=====================DELETING VALUES INTO DB=====================*/     
    public void deleteRecordById(String table, String id)
    {
    	openDataBase();
    	String where  = "_id = '"+id+"'";
    		try {
    	    	databaseConnect.delete(table, where, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	close();
    }
    
    public void deletePatientRoundsById(String id)
    {
    	openDataBase();
    	String where  = "patient_id = '"+id+"'";
    		try {
    	    	databaseConnect.delete("Schedule", where, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	close();
    }
    
    
    public void deleteRecordById(String table, int id)
    {
    	openDataBase();
    	String where  = "_id = "+id;
    		try {
    	    	databaseConnect.delete(table, where, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	close();
    }
    
/*=====================COUNTING ROW IN DB=====================*/      
 public int countRowScehdule(String date, String type)
 {
	 int count = 0;
	 openDataBase();
	 	Cursor c =  databaseConnect.rawQuery("SELECT * FROM Schedule where date='"+date+"' AND type = '"+type+"'", null);
	 	count = c.getCount();
	 	Log.d("dbcheck", Integer.toString(count)+" with Type: "+type+" with Date: "+date);
	 close();
	 return count;
 }
 
 public int countRow(String table)
 {
	 int count = 0;
	 openDataBase();	
	 
	 	Cursor c = databaseConnect.rawQuery("SELECT * from "+table, null);
	 	count = c.getCount();
    	Log.d("dbcheck","Current Rows in DB: "+count);
	 close();
	 return count;
 }
    
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
