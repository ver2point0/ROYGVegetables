package com.ver2point0.android.roygvegetables.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class VegetableContract {

    // to prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private VegetableContract() {}

    public static final String CONTENT_AUTHORITY = "com.ver2point0.android.roygvegetables";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_VEGETABLES = "vegetables";

    // inner class to define the table contents
    public static class VegetableEntry implements BaseColumns {

        // CONTENT_URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEGETABLES);

        // CONTENT_LIST_TYPE
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEGETABLES;

        // CONTENT_ITEM_TYPE
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEGETABLES;

        // table name
        public static final String TABLE_NAME = "vegetables";
        // column names
        public static final String _ID = BaseColumns._ID;
        public static final String VEGETABLE_NAME = "name";
        public static final String VEGETABLE_PHOTO = "photo";
        public static final String VEGETABLE_PRICE = "price";
        public static final String VEGETABLE_QUANTITY = "quantity";
        public static final String VEGETABLE_SUPPLIER = "supplier";
        public static final String VEGETABLE_SUPPLIER_EMAIL = "supplierEmail";

        // possible vegetable suppliers
        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_ROYG = 1;
        public static final int SUPPLIER_ACME = 2;
    }
}
