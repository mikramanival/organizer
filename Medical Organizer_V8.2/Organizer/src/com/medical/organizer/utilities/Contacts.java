package com.medical.organizer.utilities;

public class Contacts {
private String fname;
private String lname;
private String addr;
private String c_id;
private String c_number;
private String Specialty;

public String getFname() {
	return fname;
}
public void setFname(String fname) {
	this.fname = fname;
}
public String getLname() {
	return lname;
}
public void setLname(String lname) {
	this.lname = lname;
}
public String getAddr() {
	return addr;
}
public void setAddr(String addr) {
	this.addr = addr;
}

public String getC_id() {
	return c_id;
}
public void setC_id(String c_id) {
	this.c_id = c_id;
}
public String getNum() {
	return c_number;
}
public void setNum(String number) {
	this.c_number= number;
}
public String getSpec() {
	return Specialty;
}
public void setSpec(String specialty) {
	this.Specialty = specialty;
}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Dr. "+this.lname+", "+this.fname;
	}


}

