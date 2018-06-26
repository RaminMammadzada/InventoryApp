package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.data.StoreContract.ProductEntry;
import com.example.android.inventoryapp.data.SaleContract.SaleEntry;

/**
 * Database helper for Inventory app. Manages database creation and version management.
 */
public class SalesAndStoreDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory2.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link SalesAndStoreDbHelper}.
     *
     * @param context of the app
     */
    public SalesAndStoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the store table
        String SQL_CREATE_STORE_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry.PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_NAME + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_STORE_TABLE);

        // Create a String that contains the SQL statement to create the sales table
        String SQL_CREATE_SALES_TABLE =  "CREATE TABLE " + SaleEntry.TABLE_NAME + " ("
                + SaleEntry.SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SaleEntry.COLUMN_SALE_PRODUCT_NAME + " TEXT NOT NULL, "
                + SaleEntry.COLUMN_SALE_PRICE + " INTEGER NOT NULL, "
                + SaleEntry.COLUMN_SALE_QUANTITY+ " INTEGER NOT NULL, "
                + SaleEntry.COLUMN_SALE_SUPPLIER_NAME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_SALES_TABLE);


    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(SalesAndStoreDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME );

        db.execSQL("DROP TABLE IF EXISTS " + SaleEntry.TABLE_NAME );

        onCreate(db);
    }

}
