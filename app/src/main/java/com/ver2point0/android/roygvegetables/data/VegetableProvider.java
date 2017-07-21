package com.ver2point0.android.roygvegetables.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ver2point0.android.roygvegetables.data.VegetableContract.VegetableEntry;

public class VegetableProvider extends ContentProvider {

    private static final String LOG_TAG = VegetableProvider.class.getName();

    // URI matcher code for the entire table
    private static final int VEGETABLES = 100;
    // URI matcher code for a single entry
    private static final int VEGETABLE_ID = 101;

    private VegetableDbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(VegetableContract.CONTENT_AUTHORITY,
                VegetableContract.PATH_VEGETABLES, VEGETABLES);

        sUriMatcher.addURI(VegetableContract.CONTENT_AUTHORITY,
                VegetableContract.PATH_VEGETABLES + "/#", VEGETABLE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new VegetableDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case VEGETABLES:
                cursor = db.query(VegetableEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case VEGETABLE_ID:
                selection = VegetableEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(VegetableEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VEGETABLES:
                return VegetableEntry.CONTENT_LIST_TYPE;
            case VEGETABLE_ID:
                return VegetableEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VEGETABLES:
                return insertVegetable(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertVegetable(Uri uri, ContentValues values) {
        String name = values.getAsString(VegetableEntry.VEGETABLE_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Vegetable requires a name");
        }

        Integer price = values.getAsInteger(VegetableEntry.VEGETABLE_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Vegetable price must be greater than 0");
        }

        Integer quantity = values.getAsInteger(VegetableEntry.VEGETABLE_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Vegetable quantity must be greater than 0");
        }

        Integer supplier = values.getAsInteger(VegetableEntry.VEGETABLE_SUPPLIER);
        if (supplier == null || !VegetableEntry.isValidSupplier(supplier)) {
            throw new IllegalArgumentException("Vegetable requires valid supplier");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(VegetableEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VEGETABLES:
                rowsDeleted = db.delete(VegetableEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VEGETABLE_ID:
                selection = VegetableEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(VegetableEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not support for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VEGETABLES:
                return updateVegetable(uri, values, selection, selectionArgs);
            case VEGETABLE_ID:
                selection = VegetableEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateVegetable(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateVegetable(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(VegetableEntry.VEGETABLE_NAME)) {
            String name = values.getAsString(VegetableEntry.VEGETABLE_NAME);
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("Vegetable requires a name");
            }
        }

        if (values.containsKey(VegetableEntry.VEGETABLE_PRICE)) {
            Integer price = values.getAsInteger(VegetableEntry.VEGETABLE_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Vegetable price must be greater than 0");
            }
        }

        if (values.containsKey(VegetableEntry.VEGETABLE_QUANTITY)) {
            Integer quantity = values.getAsInteger(VegetableEntry.VEGETABLE_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Vegetable quantity must be greater than 0");
            }
        }

        if (values.containsKey(VegetableEntry.VEGETABLE_SUPPLIER)) {
            Integer supplier = values.getAsInteger(VegetableEntry.VEGETABLE_SUPPLIER);
            if (supplier == null || !VegetableEntry.isValidSupplier(supplier)) {
                throw new IllegalArgumentException("Vegetable requires valid supplier");
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(VegetableEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

}