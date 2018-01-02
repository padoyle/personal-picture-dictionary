package com.padoyle.speechhelper;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.padoyle.speechhelper.WordView.OnWordChangeListener;


public class WordGridFragment extends Fragment implements LoaderCallbacks<Cursor>,
	OnWordChangeListener {

	/** Reference to the GridView object displayed */
	private GridView mGridView;
	
	/** Reference to the heading TextView */
	private TextView mHeading;
	
	/** A cursor adapter that links to the data that's displayed */
	private WordCursorAdapter mAdapter;
	
	/** Stores the currently selected category */
	private Category mSelectedCategory;
	
	/** The ID of the CursorLoader to be initialized in the LoaderManager and used to load a Cursor. */
	private static final int LOADER_ID = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.word_grid_view, container, false);
		
		this.mGridView = (GridView) parent.findViewById(R.id.grid_view);
		this.mHeading = (TextView) parent.findViewById(R.id.category_heading);
		if (this.mSelectedCategory == null)
			this.mSelectedCategory = Category.PEOPLE;
		
		this.mHeading.setText(this.mSelectedCategory.getName());
		
		return parent;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.mAdapter = new WordCursorAdapter(getActivity(), null, 0);
		
		if (this.mSelectedCategory == null)
			this.mSelectedCategory = Category.PEOPLE;
		
		this.mAdapter.setOnWordChangeListener(this);
		this.mGridView.setAdapter(mAdapter);
		
		initListeners();
		fillData();
	}
	
	protected void initListeners() {
		this.mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				WordView wordView = (WordView) view;
				((SentenceBuilderActivity) getActivity()).addWordToSentence(wordView.getWord().getText());
			}
		});
		this.mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
				final Word word = ((WordView)view).getWord();
				AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
				dialog.setTitle("Remove word \"" + word.getText() + "\"?");
				dialog.setPositiveButton("Yes", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						((SentenceBuilderActivity)getActivity()).removeWord(word);
					}
				});
				dialog.setNegativeButton("No", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
				return true;
			}
		});
	}

	protected void fillData() {
		getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
		mGridView.setAdapter(mAdapter);
	}
	
	@Override
	public void onWordChanged(WordView view, Word word) {
		Uri updateUri = Uri.parse(WordContentProvider.CONTENT_URI + "/word/" + word.getId());
		ContentValues cv = new ContentValues();
		cv.put(WordTable.WORD_KEY_TEXT, word.getText());
		cv.put(WordTable.WORD_KEY_IMAGE, word.getImagePath());
		cv.put(WordTable.WORD_KEY_CATEGORY, word.getCategory().toString());
		getActivity().getContentResolver().update(updateUri, cv, null, null);
		mAdapter.setOnWordChangeListener(null);
		fillData();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String projection[] = {
				WordTable.WORD_KEY_ID,
				WordTable.WORD_KEY_TEXT,
				WordTable.WORD_KEY_IMAGE,
				WordTable.WORD_KEY_CATEGORY
		};
		
		Uri uri = Uri.parse(WordContentProvider.CONTENT_URI + "/category/" + this.mSelectedCategory.toString());
		Loader<Cursor> loader = new CursorLoader(getActivity(), uri, projection, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		mAdapter.setOnWordChangeListener(this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	public void setCategory(Category category) {
		this.mSelectedCategory = category;
	}
	
	public void setCategoryAndRefresh(Category category) {
		setCategory(category);
		this.mHeading.setText(this.mSelectedCategory.getName());
		fillData();
	}
}