package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;

import com.example.android.inventoryapp.data.SalesAndStoreDbHelper;

import com.example.android.inventoryapp.data.StoreContract;
import com.example.android.inventoryapp.data.StoreContract.ProductEntry;
import com.example.android.inventoryapp.data.SaleContract.SaleEntry;


/**
 * Displays list of products that were entered and stored in the app.
 */
public class SalesActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private SalesAndStoreDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sales );

        // Setup FAB to open AddProductToStoreActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( SalesActivity.this, AddSaleToSalesActivity.class );
                startActivity( intent );
            }
        } );

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new SalesAndStoreDbHelper( this );
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                SaleEntry.SALE_ID,
                SaleEntry.COLUMN_SALE_PRODUCT_NAME,
                SaleEntry.COLUMN_SALE_PRICE,
                SaleEntry.COLUMN_SALE_QUANTITY,
                SaleEntry.COLUMN_SALE_SUPPLIER_NAME};

        // Perform a query on the sales table
        Cursor cursor = db.query(
                SaleEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null );                   // The sort order

        TextView displayView = (TextView) findViewById( R.id.text_view_pet );

        try {
            // Create a header in the Text View that looks like this:
            //
            // The sales table contains <number of rows in Cursor> pets.
            // product_id - sale_product_name - sale_price - sale_quantity - supplier_name
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText( "The sales table contains " + cursor.getCount() + " sales.\n\n" );
            displayView.append(
                    SaleEntry.SALE_ID + " - " +
                            SaleEntry.COLUMN_SALE_PRODUCT_NAME + " - " +
                            SaleEntry.COLUMN_SALE_PRICE + " - " +
                            SaleEntry.COLUMN_SALE_QUANTITY + " - " +
                            SaleEntry.COLUMN_SALE_SUPPLIER_NAME +  "\n" );

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex( SaleEntry.SALE_ID );
            int nameColumnIndex = cursor.getColumnIndex( SaleEntry.COLUMN_SALE_PRODUCT_NAME );
            int priceColumnIndex = cursor.getColumnIndex( SaleEntry.COLUMN_SALE_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex( SaleEntry.COLUMN_SALE_QUANTITY );
            int supplierNameColumnIndex = cursor.getColumnIndex( SaleEntry.COLUMN_SALE_SUPPLIER_NAME );

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt( idColumnIndex );
                String currentName = cursor.getString( nameColumnIndex );
                int currentPrice = cursor.getInt( priceColumnIndex );
                int currentQuantity = cursor.getInt( quantityColumnIndex );
                int currentSupplier = cursor.getInt( supplierNameColumnIndex );
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append( ("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier ) );
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_all.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_all, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.go_to_store:
                Intent intent = new Intent( SalesActivity.this, StoreActivity.class );
                startActivity( intent );
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.go_to_sales:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected( item );
    }
}

