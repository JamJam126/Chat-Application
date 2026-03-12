package utils;

import java.io.Serializable;

public class ImageMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
    private byte[] imageData;
    private int size;
    
    
    public ImageMessage(String name, int size, byte[] imageData) {

    	this.name = name;
        this.size = size;
        this.imageData = imageData;
    }

    public byte[] getImageData() {
        return imageData;
    }
    
    public void setImageDate(byte[] imageData) {
    	this.imageData = imageData;
    }

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
