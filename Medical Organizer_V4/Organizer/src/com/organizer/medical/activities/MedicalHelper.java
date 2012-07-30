package com.organizer.medical.activities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.organizer.medical.others.Contacts;
import com.organizer.medical.others.Patients;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class MedicalHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.organizer.medical.activities/databases/";
 
    private static String DB_NAME = "MedicalOrganizer";
 
    private SQLiteDatabase databaseConnect; 
 
    private final Context myContext;
	
	public MedicalHelper(Context context) {
		
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
    
    public Cursor retrieveAllData(String table_name) throws SQLException{
    	 //Retrieve all data!
    	 return databaseConnect.rawQuery("SELECT * FROM "+table_name, null);
    }
    
    public void insertIntoDatabase(String table_name, Patients p) throws SQLException{
   	 	openDataBase();
   	 	String sql = "INSERT into "+table_name+"(firstname,middleinitial,lastname,address,age,med_history,status)" +
   	 				 "VALUES('"+p.getFname()+"','"+p.getMi()+"','"+p.getLname()+"','"+p.getAddr()+"',"+p.getAge()+",'"+p.getMed_history()+"',"+p.getPat_status()+");";
   	 	databaseConnect.execSQL(sql);
    }
    
    //for ContactActivity
    public void C_insertIntoDatabase(String table_name, Contacts c) throws SQLException{
   	 	openDataBase();
   	 	String sql = "INSERT into "+table_name+"(Address,C_number,Fname,Lname,Specialty)" +
   	 				 "VALUES('"+c.getAddr()+"',"+c.getNum()+",'"+c.getFname()+"','"+c.getLname()+"','"+c.getSpec()+"');";
   	 	databaseConnect.execSQL(sql);
    }
    public Cursor retrieveAllDataWhere(String left_assign, String right_assign ){
   	 //Retrieve all data!
    	if(left_assign == "status")
    		return databaseConnect.rawQuery("SELECT * FROM Patients where "+left_assign+"="+Integer.parseInt(right_assign), null);
    	else
    		return databaseConnect.rawQuery("SELECT * FROM Patients where "+left_assign+"='"+right_assign+"'", null);
    }
    
    @Override
	public synchronized void close() {
 
    	    if(databaseConnect != null)
    		    databaseConnect.close();
 
    	    super.close();
 
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
