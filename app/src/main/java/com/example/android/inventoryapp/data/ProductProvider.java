package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the products table
     */
    private static final int PRODUCTS = 100;

    /**
     * URI matcher code for the content URI for a single product in the products table
     */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher( UriMatcher.NO_MATCH );

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PRODUCTS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI( ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS );

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PRODUCT_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI( ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID );
    }

    /**
     * Database helper object
     */
    private SalesAndProductsDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SalesAndProductsDbHelper( getContext() );
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match( uri );
        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the sales table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query( ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder );
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventoryapp/sales/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf( ContentUris.parseId( uri ) )};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query( ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder );
                break;
            default:
                throw new IllegalArgumentException( "Cannot query unknown URI " + uri );
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri( getContext().getContentResolver(), uri );

        // Return the cursor
        return cursor;

    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match( uri );
        switch (match) {
            case PRODUCTS:
                return SaleContract.SaleEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return SaleContract.SaleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException( "Unknown URI " + uri + " with match " + match );
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match( uri );
        switch (match) {
            case PRODUCTS:
                return insertProduct( uri, contentValues );
            default:
                throw new IllegalArgumentException( "Insertion is not supported for " + uri );
        }
    }

    /**
     * Insert a sale into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString( ProductEntry.COLUMN_PRODUCT_NAME );
        if (name == null) {
            throw new IllegalArgumentException( "Sale requires a product name" );
        }

        // Check that the price is valid
        Integer price = values.getAsInteger( ProductEntry.COLUMN_PRODUCT_PRICE );
        if (price == null && price < 0) {
            throw new IllegalArgumentException( "Sale requires a price" );
        }

        // If the quantity is provided, check that it's greater than or equal to 0 kg
        Integer quantity = values.getAsInteger( ProductEntry.COLUMN_PRODUCT_QUANTITY );
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException( "Sale requires valid quantity" );
        }

        // Check that the supplier name is valid
        Integer supplier = values.getAsInteger( ProductEntry.COLUMN_SUPPLIER_NAME );
        if (supplier == null || !ProductEntry.isValidSupplier( supplier )) {
            throw new IllegalArgumentException( "Product requires valid supplier" );
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert( ProductEntry.TABLE_NAME, null, values );
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e( LOG_TAG, "Failed to insert row for " + uri );
            return null;
        }

        // Notify all listeners that the data has changed for the sale content URI
        getContext().getContentResolver().notifyChange( uri, null );

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId( uri, id );
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match( uri );
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete( ProductEntry.TABLE_NAME, selection, selectionArgs );
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf( ContentUris.parseId( uri ) )};
                rowsDeleted = database.delete( ProductEntry.TABLE_NAME, selection, selectionArgs );
                break;
            default:
                throw new IllegalArgumentException( "Deletion is not supported for " + uri );
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange( uri, null );
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match( uri );
        switch (match) {
            case PRODUCTS:
                return updateProduct( uri, contentValues, selection, selectionArgs );
            case PRODUCT_ID:
                // For the SALE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf( ContentUris.parseId( uri ) )};
                return updateProduct( uri, contentValues, selection, selectionArgs );
            default:
                throw new IllegalArgumentException( "Update is not supported for " + uri );
        }
    }

    /**
     * Update sales in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more sales).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ProductEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey( ProductEntry.COLUMN_PRODUCT_NAME )) {
            String name = values.getAsString( ProductEntry.COLUMN_PRODUCT_NAME );
            if (name == null) {
                throw new IllegalArgumentException( "Sale requires a product name" );
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the gender value is valid.
        if (values.containsKey( ProductEntry.COLUMN_PRODUCT_PRICE )) {
            Integer price = values.getAsInteger( ProductEntry.COLUMN_PRODUCT_PRICE );
            if (price == null) {
                throw new IllegalArgumentException( "Sale requires valid price" );
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the weight value is valid.
        if (values.containsKey( ProductEntry.COLUMN_PRODUCT_QUANTITY )) {
            // Check that the weight is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger( ProductEntry.COLUMN_PRODUCT_QUANTITY );
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException( "Sale requires valid quantity" );
            }
        }

        // If the {@link ProductEntry#COLUMN_SUPPLIER_NAME} key is present,
        // check that the weight value is valid.
        if (values.containsKey( ProductEntry.COLUMN_SUPPLIER_NAME )) {
            // Check that the weight is greater than or equal to 0 kg
            Integer supplier = values.getAsInteger( ProductEntry.COLUMN_SUPPLIER_NAME );
            if (supplier == null || !ProductEntry.isValidSupplier( supplier )) {
                throw new IllegalArgumentException( "Product requires valid supplier" );
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase databaseForWriting = mDbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        Boolean isTheAboveIfConditionWorked = false;


        // between line 294-309, we are trying to get the current string value of quantity in Sale table.
        // For accessing the previous value of the quantity on the product table
        // Get readable database
        SQLiteDatabase databaseForReading = mDbHelper.getReadableDatabase();

        //specify the columns to be fetched
        String[] columnsInUpdateSaleTable = {SaleContract.SaleEntry.COLUMN_SALE_QUANTITY,};
        //Select condition
        String selectionInUpdateSaleTable = SaleContract.SaleEntry.COLUMN_SALE_PRODUCT_NAME + " = ?";
        //Arguments for selection
        String productNameSaleTable = values.getAsString( SaleContract.SaleEntry.COLUMN_SALE_PRODUCT_NAME );
        String[] selectionArgsInUpdateSaleTable = {productNameSaleTable};

        Cursor cursorSaleTable = databaseForReading.query( SaleContract.SaleEntry.TABLE_NAME, columnsInUpdateSaleTable, selectionInUpdateSaleTable,
                selectionArgsInUpdateSaleTable, null, null, null );

        Boolean isThereCurrentQuantityInSaleTable = true;
        if (cursorSaleTable == null) {
            isThereCurrentQuantityInSaleTable = false;
        }
        if (isThereCurrentQuantityInSaleTable == false) {

            //
            int newQuantity = 0;
            int enteredSaleQuantity = 0;
            int currentQuantityInProductTable = 0;


            enteredSaleQuantity = values.getAsInteger( ProductEntry.COLUMN_PRODUCT_QUANTITY );
            String saleProductName = values.getAsString( SaleContract.SaleEntry.COLUMN_SALE_PRODUCT_NAME );

            //specify the columns to be fetched
            String[] columnsInUpdateProductTable = {ProductEntry.COLUMN_PRODUCT_QUANTITY,};
            //Select condition
            String selectionInUpdateProductTable = ProductEntry.COLUMN_PRODUCT_NAME + " = ?";
            //Arguments for selection
            String productName = values.getAsString( ProductEntry.COLUMN_PRODUCT_NAME );
            String[] selectionArgsInUpdateProductTable = {productName};

            Cursor cursorProductTable = databaseForReading.query( ProductEntry.TABLE_NAME, columnsInUpdateProductTable, selectionInUpdateProductTable,
                    selectionArgsInUpdateProductTable, null, null, null );

            if (cursorProductTable != null) {
                // for current quantity in product table
                cursorProductTable.moveToFirst();
                int indexForQuantityProductTable = cursorProductTable.getColumnIndex( ProductEntry.COLUMN_PRODUCT_QUANTITY );
                currentQuantityInProductTable = cursorProductTable.getInt( indexForQuantityProductTable );
            }


            // Updating the quantity value in the Product table
            // New value for one column
            if (currentQuantityInProductTable >= enteredSaleQuantity) {
                newQuantity = currentQuantityInProductTable - enteredSaleQuantity;
            } else {
                throw new IllegalArgumentException( "Sale quantity must be less than " +
                        String.valueOf( currentQuantityInProductTable ) + " !" );
            }

            // New value for one column
            values.put( ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity );

            // Which row to update, based on the title
            String selectionForProductTableUpdate = ProductEntry.COLUMN_PRODUCT_NAME + " = ?";
            String[] selectionArgsForProductTableUpdate = {saleProductName};

            // Otherwise, get writable database to update the data

            rowsUpdated = databaseForWriting.update(
                    ProductEntry.TABLE_NAME,
                    values,
                    selectionForProductTableUpdate,
                    selectionArgsForProductTableUpdate );

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                // Notify all listeners that the data has changed for the sale content URI
                getContext().getContentResolver().notifyChange( uri, null );
            }

            isTheAboveIfConditionWorked = true;
        } else {
            //
            int newQuantity = 0;
            int enteredSaleQuantity = 0;
            int currentQuantityInProductTable = 0;


            enteredSaleQuantity = values.getAsInteger( ProductEntry.COLUMN_PRODUCT_QUANTITY );
            String saleProductName = values.getAsString( SaleContract.SaleEntry.COLUMN_SALE_PRODUCT_NAME );

            //specify the columns to be fetched
            String[] columnsInUpdateProductTable = {ProductEntry.COLUMN_PRODUCT_QUANTITY,};
            //Select condition
            String selectionInUpdateProductTable = ProductEntry.COLUMN_PRODUCT_NAME + " = ?";
            //Arguments for selection
            String productName = values.getAsString( ProductEntry.COLUMN_PRODUCT_NAME );
            String[] selectionArgsInUpdateProductTable = {productName};

            Cursor cursorProductTable = databaseForReading.query( ProductEntry.TABLE_NAME, columnsInUpdateProductTable, selectionInUpdateProductTable,
                    selectionArgsInUpdateProductTable, null, null, null );

            if (cursorProductTable != null) {
                // for current quantity in product table
                cursorProductTable.moveToFirst();
                int indexForQuantityProductTable = cursorProductTable.getColumnIndex( ProductEntry.COLUMN_PRODUCT_QUANTITY );
                currentQuantityInProductTable = cursorProductTable.getInt( indexForQuantityProductTable );
            }


            // Updating the quantity value in the Product table
            // New value for one column
            if (currentQuantityInProductTable >= enteredSaleQuantity) {
                newQuantity = currentQuantityInProductTable - enteredSaleQuantity;
            } else {
                throw new IllegalArgumentException( "Sale quantity must be less than " +
                        String.valueOf( currentQuantityInProductTable ) + " !" );
            }

            // New value for one column
            values.put( ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity );

            // Which row to update, based on the title
            String selectionForProductTableUpdate = ProductEntry.COLUMN_PRODUCT_NAME + " = ?";
            String[] selectionArgsForProductTableUpdate = {saleProductName};

            // Otherwise, get writable database to update the data

            rowsUpdated = databaseForWriting.update(
                    ProductEntry.TABLE_NAME,
                    values,
                    selectionForProductTableUpdate,
                    selectionArgsForProductTableUpdate );

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                // Notify all listeners that the data has changed for the sale content URI
                getContext().getContentResolver().notifyChange( uri, null );
            }

            isTheAboveIfConditionWorked = true;
        }


        if (isTheAboveIfConditionWorked == false) {

            // Perform the update on the database and get the number of rows affected
            rowsUpdated = databaseForWriting.update( ProductEntry.TABLE_NAME, values, selection, selectionArgs );

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange( uri, null );
            }
        }


        // Return the number of rows updated
        return rowsUpdated;
    }
}
