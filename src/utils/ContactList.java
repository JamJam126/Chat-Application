package utils;

import java.io.Serializable;
import java.util.List;

public class ContactList implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Contact> contacts;

	public ContactList(List<Contact> contacts) {
		
		this.contacts = contacts;
	}
	
	public List<Contact> getContacts() {
		
        return contacts;
    }
}
