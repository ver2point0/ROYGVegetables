package com.ver2point0.android.roygvegetables.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ver2point0.android.roygvegetables.R;
import com.ver2point0.android.roygvegetables.data.VegetableContract.VegetableEntry;

public class VegetableCursorAdapter extends CursorAdapter {

    public VegetableCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // find individual views
        ImageView vegetableImage = (ImageView) view.findViewById(R.id.list_item_vegetable_image_view);
        TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);
        ImageView saleImage = (ImageView) view.findViewById(R.id.list_item_sale_image_view);

        // find columns of vegetable attributes needed
        int idColumnIndex = cursor.getColumnIndex(VegetableEntry._ID);
        int photoColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_PHOTO);
        int nameColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_NAME);
        int priceColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(VegetableEntry.COLUMN_VEGETABLE_QUANTITY);

        // read vegetable attributes from the cursor for current vegetable
        final int vegetableId = cursor.getInt(idColumnIndex);
        byte[] vegetablePhoto = cursor.getBlob(photoColumnIndex);
        String vegetableName = cursor.getString(nameColumnIndex);
        int vegetablePrice = cursor.getInt(priceColumnIndex);
        final int vegetableQuantity = cursor.getInt(quantityColumnIndex);

        // update views with the attributes for the current vegetable
        Bitmap vegetableBitmap = BitmapFactory.decodeByteArray(vegetablePhoto, 0, vegetablePhoto.length);
        vegetableImage.setImageBitmap(vegetableBitmap);
        nameTextView.setText(vegetableName);
        priceTextView.setText("$" + Integer.toString(vegetablePrice));
        quantityTextView.setText(Integer.toString(vegetableQuantity));

        saleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentVegetableUri = ContentUris.withAppendedId(VegetableEntry.CONTENT_URI, vegetableId);
                makeSale(context, vegetableQuantity, currentVegetableUri);
            }
        });
    }

    private void makeSale(Context context, int vegQuantity, Uri uriVegetable) {
        if (vegQuantity == 0) {
            Toast.makeText(context, R.string.no_vegetables_to_sell, Toast.LENGTH_SHORT).show();
        } else {
            int newQuantity = vegQuantity - 1;
            ContentValues values = new ContentValues();
            values.put(VegetableEntry.COLUMN_VEGETABLE_QUANTITY, newQuantity);
            context.getContentResolver().update(uriVegetable, values, null, null);
        }
    }
}
