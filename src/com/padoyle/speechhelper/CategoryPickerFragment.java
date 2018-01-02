package com.padoyle.speechhelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class CategoryPickerFragment extends Fragment {

	private LinearLayout mViewParent;
	private CategoryAdapter mAdapter;
	private ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mViewParent = (LinearLayout) inflater.inflate(R.layout.category_picker_view, container, false);
		this.mListView = (ListView)mViewParent.findViewById(R.id.category_list);
		this.mAdapter = new CategoryAdapter(getActivity());
		this.mAdapter.notifyDataSetChanged();
		this.mListView.setAdapter(mAdapter);
		initListeners();

		return mViewParent;
	}
	
	private void initListeners() {
		this.mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CategoryView catView = (CategoryView)view;
				SentenceBuilderActivity activity = (SentenceBuilderActivity)getActivity();
				activity.selectCategory(catView.getCategory());
				catView.setSelected(true);
			}
		});
	}
}