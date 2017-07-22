package com.ver2point0.android.roygvegetables.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ver2point0.android.roygvegetables.data.VegetableContract.VegetableEntry;

public class VegetableDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = VegetableDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "basket.db";
    private static final int DATABASE_VERSION = 1;

    public VegetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_VEGETABLES_TABLE =
                "CREATE TABLE " + VegetableEntry.TABLE_NAME + "( " +
                VegetableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VegetableEntry.VEGETABLE_NAME + " TEXT NOT NULL, " +
                VegetableEntry.VEGETABLE_PHOTO + " BLOB, " +
                VegetableEntry.VEGETABLE_PRICE + " INTEGER NOT NULL, " +
                VegetableEntry.VEGETABLE_QUANTITY + " INTEGER NOT NULL, " +
                VegetableEntry.VEGETABLE_SUPPLIER + " INTEGER NOT NULL DEFAULT 0, " +
                VegetableEntry.VEGETABLE_SUPPLIER_EMAIL + " TEXT );";

    Log.i(LOG_TAG, SQL_CREATE_VEGETABLES_TABLE);
        db.execSQL(SQL_CREATE_VEGETABLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + VegetableEntry.TABLE_NAME);
        onCreate(db);
    }
}
