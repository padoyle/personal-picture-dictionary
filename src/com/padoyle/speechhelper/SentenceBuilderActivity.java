package com.padoyle.speechhelper;

import java.util.Locale;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SentenceBuilderActivity extends FragmentActivity {

	private Button mAddButton;
	private Button mBackspaceButton;
	private TextView mSentenceView;
	
	private CategoryPickerFragment mCatPickFrag;
	private WordGridFragment mWordGridFrag;
	
	protected static final String CLEAR_SENTENCE = "CLEAR_EXISTING_SENTENCE";
	
	public static final String CATEGORY_SELECTED = "CATEGORY";
	public static final String SAVED_SENTENCE = "SAVED_SENTENCE";
	
	private static final int WIDE_LAYOUT = 0;
	private static final int CATEGORIES = 1;
	private static final int WORDS = 2;
	
	private int mActiveFragment = WIDE_LAYOUT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeCorrectView();
	}
	
	protected void initializeCorrectView() {
		setContentView(R.layout.activity_sentence_builder);
		
		this.mAddButton = (Button) findViewById(R.id.add_word_button);
		this.mBackspaceButton = (Button) findViewById(R.id.backspace_button);
		this.mSentenceView = (TextView) findViewById(R.id.sentence_viewer);
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String sentence = prefs.getString(SAVED_SENTENCE, "");
		this.mSentenceView.setText(sentence);
		
		if (findViewById(R.id.fragment_container) != null) {
			this.mCatPickFrag = new CategoryPickerFragment();
			this.mWordGridFrag = new WordGridFragment();
			this.mCatPickFrag.setArguments(getIntent().getExtras());
			FragmentManager fm = this.getSupportFragmentManager();
			if (fm.getFragments() != null)
				fm.beginTransaction().replace(R.id.fragment_container, mCatPickFrag).commit();
			else
				fm.beginTransaction().add(R.id.fragment_container, mCatPickFrag).commit();				
			this.mActiveFragment = CATEGORIES;
		}
		else {
			this.mCatPickFrag = (CategoryPickerFragment) this.getSupportFragmentManager().findFragmentById(R.id.category_fragment);
			this.mWordGridFrag = (WordGridFragment) this.getSupportFragmentManager().findFragmentById(R.id.words_fragment);			
			this.mActiveFragment = WIDE_LAYOUT;
		}

		initListeners();
	}
	
	protected void selectCategory(Category category) {
		if (findViewById(R.id.fragment_container) != null) {
			FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, mWordGridFrag);
			transaction.commit();
			this.mWordGridFrag.setCategory(category);
			this.mActiveFragment = WORDS;
		}
		else {
			this.mWordGridFrag.setCategoryAndRefresh(category);
		}
	}
	
	protected void returnToCategorySelector() {
		if (findViewById(R.id.fragment_container) != null) {
			FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, mCatPickFrag);
			transaction.commit();
			this.mActiveFragment = CATEGORIES;
		}		
	}
	
	private void initListeners() {
		this.mAddButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddWordDialog dialog = new AddWordDialog();
				dialog.show(getSupportFragmentManager(), "add_word_dialog");
			}
		});
		this.mBackspaceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sentence = mSentenceView.getText().toString();
				if (!sentence.isEmpty()) {
					int end = sentence.lastIndexOf('-') - 1;
					if (end > 0)
						sentence = sentence.substring(0, end);
					else 
						sentence = "";
					mSentenceView.setText(sentence);
				}
			}
		});
	}
	
	public void addWordToDictionary(Word word) {
		Uri createUri = Uri.parse(WordContentProvider.CONTENT_URI + "/word/" + word.getId());
		ContentValues cv = new ContentValues();
		cv.put(WordTable.WORD_KEY_TEXT, word.getText());
		cv.put(WordTable.WORD_KEY_IMAGE, word.getImagePath());
		cv.put(WordTable.WORD_KEY_CATEGORY, word.getCategory().toString());
		Uri idUri = this.getContentResolver().insert(createUri, cv);
		word.setId(Integer.parseInt(idUri.getLastPathSegment()));
		if (this.mActiveFragment == WORDS || this.mActiveFragment == WIDE_LAYOUT) {
			this.mWordGridFrag.fillData();
		}
	}
	
	public void removeWord(Word word) {
		long id = word.getId();
		Uri removeUri = Uri.parse(WordContentProvider.CONTENT_URI + "/word/" + id);
		this.getContentResolver().delete(removeUri, null, null);
		String filename = word.getImagePath();
		boolean deleted = this.deleteFile(filename);
		Log.d("SpeechHelper", "File deletion successful: " + deleted);
		if (this.mActiveFragment == WORDS || this.mActiveFragment == WIDE_LAYOUT) {
			this.mWordGridFrag.fillData();
		}
	}

	public void addWordToSentence(String text) {
		if ("".equals(mSentenceView.getText())) {
			mSentenceView.setText(text.substring(0,1).toUpperCase(Locale.US) + 
					text.substring(1,text.length()).toLowerCase(Locale.US));
		}
		else {
			mSentenceView.setText(mSentenceView.getText() + " - " + text.toLowerCase(Locale.US));
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString(SAVED_SENTENCE, this.mSentenceView.getText().toString());
		edit.commit();
	}
	
	@Override
	public void onBackPressed() {
		if (this.mActiveFragment == WORDS)
			returnToCategorySelector();
	}
}
