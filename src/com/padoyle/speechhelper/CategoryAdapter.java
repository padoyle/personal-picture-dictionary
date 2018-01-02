package com.padoyle.speechhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter implements SpinnerAdapter {
	
	private Context mContext;
	private int mType;
	
	public CategoryAdapter(Context context) {
		this.mContext = context;
		this.mType = CategoryView.DETAILED;
	}
	
	public CategoryAdapter(Context context, int type) {
		this.mContext = context;
		this.mType = type;
	}
	
	@Override
	public int getCount() {
		return Category.values().length;
	}

	@Override
	public Object getItem(int position) {
		Category[] categories = Category.values();
		if (position >= categories.length)
			return null;
		return categories[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CategoryView catView = null;
		
		if (convertView == null)
			catView = new CategoryView(this.mContext, (Category)getItem(position), this.mType);
		else {
			catView = (CategoryView)convertView;
			catView.setCategory((Category)getItem(position));
		}
		
		return catView;
	}

}
