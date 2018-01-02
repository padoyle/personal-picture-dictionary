package com.padoyle.speechhelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WordTable {

	public static final String DATABASE_TABLE_WORD = "word_table";
	
	public static final String WORD_KEY_ID = "_id";
	public static final int WORD_COL_ID = 0;
	
	public static final String WORD_KEY_TEXT = "word_text";
	public static final int WORD_COL_TEXT = WORD_COL_ID + 1;
	
	public static final String WORD_KEY_IMAGE = "image";
	public static final int WORD_COL_IMAGE = WORD_COL_ID + 2;
	
	public static final String WORD_KEY_CATEGORY = "category";
	public static final int WORD_COL_CATEGORY = WORD_COL_ID + 3;
	
	/** Create the table */
	public static final String DATABASE_CREATE = "create table " + DATABASE_TABLE_WORD + " (" +
			WORD_KEY_ID + " integer primary key autoincrement, " +
			WORD_KEY_TEXT + " text not null, " +
			WORD_KEY_IMAGE + " text not null, " +
			WORD_KEY_CATEGORY + " text not null);";

	/** Drop table command used if upgrading database */
	public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE_WORD;
	
	/**
	 * Initializes the database.
	 * 
	 * @param database
	 * 				The database to initialize.	
	 */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	/**
	 * Upgrades the database to a new version.
	 * 
	 * @param database
	 * 					The database to upgrade.
	 * @param oldVersion
	 * 					The old version of the database.
	 * @param newVersion
	 * 					The new version of the database.
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(WordTable.class.getName(), "Upgrading database from version " + 
				oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP);
		WordTable.onCreate(database);
	}
}
