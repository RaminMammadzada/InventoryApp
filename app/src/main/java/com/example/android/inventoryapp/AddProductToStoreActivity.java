package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Trace;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.data.StoreContract.ProductEntry;
import com.example.android.inventoryapp.data.SalesAndStoreDbHelper;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class AddProductToStoreActivity extends AppCompatActivity {

    /**
     * EditText field to enter the product's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the product's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mSupplierNameSpinner;

    /**
     * EditText field to enter the supplier's phone number
     */
    private EditText mSupplierPhone;

    /**
     * Supplier name. The possible valid values are in the StoreContract.java file:
     * The only possible values are {KAMUEL}, {WALKAIR},
     * {NIKE}, {FOREX}, {FORSCLASS},
     * or {DEPEDRO}.
     *
     * Type: TEXT
     */
    public int mSupplierName = ProductEntry.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_product_to_store );

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById( R.id.add_name );
        mPriceEditText = (EditText) findViewById( R.id.add_price);
        mQuantityEditText = (EditText) findViewById( R.id.add_quantity );
        mSupplierNameSpinner = (Spinner) findViewById( R.id.spinner_supplier );
        mSupplierPhone = (EditText) findViewById( R.id.add_supplier_phone );

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource( this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item );

        // Specify dropdown layout style - simple list view with 1 item per line
        supplierSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_dropdown_item_1line );

        // Apply the adapter to the spinner
        mSupplierNameSpinner.setAdapter( supplierSpinnerAdapter );

        // Set the integer mSelected to the constant values
        mSupplierNameSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition( position );

                if (!TextUtils.isEmpty( selection )) {
                    if (selection.equals( getString( R.string.supplier_KAMUEL ) )) {
                        mSupplierName = ProductEntry.KAMUEL;
                    } else if (selection.equals( getString( R.string.supplier_DEPEDRO ) )) {
                        mSupplierName = ProductEntry.DEPEDRO;
                    } else if(selection.equals( getString( R.string.supplier_FOREX ) )){
                        mSupplierName = ProductEntry.FOREX;
                    } else if (selection.equals( getString( R.string.supplier_FORSCLASS ) )) {
                        mSupplierName = ProductEntry.FORSCLASS;
                    } else if(selection.equals( getString( R.string.supplier_NIKE ) )){
                        mSupplierName = ProductEntry.NIKE;
                    } else if (selection.equals( getString( R.string.supplier_WALKAIR ) )) {
                        mSupplierName = ProductEntry.WALKAIR;
                    }
                }

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplierName = ProductEntry.UNKNOWN;
            }
        } );
    }

    /**
     * Get user input from editor and save new product into database.
     */
    private Boolean insertProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhone.getText().toString().trim();
        int quantity = 0;
        int price = 0;

        Boolean isThereMissingField = false;

        // checking if there any blank editText or not
        if(TextUtils.isEmpty(mNameEditText.getText()) ){ mNameEditText.setError( "Name is required!" ); isThereMissingField = true;}
        else if (TextUtils.isEmpty(mPriceEditText.getText())) { mPriceEditText.setError( "Price is required!" ); isThereMissingField = true;}
        else if (TextUtils.isEmpty(mQuantityEditText.getText())) { mQuantityEditText.setError( "Quantity is required!" ); isThereMissingField = true;}
        else if (TextUtils.isEmpty(mSupplierPhone.getText())) {mSupplierPhone.setError( "Phone is required" ); isThereMissingField = true;}

        if(isThereMissingField) return false;

        // To be sure that editText fields are filled
        if(!TextUtils.isEmpty(mQuantityEditText.getText())) {quantity = Integer.parseInt( quantityString ); }
        if(!TextUtils.isEmpty(mPriceEditText.getText())) {price = Integer.parseInt( priceString ); }

        // Create database helper
        SalesAndStoreDbHelper mDbHelper = new SalesAndStoreDbHelper( this );

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put( ProductEntry.COLUMN_PRODUCT_NAME, nameString );
        values.put( ProductEntry.COLUMN_PRODUCT_PRICE, price );
        values.put( ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity );
        values.put( ProductEntry.COLUMN_SUPPLIER_NAME, mSupplierName );
        values.put( ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString );

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert( ProductEntry.TABLE_NAME, null, values );

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText( this, "Error with saving product", Toast.LENGTH_SHORT ).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText( this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT ).show();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_add_item, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                if (!insertProduct()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask( this );
                return true;
        }
        return super.onOptionsItemSelected( item );
    }
}