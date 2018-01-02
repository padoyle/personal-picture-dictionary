package com.padoyle.speechhelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AddWordDialog extends DialogFragment {
	
	private EditText mWordEditText;
	private Spinner mCategorySpinner;
	private ImageButton mGalleryButton;
	private ImageButton mCameraButton;
	private Button mSubmitButton;
	
	private CategoryAdapter mCategoryAdapter;
	private Bitmap mImageHolder;
	
	private Uri mImageUri;
	private File mTempFile;
	
	private static final int SELECT_GALLERY_PICTURE = 1;
	private static final int TAKE_CAMERA_PICTURE = 2;
	
	public AddWordDialog() {
		this.mImageHolder = null;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_word_layout, container);
        this.mWordEditText = (EditText) view.findViewById(R.id.add_word_edit);
        this.mCategorySpinner = (Spinner) view.findViewById(R.id.add_category_spinner);
        this.mGalleryButton = (ImageButton) view.findViewById(R.id.add_gallery_button);
        this.mCameraButton = (ImageButton) view.findViewById(R.id.add_camera_button);
        this.mSubmitButton = (Button) view.findViewById(R.id.add_word_submit_button);
        
        this.getDialog().setTitle(getResources().getString(R.string.add_word_dialog_title));
        
        this.mCategoryAdapter = new CategoryAdapter(getActivity(), CategoryView.SIMPLE);
        this.mCategorySpinner.setAdapter(mCategoryAdapter);
     
        initListeners();
        
        return view;
    }
    
    private void initListeners() {
    	this.mSubmitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String wordText = mWordEditText.getText().toString();
				if (mImageHolder == null) {
					Toast.makeText(getActivity(), "No image selected!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if (wordText == null || wordText.isEmpty()) {
					Toast.makeText(getActivity(), "No word entered!", Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					FileOutputStream fos = getActivity().openFileOutput(wordText+".png", Context.MODE_PRIVATE);
					mImageHolder.compress(CompressFormat.PNG, 90, fos);
					fos.close();
				} catch (FileNotFoundException e) {
					Log.e("SpeechHelper", "File " + wordText + ".png not writable!");
				} catch (IOException e) {
					Log.e("SpeechHelper", "File " + wordText + ".png could not be closed!");
				}
				Word word = new Word(0, mWordEditText.getText().toString(), 
						wordText+".png", 
						(Category)mCategorySpinner.getSelectedItem());
				((SentenceBuilderActivity)getActivity()).addWordToDictionary(word);
				getDialog().dismiss();
			}
		});
    	this.mGalleryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_GALLERY_PICTURE);
			}
		});
    	this.mCameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mTempFile = new File(Environment.getExternalStorageDirectory(), "speechhelper_temp_image.jpg");
                mImageUri = Uri.fromFile(mTempFile);

                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageUri);
                intent.putExtra("return-data", true);

                startActivityForResult(intent, TAKE_CAMERA_PICTURE);
           }
		});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		String imagePath = null;
    		if (requestCode == SELECT_GALLERY_PICTURE) {
    			mImageUri = data.getData();
    			imagePath = getImagePath(mImageUri);
    		}
    		else {
                imagePath = mImageUri.getPath();
    		}
    		
			if (imagePath != null) {
				int width = getResources().getDimensionPixelSize(R.dimen.word_image_width);
				int height = getResources().getDimensionPixelSize(R.dimen.word_image_height);
				this.mImageHolder = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
				Bitmap fullImage = BitmapFactory.decodeFile(imagePath);
				fullImage = getTransformedBitmap(imagePath, fullImage, width, height);
				Canvas canvas = new Canvas(this.mImageHolder);
				Rect thumbSize = new Rect(0, 0, width, height);
				canvas.drawBitmap(fullImage, null, thumbSize, null);
				if (mTempFile != null)
					mTempFile.delete();
				fullImage = null;
			}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private String getImagePath(Uri uri) {
    	String[] projection = { MediaColumns.DATA };    	
    	Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
    	if (cursor != null) {
    		cursor.moveToFirst();
    		int col = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
    		String filePath = cursor.getString(col);
    		cursor.close();
    		return filePath;
    	}
    	return uri.getPath();
    }
    
    
    private Bitmap getTransformedBitmap(String path, Bitmap image, int width, int height) {
    	ExifInterface ei;
		try {
			ei = new ExifInterface(path);
	    	int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	    	int rotation = 0;

	    	switch(orientation) {
	    	    case ExifInterface.ORIENTATION_ROTATE_90:
	    	        rotation = 90;
	    	        break;
	    	    case ExifInterface.ORIENTATION_ROTATE_180:
	    	        rotation = 180;
	    	        break;
	    	    case ExifInterface.ORIENTATION_ROTATE_270:
	    	        rotation = 270;
	    	        break;
	    	    default:
	    	        rotation = 0;
	    	        break;
	    	}
	    	
	    	Matrix mat = new Matrix();
	    	mat.setRotate(rotation, image.getWidth()/2, image.getHeight()/2);
			Canvas canvas = new Canvas(this.mImageHolder);
			Rect thumbSize = new Rect(0, 0, width, height);
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			int offX = 0, offY = 0;
			if (imageWidth > imageHeight) {
				offX = (imageWidth - imageHeight)/2;
				imageWidth = imageHeight;
			}
			else if (imageHeight > imageWidth) {
				offY = (imageHeight - imageWidth)/2;
				imageHeight = imageWidth;
			}
			canvas.drawBitmap(Bitmap.createBitmap(image, offX, offY, imageWidth, imageHeight, mat, true),
					null, thumbSize, new Paint(Paint.ANTI_ALIAS_FLAG));
		} catch (IOException e) {
			Log.e("SpeechHelper", "File not found!");
		}
		return this.mImageHolder;
    }
}