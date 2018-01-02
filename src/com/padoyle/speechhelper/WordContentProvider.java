package com.padoyle.speechhelper;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WordContentProvider extends ContentProvider {

	/** Database connection helper object */
	private WordDatabaseHelper mDatabase;
	
	/** URI matcher values */
	private static final int WORD_ID = 1;
	private static final int WORD_CATEGORY = 2;
	
	/** The authority for the content provider */
	private static final String AUTHORITY = "com.padoyle.speechhelper.contentprovider";
	
	/** Table used for reading, writing, and URI matching */
	private static final String BASE_PATH = "word_table";
	
	/** Location of the content this provider interacts with, used by activities that access it */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	/** Matches URI given with the expected access types */
	public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/word/#", WORD_ID);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/category/*", WORD_CATEGORY);
	}
	
	@Override
	public boolean onCreate() {
		mDatabase = new WordDatabaseHelper(getContext(), WordDatabaseHelper.DATABASE_NAME,
				null, WordDatabaseHelper.DATABASE_VERSION);
		return true;
	}

	/** Not used */
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection);
		
		queryBuilder.setTables(WordTable.DATABASE_TABLE_WORD);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case WORD_CATEGORY:
			String category = uri.getLastPathSegment();
			queryBuilder.appendWhere(WordTable.WORD_KEY_CATEGORY + "='" + category + "'");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		SQLiteDatabase db = this.mDatabase.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, null, null, null, null);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {	
		SQLiteDatabase db = this.mDatabase.getWritableDatabase();
		long id = 0;
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case WORD_ID:
			id = db.insert(WordTable.DATABASE_TABLE_WORD, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = this.mDatabase.getWritableDatabase();
		
		int updatedRows = 0;
		int uriType = sURIMatcher.match(uri);
		
		switch(uriType) {
		case WORD_ID:
			String wordToUpdate = uri.getLastPathSegment();
			db.update(WordTable.DATABASE_TABLE_WORD, values, 
					WordTable.WORD_KEY_ID + "=" + wordToUpdate, null);
			updatedRows++;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updatedRows;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = this.mDatabase.getWritableDatabase();
		
		int deletedRows = 0;
		int uriType = sURIMatcher.match(uri);
		
		switch(uriType) {
		case WORD_ID:
			String wordToDelete = uri.getLastPathSegment();
			db.delete(WordTable.DATABASE_TABLE_WORD, WordTable.WORD_KEY_ID + "=" + wordToDelete, null);
			deletedRows++;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return deletedRows;
	}

	/**
	 * Verifies the correct set of columns to return data from when performing a query.
	 * 
	 * @param projection
	 * 						The set of columns about to be queried.
	 */
	private void checkColumns(String[] projection) {
		String[] available = { WordTable.WORD_KEY_ID, WordTable.WORD_KEY_TEXT, WordTable.WORD_KEY_IMAGE,
				WordTable.WORD_KEY_CATEGORY };
		
		if(projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			
			if(!availableColumns.containsAll(requestedColumns))	{
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
