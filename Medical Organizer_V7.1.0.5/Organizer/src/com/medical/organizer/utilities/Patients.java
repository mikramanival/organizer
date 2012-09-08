package com.medical.organizer.utilities;

public class Patients {
private String id;
private String fname;
private String mi;
private String lname;
private String addr;
private int age;
private String med_history;
private int pat_status;
private String hosp_room;
private String hosp_name;

public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getFname() {
	return fname;
}
public void setFname(String fname) {
	this.fname = fname;
}
public String getMi() {
	return mi;
}
public void setMi(String mi) {
	this.mi = mi;
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
public int getAge() {
	return age;
}
public void setAge(int age) {
	this.age = age;
}
public String getMed_history() {
	return med_history;
}
public void setMed_history(String med_history) {
	this.med_history = med_history;
}
public int getPat_status() {
	return pat_status;
}
public void setPat_status(int pat_status) {
	this.pat_status = pat_status;
}

public String getHosp_room() {
	return hosp_room;
}
public void setHosp_room(String hosp_room) {
	this.hosp_room = hosp_room;
}
public String getHosp_name() {
	return hosp_name;
}
public void setHosp_name(String hosp_name) {
	this.hosp_name = hosp_name;
}

@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.lname+", "+this.fname+" "+this.mi.substring(0,1)+".";
	}

}