package com.padoyle.speechhelper;

public class Word {

	private long mId;
	private String mText;
	private String mImagePath;
	private Category mCategory;
	
	/**
	 * Create a new empty word object
	 * 
	 * @return A word object with default member values
	 */
	public Word() {
		this.mId = 0;
		this.mText = "";
		this.mImagePath = "";
		this.mCategory = Category.OTHER;
	}
	
	public Word(String text) {
		this.mId = 0;
		this.mImagePath = "";
		this.mCategory = Category.OTHER;
		
		this.mText = text;
	}
	
	/**
	 * Create a new word object
	 * @param id The ID of the word object to create
	 * @param text The text for the word itself
	 * @param imagePath The path to the image associated with this word
	 * @param category The category for this word
	 */
	public Word(long id, String text, String imagePath, Category category) {
		this.mId = id;
		this.mText = text;
		this.mImagePath = imagePath;
		this.mCategory = category;
	}

	public long getId() {
		return mId;
	}
	
	public void setId(long id) {
		this.mId = id;
	}

	public String getText() {
		return mText;
	}

	public String getImagePath() {
		return mImagePath;
	}

	public Category getCategory() {
		return mCategory;
	}
}