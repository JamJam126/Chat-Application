package utils;

import java.util.Scanner;

import java.util.Map;
import java.util.HashMap;

public class User extends NewUser  {
    private static final long serialVersionUID = 1L;
	private Map<String, String> change = new HashMap<>();
    
    public User(String username, String password) {
    	super();
        this.username = username;
        this.password = password;
    }
	public User(int id){
		this.userId = id;
	}

	public Map<String, String> getChanges() {
        return change;
    }
	public void addChange(String fieldName, String value) {
        change.put(fieldName, value);
    }

    
//    public boolean isNewUser() {
//        return false;  // Returning false for login users
//    }

}
