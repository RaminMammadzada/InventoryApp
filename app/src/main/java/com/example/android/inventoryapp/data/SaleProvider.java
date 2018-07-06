package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.SaleContract.SaleEntry;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class SaleProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = SaleProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int SALES = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int SALE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(SaleContract.CONTENT_AUTHORITY, SaleContract.PATH_SALES, SALES);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(SaleContract.CONTENT_AUTHORITY, SaleContract.PATH_SALES + "/#", SALE_ID);
    }

    /** Database helper object */
    private SalesAndProductsDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SalesAndProductsDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                // For the SALES code, query the sales table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(SaleEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SALE_ID:
                // For the SALE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventoryapp/sales/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf( ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(SaleEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;

    }


    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                return SaleContract.SaleEntry.CONTENT_LIST_TYPE;
            case SALE_ID:
                return SaleContract.SaleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert( Uri uri,  ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                return insertSale(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a sale into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertSale(Uri uri, ContentValues values) {
        // Check that the name is not null
        String saleProductName = values.getAsString(SaleEntry.COLUMN_SALE_PRODUCT_NAME);
        if (saleProductName == null) {
            throw new IllegalArgumentException("Sale requires a product name");
        }

        // Check that the price is valid
        Integer saleProductPrice = values.getAsInteger(SaleEntry.COLUMN_SALE_PRICE);
        if (saleProductPrice == null && saleProductPrice < 0) {
            throw new IllegalArgumentException("Sale requires a price");
        }

        // If the quantity is provided, check that it's greater than or equal to 0 kg
        Integer saleProductQuantity = values.getAsInteger(SaleEntry.COLUMN_SALE_QUANTITY);
        if (saleProductQuantity != null && saleProductQuantity < 0) {
            throw new IllegalArgumentException("Sale requires valid quantity");
        }

        // Check that the supplier name is valid
        Integer supplierName = values.getAsInteger(SaleEntry.COLUMN_SALE_SUPPLIER_NAME);
        if (supplierName == null || !SaleEntry.isValidSupplier(supplierName)) {
            throw new IllegalArgumentException("Sale requires valid supplier name");
        }

        // Check that the supplier name is valid
        String supplierPhone = values.getAsString(SaleEntry.COLUMN_SALE_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Sale requires valid supplier phone");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(SaleEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the sale content URI
        getContext().getContentResolver().notifyChange( uri, null );

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(SaleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SALE_ID:
                // Delete a single row given by the ID in the URI
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(SaleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update( Uri uri,  ContentValues contentValues,  String selection,  String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALES:
                return updateSale(uri, contentValues, selection, selectionArgs);
            case SALE_ID:
                // For the SALE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateSale(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update sales in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more sales).
     * Return the number of rows that were successfully updated.
     */
    private int updateSale(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link SaleEntry#COLUMN_SALE_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(SaleEntry.COLUMN_SALE_PRODUCT_NAME)) {
            String name = values.getAsString(SaleEntry.COLUMN_SALE_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Sale requires a product name");
            }
        }

        // If the {@link SaleEntry#COLUMN_PET_GENDER} key is present,
        // check that the price value is valid.
        if (values.containsKey(SaleEntry.COLUMN_SALE_PRICE)) {
            Integer price = values.getAsInteger(SaleEntry.COLUMN_SALE_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Sale requires valid price");
            }
        }

        // If the {@link SaleEntry#COLUMN_SALE_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(SaleEntry.COLUMN_SALE_QUANTITY)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger(SaleEntry.COLUMN_SALE_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Sale requires valid quantity");
            }
        }

        // If the {@link SaleEntry#COLUMN_SALE_SUPPLIER_NAME} key is present,
        // check that the supplier name value is valid.
        if (values.containsKey(SaleEntry.COLUMN_SALE_SUPPLIER_NAME)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer supplierName = values.getAsInteger(SaleEntry.COLUMN_SALE_SUPPLIER_NAME);
            if (supplierName == null || !SaleEntry.isValidSupplier(supplierName)) {
                throw new IllegalArgumentException("Sale requires valid supplier name");
            }
        }

        // If the {@link SaleEntry#COLUMN_SALE_SUPPLIER_PHONE} key is present,
        // check that the supplier phone value is valid.
        if (values.containsKey(SaleEntry.COLUMN_SALE_SUPPLIER_PHONE)) {
            // Check that the weight is greater than or equal to 0 kg
            String supplierPhone = values.getAsString(SaleEntry.COLUMN_SALE_SUPPLIER_PHONE);
            if (supplierPhone == null ) {
                throw new IllegalArgumentException("Sale requires valid supplier phone");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(
                SaleEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            // Notify all listeners that the data has changed for the sale content URI
            getContext().getContentResolver().notifyChange( uri, null );
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

}
