package utils;

import java.io.Serializable;
import java.util.Scanner;

public class NewUser implements Serializable {
    protected String username;
    protected String password;
    protected int userId;
    private String dob;
    private String email;
    private static final long serialVersionUID = 1L;
    private static final int MIN_PASSWORD_LENGTH = 8;
    
	public NewUser() {
		
    }
	
    public NewUser(String username, String password, String dob, String email){
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.email = email;

    }
   
    public String getDob() {
        return dob;
    }
    
    public void setDob(String dob) {
        this.dob = dob;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }
    

//    public boolean isNewUser() {
//        return true;  // Always return true for new users
//    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
