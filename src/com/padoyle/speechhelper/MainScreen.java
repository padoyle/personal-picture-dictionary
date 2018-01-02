package com.padoyle.speechhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainScreen extends Activity {

	private Button mSentenceBuilderButton;
	private Button mPhrasePickerButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.mSentenceBuilderButton = (Button)findViewById(R.id.sentence_builder_button);
		this.mPhrasePickerButton = (Button)findViewById(R.id.phrase_picker_button);
		
		initButtons();
	}
	
	public void initButtons() {
		this.mSentenceBuilderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, SentenceBuilderActivity.class);
				intent.putExtra(SentenceBuilderActivity.CLEAR_SENTENCE, true);
				MainScreen.this.finish();
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fullscreen, menu);
		return true;
	}

}
