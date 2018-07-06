package com.example.android.inventoryapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.renderscript.Sampler;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    OnProductInteractionListener listener;

    public OnProductInteractionListener getListener() {
        return listener;
    }

    public void setListener(OnProductInteractionListener listener) {
        this.listener = listener;
    }

    /**
     * Constructs a new {@link SaleCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super( context, c, 0 /* flags */ );
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
        return LayoutInflater.from( context ).inflate( R.layout.list_item, parent, false );
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
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
        TextView nameTextView = (TextView) view.findViewById( R.id.name );
        TextView priceTextView = (TextView) view.findViewById( R.id.price );
        TextView quantityTextView = (TextView) view.findViewById( R.id.quantity );

        // Find the columns of product attributes that we're interested in
        int idIndex = cursor.getColumnIndex( ProductEntry._ID );
        int nameColumnIndex = cursor.getColumnIndex( ProductEntry.COLUMN_PRODUCT_NAME );
        int priceColumnIndex = cursor.getColumnIndex( ProductEntry.COLUMN_PRODUCT_PRICE );
        int quantityColumnIndex = cursor.getColumnIndex( ProductEntry.COLUMN_PRODUCT_QUANTITY );

        // Read the product attributes from the Cursor for the current product
        final long id = cursor.getLong( idIndex );
        String productProductName = cursor.getString( nameColumnIndex );
        int productProductPrice = cursor.getInt( priceColumnIndex );
        int productProductQuantity = cursor.getInt( quantityColumnIndex );


        // Update the TextViews with the attributes for the current product
        nameTextView.setText( productProductName );
        priceTextView.setText( String.valueOf( productProductPrice ) );
        quantityTextView.setText( String.valueOf( productProductQuantity ) );

        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListener().onItemClick( id );
            }
        } );

        Button saleButton = view.findViewById( R.id.sale_button );
        saleButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListener().onSaleButtonClick( id );
            }
        } );

        Button orderButton = view.findViewById( R.id.order_button );
        orderButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListener().onOrderButtonClick( id );
            }
        } );

    }

    public interface OnProductInteractionListener {
        void onItemClick(long id);

        void onSaleButtonClick(long id);

        void onOrderButtonClick(long id);
    }
}
