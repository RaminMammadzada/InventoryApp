package com.example.android.inventoryapp.ui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.LoaderManager;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.SaleContract.SaleEntry;

/**
 * Allows user to create a new sale or edit an existing one.
 */
public class AddEditSaleActivity extends AppCompatActivity implements 
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the pet data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentSaleUri;

    /**
     * EditText field to enter the sale's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the sale's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the sale's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mSupplierNameSpinner;

    /**
     * Supplier name. The possible valid values are in the SaleContract.java file:
     * The only possible values are {KAMUEL}, {WALKAIR},
     * {NIKE}, {FOREX}, {FORSCLASS},
     * or {DEPEDRO}.
     *
     * Type: TEXT
     */
    public int mSupplierName = SaleEntry.UNKNOWN;

    /** Boolean flag that keeps track of whether the sale has been edited (true) or not (false) */
    private boolean mSaleHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mSaleHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSaleHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_sale_to_sales );

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentSaleUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentSaleUri == null) {
            // This is a new pet, so change the app bar to say "Add a Sale"
            setTitle(getString(R.string.editor_activity_title_new_sale));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Sale"
            setTitle(getString(R.string.editor_activity_title_edit_sale));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById( R.id.add_name );
        mPriceEditText = (EditText) findViewById( R.id.add_price);
        mQuantityEditText = (EditText) findViewById( R.id.add_quantity );
        mSupplierNameSpinner = (Spinner) findViewById( R.id.spinner_supplier );

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameSpinner.setOnTouchListener(mTouchListener);

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
                    if (selection.equals( "UNKNOWN" )){
                        mSupplierName = SaleEntry.UNKNOWN;
                    } else if (selection.equals( getString( R.string.supplier_KAMUEL ) )) {
                        mSupplierName = SaleEntry.KAMUEL;
                    } else if (selection.equals( getString( R.string.supplier_DEPEDRO ) )) {
                        mSupplierName = SaleEntry.DEPEDRO;
                    } else if(selection.equals( getString( R.string.supplier_FOREX ) )){
                        mSupplierName = SaleEntry.FOREX;
                    } else if (selection.equals( getString( R.string.supplier_FORSCLASS ) )) {
                        mSupplierName = SaleEntry.FORSCLASS;
                    } else if(selection.equals( getString( R.string.supplier_NIKE ) )){
                        mSupplierName = SaleEntry.NIKE;
                    } else if (selection.equals( getString( R.string.supplier_WALKAIR ) )) {
                        mSupplierName = SaleEntry.WALKAIR;
                    }
                }

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplierName = SaleEntry.UNKNOWN;
            }
        } );
    }

    /**
     * Get user input from editor and save new sale into database.
     */
    private Boolean saveSale() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentSaleUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && mSupplierName == SaleEntry.UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return false;
        }

        // These lines check weather all of the field filled or not. If not, it prompts the error messages.
        Boolean isThereMissingField = false;
        // checking if there any blank editText or not
        if(TextUtils.isEmpty(mNameEditText.getText()) ){ mNameEditText.setError( "Name is required!" ); isThereMissingField = true;}
        else if (TextUtils.isEmpty(mPriceEditText.getText())) { mPriceEditText.setError( "Price is required!" ); isThereMissingField = true;}
        else if (TextUtils.isEmpty(mQuantityEditText.getText())) { mQuantityEditText.setError( "Quantity is required!" ); isThereMissingField = true;}

        if(isThereMissingField) return false;

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put( SaleEntry.COLUMN_SALE_PRODUCT_NAME, nameString );
        values.put( SaleEntry.COLUMN_SALE_PRICE, priceString );
        values.put( SaleEntry.COLUMN_SALE_QUANTITY, quantityString );
        values.put( SaleEntry.COLUMN_SALE_SUPPLIER_NAME, mSupplierName );

        // Determine if this is a new or existing pet by checking if mCurrentSaleUri is null or not
        if (mCurrentSaleUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(SaleEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_sale_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_sale_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the sale with content URI: mCurrentSaleUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentSaleUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentSaleUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_sale_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_sale_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_add_or_edit_item, menu );
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new sale, hide the "Delete" menu item.
        if (mCurrentSaleUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save sale to database
                if (!saveSale()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if(mSaleHasChanged) {
                    NavUtils.navigateUpFromSameTask( AddEditSaleActivity.this );
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddEditSaleActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the sale hasn't changed, continue with handling back button press
        if (!mSaleHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                SaleEntry._ID,
                SaleEntry.COLUMN_SALE_PRODUCT_NAME,
                SaleEntry.COLUMN_SALE_PRICE,
                SaleEntry.COLUMN_SALE_QUANTITY,
                SaleEntry.COLUMN_SALE_SUPPLIER_NAME};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentSaleUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_SALE_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_SALE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_SALE_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_SALE_SUPPLIER_NAME);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierNameColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString( price ));
            mQuantityEditText.setText(Integer.toString( quantity ));

            // Supplier is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 KAMUEL, 2 is WALKAIR, 3 is DEPEDRO and so on).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (supplierName) {
                case SaleEntry.UNKNOWN:
                    mSupplierNameSpinner.setSelection( 0 );
                    break;
                case SaleEntry.KAMUEL:
                    mSupplierNameSpinner.setSelection( 1 );
                    break;
                case SaleEntry.WALKAIR:
                    mSupplierNameSpinner.setSelection( 2 );
                    break;
                case SaleEntry.DEPEDRO:
                    mSupplierNameSpinner.setSelection( 3 );
                    break;
                case SaleEntry.NIKE:
                    mSupplierNameSpinner.setSelection( 4 );
                    break;
                case SaleEntry.FOREX:
                    mSupplierNameSpinner.setSelection( 5 );
                    break;
                case SaleEntry.FORSCLASS:
                    mSupplierNameSpinner.setSelection( 6 );
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameSpinner.setSelection( 0 ); // Select 'UNKNOWN' supplier

    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg_sale);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteSale();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteSale() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentSaleUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentSaleUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentSaleUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_sale_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_sale_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
