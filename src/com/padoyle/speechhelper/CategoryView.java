package com.padoyle.speechhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CategoryView extends LinearLayout {

	private TextView mTextView;
	private Category mCategory;

	public static final int DETAILED = 1;
	public static final int SIMPLE = 2;

	public CategoryView(Context context, Category category) {
		this(context, category, SIMPLE);
	}
		
	public CategoryView(Context context, Category category, int type) {
		super(context);
		this.mCategory = category;
		if (type == DETAILED) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.category_view, this, true);
			this.mTextView = (TextView)findViewById(R.id.category_text);
		}
		else {
			this.mTextView = new TextView(context);
			this.mTextView.setTextSize(24.f);
			this.addView(this.mTextView);
		}
		initLayout();
	}
	
	public void initLayout() {
		this.mTextView.setText(mCategory.getName());
	}
	
	public Category getCategory() {
		return mCategory;
	}

	public void setCategory(Category item) {
		this.mTextView.setText(item.getName());
	}
}
