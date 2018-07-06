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
import android.widget.Button;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.adapter.ProductCursorAdapter;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.example.android.inventoryapp.data.SaleContract.SaleEntry;


/**
 * Displays list of products that were entered and stored in the app.
 */
public class ProductsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the product data loader */
    private static final int PRODUCT_LOADER = 0;

    /** Adapter for the ListView */
    ProductCursorAdapter mCursorAdapter;

    Button saleButton;

    long mIdForProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_products );

        // Setup FAB to open AddEditProductActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductsActivity.this, AddEditProductActivity.class);
                startActivity(intent);

            }
        });

        // Find the ListView which will be populated with the pet data
        ListView productListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        //saleButton = findViewById( R.id.sale_button );

        // Setup the item click listener
        mCursorAdapter.setListener( new ProductCursorAdapter.OnProductInteractionListener() {
            @Override
            public void onItemClick(long id) {
                Toast.makeText( ProductsActivity.this, "Itema tikladin", Toast.LENGTH_SHORT ).show();

                Intent intent = new Intent(ProductsActivity.this, AddEditProductActivity.class);

                // Form the content URI that represents the specific product that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProductEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.inventoryapp/prodcuts/2"
                // if the product with ID 2 was clicked on.
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);
            }

            @Override
            public void onSaleButtonClick(long id) {
                Toast.makeText( ProductsActivity.this, "Sale buttona tikladin", Toast.LENGTH_SHORT ).show();

                Intent intent = new Intent(ProductsActivity.this, AddEditSaleActivity.class);

                // Form the content URI that represents the specific product that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProductEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.inventoryapp/prodcuts/2"
                // if the product with ID 2 was clicked on.
                Uri currentProductUri = ContentUris.withAppendedId( ProductEntry.CONTENT_URI, id);



                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);

                intent.putExtra("caller", "ProductsActivity");

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);

            }
        } );

        // Kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "FC176");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 60);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 445);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, ProductEntry.FORSCLASS);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, "021258439805");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
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
                Intent intentForProducts = new Intent( ProductsActivity.this, ProductsActivity.class );
                startActivity( intentForProducts );
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.go_to_sales:
                Intent intentForSales = new Intent( ProductsActivity.this, SalesActivity.class );
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
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ProductEntry.CONTENT_URI,   // Provider content URI to query
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
