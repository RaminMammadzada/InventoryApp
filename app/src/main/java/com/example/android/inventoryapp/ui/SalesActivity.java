package com.example.android.inventoryapp.ui;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.adapter.SaleCursorAdapter;

import com.example.android.inventoryapp.data.SaleContract.SaleEntry;


/**
 * Displays list of sales that were entered and stored in the app.
 */
public class SalesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the sale data loader */
    private static final int SALE_LOADER = 0;

    /** Adapter for the ListView */
    SaleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_sales );

        // Setup FAB to open AddEditSaleActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SalesActivity.this, AddEditSaleActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView saleListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        saleListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new SaleCursorAdapter(this, null);
        saleListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        saleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(SalesActivity.this, AddEditSaleActivity.class);

                // Form the content URI that represents the specific prpduct that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProductEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.inventoryapp/prodcuts/2"
                // if the pet with ID 2 was clicked on.
                Uri currentSaleUri = ContentUris.withAppendedId(SaleEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentSaleUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(SALE_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(SaleEntry.COLUMN_SALE_PRODUCT_NAME, "DA176");
        values.put(SaleEntry.COLUMN_SALE_PRICE, 88);
        values.put(SaleEntry.COLUMN_SALE_QUANTITY, 105);
        values.put(SaleEntry.COLUMN_SALE_SUPPLIER_NAME, SaleEntry.FOREX);
        values.put(SaleEntry.COLUMN_SALE_SUPPLIER_PHONE, "02128584782");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(SaleEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(SaleEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.go_to_store:
                Intent intentForProducts = new Intent( SalesActivity.this, ProductsActivity.class );
                startActivity( intentForProducts );
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.go_to_sales:
                Intent intentForSales = new Intent( SalesActivity.this, SalesActivity.class );
                startActivity( intentForSales );
                // Do nothing for now
                return true;
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {SaleEntry._ID,
                SaleEntry.COLUMN_SALE_PRODUCT_NAME,
                SaleEntry.COLUMN_SALE_PRICE,
                SaleEntry.COLUMN_SALE_QUANTITY };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                SaleEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}

