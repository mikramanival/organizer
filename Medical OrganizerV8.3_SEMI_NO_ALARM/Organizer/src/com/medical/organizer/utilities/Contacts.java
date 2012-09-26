package com.medical.organizer.utilities;

public class Contacts {
private String name;
private String addr;
private String c_id;
private String c_number;
private String Specialty;
private boolean isImported;

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

/**
 * @return the display_name
 */
public String getName() {
	return name;
}
/**
 * @param name the display_name to set
 */
public void setName(String name) {
	this.name = name;
}

public boolean isImported() {
	return isImported;
}
public void setImported(boolean isImported) {
	this.isImported = isImported;
}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Dr. "+this.name;
	}


}

