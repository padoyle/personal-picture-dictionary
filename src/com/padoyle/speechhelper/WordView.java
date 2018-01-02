package com.padoyle.speechhelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WordView extends LinearLayout {

	/** Reference to the ImageView contained in this View */
	private ImageView mImageView;

	/** Reference to the TextView contained in this View */
	private TextView mTextView;
	
	/** The Word object to be displayed by this view */
	private Word mWord;
	
	/** The on-change listener for use when recycling Views */
	private OnWordChangeListener mListener;
	
	private Context mContext;

	public WordView(Context context, Word word) {
		super(context);
		this.mContext = context;
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.word_view, this, true);
		int width = getResources().getDimensionPixelSize(R.dimen.word_image_width);
		int height = getResources().getDimensionPixelSize(R.dimen.word_image_height);
		this.setLayoutParams(new GridView.LayoutParams(width, height));
		this.mImageView = (ImageView)findViewById(R.id.word_image);
		this.mTextView = (TextView)findViewById(R.id.word_text);
	}
	
	/**
	 * Mutator for the contained Word object
	 * @param word The new Word object to use
	 */
	public void setWord(Word word) {
		this.mWord = word;
		try {
			if (word.getImagePath().matches("^\\d+$"))
				this.mImageView.setImageResource(Integer.valueOf(word.getImagePath()));
			else {
				FileInputStream fis = mContext.openFileInput(word.getImagePath());
				Bitmap image = BitmapFactory.decodeStream(fis);
				if (image != null) {
					this.mImageView.setImageBitmap(image);
				}
				else 
					this.mImageView.setImageResource(R.drawable.category_item_arrow);
			}
		}
		catch (Resources.NotFoundException ex) {
			Log.d("SpeechHelper","Image resource not found");
			this.mImageView.setImageResource(R.drawable.ic_launcher);
		} catch (FileNotFoundException e) {
			Log.d("SpeechHelper","Image resource not found");
			this.mImageView.setImageResource(R.drawable.ic_launcher);
		}
		this.mTextView.setText(mWord.getText());
	}
	
	/**
	 * Accessor to the displayed Word object
	 * @return The displayed Word object
	 */
	public Word getWord() {
		return this.mWord;
	}
	
	/**
	 * Change the listener that's called when a Word object changes
	 * @param listener The new listener to use
	 */
	public void setOnWordChangeListener(OnWordChangeListener listener) {
		this.mListener = listener;
	}
	
	protected void notifyOnWordChangeListener() {
		if (this.mListener != null)
			this.mListener.onWordChanged(this, mWord);
	}
	
	public static interface OnWordChangeListener {
		
		/**
		 * Called when a view changes its contents.  For us, this should only be
		 * whenever a view is recycled.
		 * @param view The View whose contained Word has changed
		 * @param word The Word that has changed
		 */
		public void onWordChanged(WordView view, Word word);
	}

}
