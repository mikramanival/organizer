package com.medical.organizer.utilities;

public class Contacts {
private String fname;
private String lname;
private String addr;
private int c_id;
private int c_number;
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

public int getC_id() {
	return c_id;
}
public void setC_id(int c_id) {
	this.c_id = c_id;
}
public int getNum() {
	return c_number;
}
public void setNum(int number) {
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
		return this.lname+", "+this.fname;
	}


}

