package com.example.android.inventoryapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.SaleContract.SaleEntry;

/**
 * {@link SaleCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class SaleCursorAdapter  extends CursorAdapter{


    /**
     * Constructs a new {@link SaleCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public SaleCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate( R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex( SaleEntry.COLUMN_SALE_PRODUCT_NAME);
        int supplierColumnIndex = cursor.getColumnIndex( SaleEntry.COLUMN_SALE_SUPPLIER_NAME);

        // Read the sale attributes from the Cursor for the current sale
        String saleSaleName = cursor.getString(nameColumnIndex);
        int saleSupplierName = cursor.getInt(supplierColumnIndex);

        String saleSupplierNameString = "";
        switch (saleSupplierName) {
            case SaleEntry.UNKNOWN:
                saleSupplierNameString = "UNKNOWN";
                break;
            case SaleEntry.KAMUEL:
                saleSupplierNameString = "KAMUEL";
                break;
            case SaleEntry.WALKAIR:
                saleSupplierNameString = "WALKAIR";
                break;
            case SaleEntry.DEPEDRO:
                saleSupplierNameString = "DEPEDRO";
                break;
            case SaleEntry.NIKE:
                saleSupplierNameString = "NIKE";
                break;
            case SaleEntry.FOREX:
                saleSupplierNameString = "FOREX";
                break;
            case SaleEntry.FORSCLASS:
                saleSupplierNameString = "FORSCLASS";
                break;
        }

        // If the sale breed is empty string or null, then use some default text
        // that says "Unknown Supplier", so the TextView isn't blank.
        if (TextUtils.isEmpty(saleSupplierNameString)) {
            saleSupplierNameString = "Unkown Supplier";
        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(saleSaleName);
        summaryTextView.setText(saleSupplierName);
    }
}
