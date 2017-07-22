package com.ver2point0.android.roygvegetables.ui;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ver2point0.android.roygvegetables.R;
import com.ver2point0.android.roygvegetables.adapter.VegetableCursorAdapter;
import com.ver2point0.android.roygvegetables.data.VegetableContract;
import com.ver2point0.android.roygvegetables.data.VegetableContract.VegetableEntry;

import java.io.ByteArrayOutputStream;

public class BasketActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int VEGETABLE_LOADER = 0;

    VegetableCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fabIntent = new Intent(BasketActivity.this, EditorActivity.class);
                startActivity(fabIntent);
            }
        });

        ListView vegetableListView = (ListView) findViewById(R.id.list_view);

        View emptyView = findViewById(R.id.empty_view);
        vegetableListView.setEmptyView(emptyView);

        mCursorAdapter = new VegetableCursorAdapter(this, null);
        vegetableListView.setAdapter(mCursorAdapter);

        vegetableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BasketActivity.this, EditorActivity.class);
                Uri currentVegetableUri = ContentUris.withAppendedId(VegetableContract.VegetableEntry.CONTENT_URI, id);
                intent.setData(currentVegetableUri);
                startActivity(intent);
            }
        });

        // start loader
        getLoaderManager().initLoader(VEGETABLE_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void insertVegetable() {
        insertIntoDatabase("Broccoli", convertToByte(getDrawable(R.drawable.broccoli)), 2, 20, VegetableEntry.SUPPLIER_ACME);
        insertIntoDatabase("Carrot", convertToByte(getDrawable(R.drawable.carrot)), 4, 40, VegetableEntry.SUPPLIER_ROYG);
        insertIntoDatabase("Corn", convertToByte(getDrawable(R.drawable.corn)), 6, 60, VegetableEntry.SUPPLIER_UNKNOWN);
        insertIntoDatabase("Lettuce", convertToByte(getDrawable(R.drawable.lettuce)), 8, 80, VegetableEntry.SUPPLIER_ACME);
        insertIntoDatabase("Onion", convertToByte(getDrawable(R.drawable.onion)), 12, 120, VegetableEntry.SUPPLIER_ROYG);
        insertIntoDatabase("Potato", convertToByte(getDrawable(R.drawable.potato)), 14, 140, VegetableEntry.SUPPLIER_UNKNOWN);

    }

    private byte[] convertToByte(Drawable drawable) {
        // convert to bitmap
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();

        // convert to byte to store
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void insertIntoDatabase(
            String name,
            byte[] photo,
            int price,
            int quantity,
            int supplier) {

        ContentValues values = new ContentValues();
        values.put(VegetableEntry.VEGETABLE_NAME, name);
        values.put(VegetableEntry.VEGETABLE_PHOTO, photo);
        values.put(VegetableEntry.VEGETABLE_PRICE, price);
        values.put(VegetableEntry.VEGETABLE_QUANTITY, quantity);
        values.put(VegetableEntry.VEGETABLE_SUPPLIER, supplier);
        getContentResolver().insert(VegetableEntry.CONTENT_URI, values);
    }

    private void deleteAllVegetables() {
        int rowsDeleted = getContentResolver().delete(VegetableEntry.CONTENT_URI, null, null);
        Log.v("BasketActivity", rowsDeleted + " rows deleted from vegetable database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertVegetable();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllVegetables();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                VegetableEntry._ID,
                VegetableEntry.VEGETABLE_NAME,
                VegetableEntry.VEGETABLE_PHOTO,
                VegetableEntry.VEGETABLE_PRICE,
                VegetableEntry.VEGETABLE_QUANTITY
        };

        return new CursorLoader(
                this,
                VegetableEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
