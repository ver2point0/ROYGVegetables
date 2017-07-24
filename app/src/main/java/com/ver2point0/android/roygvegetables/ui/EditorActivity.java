package com.ver2point0.android.roygvegetables.ui;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ver2point0.android.roygvegetables.R;
import com.ver2point0.android.roygvegetables.data.VegetableContract.VegetableEntry;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_VEGETABLE_LOADER = 0;
    private static final int IMAGE_REQUEST_CODE = 1;
    private Uri mCurrentUri;

    private Bitmap mBitmap;
    private boolean mHasImage;

    // image view
    private ImageView mImageView;

    // edit texts
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;

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

        Button orderButton = (Button) findViewById(R.id.editor_order_button);

        if (mCurrentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_vegetable));
            invalidateOptionsMenu();
            orderButton.setEnabled(false);
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_vegetable));
            getLoaderManager().initLoader(EXISTING_VEGETABLE_LOADER, null, this);
        }

        mImageView = (ImageView) findViewById(R.id.edit_vegetable_image);
        Button imageButton = (Button) findViewById(R.id.editor_add_image_button);
        mNameEditText = (EditText) findViewById(R.id.edit_vegetable_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_vegetable_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_vegetable_quantity);
        mSupplierSpinner = (Spinner) findViewById(R.id.edit_vegetable_supplier);
        mHasImage = false;
        mBitmap = null;

        mImageView.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierSpinner.setOnTouchListener(mTouchListener);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent, IMAGE_REQUEST_CODE);
                }
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
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

    private void saveVegetable() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.name_required),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.price_required),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.quantity_required),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues editorValues = new ContentValues();
        if (mHasImage) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] imageByte = byteArrayOutputStream.toByteArray();
            editorValues.put(VegetableEntry.COLUMN_VEGETABLE_PHOTO, imageByte);
        } else {
            Toast.makeText(this, getString(R.string.photo_required),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        editorValues.put(VegetableEntry.COLUMN_VEGETABLE_NAME, nameString);
        editorValues.put(VegetableEntry.COLUMN_VEGETABLE_PRICE, Integer.parseInt(priceString));
        editorValues.put(VegetableEntry.COLUMN_VEGETABLE_QUANTITY, Integer.parseInt(quantityString));
        editorValues.put(VegetableEntry.COLUMN_VEGETABLE_SUPPLIER, mSupplier);


        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(VegetableEntry.CONTENT_URI, editorValues);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_veg_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_veg_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, editorValues, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_veg_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_veg_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    // setup the dropdown spinner that allows the user to select a supplier
    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSupplierSpinner.setAdapter(supplierSpinnerAdapter);

        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_royg))) {
                        mSupplier = VegetableEntry.SUPPLIER_ROYG; // ROY G
                    } else {
                        mSupplier = VegetableEntry.SUPPLIER_UNKNOWN; // unknown
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = 0; // unknown
            }
        });
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
                saveVegetable();
                // exit activity
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mVegetableHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mVegetableHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (mCurrentUri == null) {
            return null;
        }

        String[] projection = {
                VegetableEntry._ID,
                VegetableEntry.COLUMN_VEGETABLE_NAME,
                VegetableEntry.COLUMN_VEGETABLE_PHOTO,
                VegetableEntry.COLUMN_VEGETABLE_PRICE,
                VegetableEntry.COLUMN_VEGETABLE_QUANTITY,
                VegetableEntry.COLUMN_VEGETABLE_SUPPLIER
        };

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // find the columns of the veggie attributes we want
            int nameColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_NAME);
            int photoColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_PHOTO);
            int priceColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_SUPPLIER);

            // extract the values from the cursor for the given column indices
            String name = cursor.getString(nameColumnIndex);
            byte[] image = cursor.getBlob(photoColumnIndex);
            if (image != null) {
                mHasImage = true;
                mBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplier = cursor.getInt(supplierColumnIndex);

            // update views
            mNameEditText.setText(name);
            mImageView.setImageBitmap(mBitmap);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            switch (supplier) {
                case VegetableEntry.SUPPLIER_ROYG:
                    mSupplierSpinner.setSelection(1);
                    break;
                default:
                    mSupplierSpinner.setSelection(0);
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(
        DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteVegetable();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteVegetable() {
        getLoaderManager().destroyLoader(0);
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_veg_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_veg_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            mBitmap = (Bitmap) extras.get("data");
            mHasImage = true;
            mImageView.setImageBitmap(mBitmap);
        }
    }

} // end EditorActivity.java
