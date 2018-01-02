package com.padoyle.speechhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class that opens the database and connects it to the WordContentProvider
 * using help from WordTable 
 * @author Paul
 *
 */
public class WordDatabaseHelper extends SQLiteOpenHelper {

	/** The name of the database */
	public static final String DATABASE_NAME = "speech_helper.db";
	
	/** The starting database version */
	public static final int DATABASE_VERSION = 1;	
	
	/**
	 * Create a helper object that can create, open, and/or manage tables in a database.
	 * 
	 * @param context The application context.
	 * @param name The name of the database.
	 * @param factory Factory used to create a cursor. Set to null for default behavior.
	 * @param version The starting database version.
	 */
	public WordDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		WordTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		WordTable.onUpgrade(database, oldVersion, newVersion);
	}

}
