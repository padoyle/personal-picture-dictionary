package com.padoyle.speechhelper;


public enum Category {
	PEOPLE("People"),
	PLACES("Places"),
	OBJECTS("Objects"),
	FOOD("Food"),
	ACTIONS("Actions"),
	OTHER("Other");
	
	private String mName;
	
	private Category(String name) {
		this.mName = name;
	}
	
	public String getName() {
		return mName;
	}
}