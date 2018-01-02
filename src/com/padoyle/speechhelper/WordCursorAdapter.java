package com.padoyle.speechhelper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.padoyle.speechhelper.WordView.OnWordChangeListener;

public class WordCursorAdapter extends CursorAdapter {

	/** An on-change listener that will be linked to each of the view objects
	 * that the adapter interacts with
	 */
	private OnWordChangeListener mListener;
	
	/**
	 * Constructor for an adapter that binds to a set of Words given by
	 * a cursor
	 * @param context The application Context 
	 * @param c The database Cursor pointing to the set of items to be displayed
	 * @param flags A list of options flags
	 */
	public WordCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}
	
	public void setOnWordChangeListener (OnWordChangeListener listener) {
		this.mListener = listener;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (!(view instanceof WordView))
			return;
		WordView wordView = (WordView)view;
		wordView.setOnWordChangeListener(null);
		wordView.setWord(new Word(cursor.getInt(WordTable.WORD_COL_ID),
				cursor.getString(WordTable.WORD_COL_TEXT),
				cursor.getString(WordTable.WORD_COL_IMAGE),
				Category.valueOf(cursor.getString(WordTable.WORD_COL_CATEGORY))));
		wordView.setOnWordChangeListener(mListener);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		WordView wordView = new WordView(context, new Word(cursor.getInt(WordTable.WORD_COL_ID),
				cursor.getString(WordTable.WORD_COL_TEXT),
				cursor.getString(WordTable.WORD_COL_IMAGE),
				Category.valueOf(cursor.getString(WordTable.WORD_COL_CATEGORY))));
		wordView.setOnWordChangeListener(mListener);
		return wordView;
	}
}
