
package com.medical.organizer.utilities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.medical.organizer/databases/";
 
    private static String DB_NAME = "MedicalOrganizer";
 
    private SQLiteDatabase databaseConnect; 
 
    private final Context myContext;
	
	public Database(Context context) {
		
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

/*========================RETRIEVAL============================*/    
    public Cursor getData(String table_name, String date) throws SQLException{
    	 //Retrieve all data!
    	openDataBase();
    	Cursor c;
    	if(date!=null)
    		c = databaseConnect.rawQuery("SELECT * FROM "+table_name+ " where date = '"+date+"'"+" OR date = 'everyday'", null);
    	else
    		c = databaseConnect.rawQuery("SELECT * FROM "+table_name, null);
    	return c;
    }

    public Cursor getScheduledPatient(String id)
    {
    	openDataBase();
    	return databaseConnect.rawQuery("SELECT * FROM Patients where _id = '"+id+"'", null);
    }
    
    public Cursor getPatientScheduled(String id)
    {
    	openDataBase();
    	return databaseConnect.rawQuery("SELECT * FROM Schedule where patient_id = '"+id+"'", null);  
    }
    
    public Cursor getScheduleDetalils(String id)
    {
    	openDataBase();
    	return databaseConnect.rawQuery("SELECT * FROM Schedule where _id = '"+id+"'", null);  
    }

/*=====================INSERTING VALUES INTO DB=====================*/    
    public void pushData(String table_name, ContentValues values) throws SQLException{

    	openDataBase();
   	 	try {
   	 		long ret = databaseConnect.insert(table_name, null, values);
   	 		Log.d("dbcheck", table_name+" Table - Inserted @ row number: "+Long.toString(ret)+" Successfull");
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
    		 int ret = databaseConnect.update(tname, values, where, null);
    		 Log.d("dbcheck", tname+" Table - Number of Rows Affected: "+Long.toString(ret)+" Successfull");
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
 
/*=====================DELETING VALUES INTO DB=====================*/     
    public void deleteRecord(String table, String id)
    {
    	openDataBase();
    	String where  = "_id = '"+id+"'";
    		try {
    	    	int ret = databaseConnect.delete(table, where, null);
    	    	Log.d("dbcheck", table+" Table - Number of Rows Deleted/Affected: "+Long.toString(ret)+" Successfull");
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	close();
    }
    
    public void deleteRecord(String table, int id)
    {
    	openDataBase();
    	String where  = "_id = "+id;
    		try {
    	    	int ret = databaseConnect.delete(table, where, null);
    	    	Log.d("dbcheck", table+" Table - Number of Rows Deleted/Affected: "+Long.toString(ret)+" Successfull");
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	close();
    }
   
/*=======================VALIDATE LOGIN=======================*/   
 public void pushLoginDetails(ContentValues values, String table_name)
 {
 	openDataBase();
	 	try {
	 		long ret = databaseConnect.insert(table_name, null, values);
	 		Log.d("dbcheck", table_name+" Table - Inserted @ row number: "+Long.toString(ret)+" Successfull");
	 	} catch (SQLException e) {
			e.printStackTrace();
		}
	 close();
 }
  
 public boolean checkDeviceId(String deviceId)
 {
	 openDataBase();
	 String sql = "SELECT * FROM Login where device_id = '"+deviceId+"'";
	 Cursor c =  databaseConnect.rawQuery(sql,null);
	 int count = c.getCount();
	 close();
	 return count > 0 ? true : false;
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
    
 /*===============TEMPORARY ONLY FOR CLEANING DEVs ONLY===============*/
 public void clear(String tname)
 {	
	 openDataBase();	
	 databaseConnect.delete(tname, null, null);
	 Log.d("DBCHECK", "TABLE CLEARD!");
	 close();
 }
 
 public void clear(String tname,String typeArg, String arg)
 {	
	 openDataBase();	
	 String where = typeArg +" = '"+arg+"'";
	 	databaseConnect.delete(tname, where, null);
	 close();
 }
 /*===============       END         ===============*/
 
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}