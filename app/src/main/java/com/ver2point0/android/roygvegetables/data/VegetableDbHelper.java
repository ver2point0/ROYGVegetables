package com.ver2point0.android.roygvegetables.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ver2point0.android.roygvegetables.data.VegetableContract.VegetableEntry;

public class VegetableDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "basket.db";
    private static final int DATABASE_VERSION = 1;

    public VegetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_VEGETABLES_TABLE =
                "CREATE TABLE" + VegetableEntry.TABLE_NAME + " (" +
                VegetableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMNT, " +
                VegetableEntry.VEGETABLE_NAME + " TEXT NOT NULL, " +
                VegetableEntry.VEGETABLE_PHOTO + " BLOG, " +
                VegetableEntry.VEGETABLE_PRICE + " INTEGER NOT NULL, " +
                VegetableEntry.VEGETABLE_QUANTITY + " INTEGER NOT NULL, " +
                VegetableEntry.VEGETABLE_SUPPLIER + " INTEGER NOT NULL DEFAULT 0, " +
                VegetableEntry.VEGETABLE_SUPPLIER_EMAIL + " TEXT );";
        db.execSQL(SQL_CREATE_VEGETABLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
