package com.ver2point0.android.roygvegetables.ui;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.ver2point0.android.roygvegetables.R;
import com.ver2point0.android.roygvegetables.data.VegetableContract.VegetableEntry;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_VEGETABLE_LOADER = 0;
    private static final int IMAGE_REQUEST_CODE = 1;
    private Uri mCurrentUri;

    // image view
    private ImageView mImageView;

    // edit texts
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;

    // buttons
    private Button mImageButton;
    private Button mOrderButton;

    // spinner
    private Spinner mSupplierSpinner;
    private int mSupplier = VegetableEntry.SUPPLIER_UNKNOWN;

    private boolean mVegetableHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mVegetableHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        mOrderButton = (Button) findViewById(R.id.editor_order_button);

        if (mCurrentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_vegetable));
            invalidateOptionsMenu();
            mOrderButton.setEnabled(false);
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_vegetable));
            getLoaderManager().initLoader(EXISTING_VEGETABLE_LOADER, null, this);
        }

        mImageView = (ImageView) findViewById(R.id.edit_vegetable_image);
        mImageButton = (Button) findViewById(R.id.editor_add_image_button);
        mNameEditText = (EditText) findViewById(R.id.edit_vegetable_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_vegetable_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_vegetable_quantity);
        mSupplierSpinner = (Spinner) findViewById(R.id.edit_vegetable_supplier);

        mImageView.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierSpinner.setOnTouchListener(mTouchListener);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent, IMAGE_REQUEST_CODE);
                }
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameEmail = mNameEditText.getText().toString().trim();

                String emailMessage = getString(R.string.email_message) + "\n" + nameEmail;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:roy@royg.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage);

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                }
            }
        });

        setupSpinner();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE && requestCode == Activity.RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(image);
        }
    }

    // setup the dropdown spinner that allows the user to select a supplier
    private void setupSpinner() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // ADD CODE to saveVegetable();
                // exit activity
                finish();
                return true;
            case R.id.action_delete:
                // ADD CODE to showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // ADD CODE to get to parent activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
