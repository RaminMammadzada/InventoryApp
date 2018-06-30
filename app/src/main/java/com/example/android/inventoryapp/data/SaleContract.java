package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class SaleContract {


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private SaleContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.sales";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventoryapp/sales/ is a valid path for
     * looking at sale data. content://com.example.android.inventoryapp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_SALES = "sales";

    /**
     * Inner class that defines constant values for the store database table.
     * Each entry in the table represents a single pet.
     */
    public static final class SaleEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SALES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of sales.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SALES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single sale.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SALES;

        /** Name of database table for sales */
        public final static String TABLE_NAME = "sales";

        /**
         * Unique ID number for the sale (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String SALE_ID = BaseColumns._ID;

        /**
         * Name of the sold product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SALE_PRODUCT_NAME ="name";

        /**
         * Sale price of the product(s).
         *
         * Type: INTEGER
         */
        public final static String COLUMN_SALE_PRICE = "price";

        /**
         * Quantity of the sold products.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_SALE_QUANTITY = "quantity";

        /**
         * Supplier name of the sold product(s).
         *
         * The only possible values are {@link #KAMUEL}, {@link #WALKAIR},
         * {@link #NIKE}, {@link #FOREX}, {@link #FORSCLASS},
         * or {@link #DEPEDRO}.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SALE_SUPPLIER_NAME = "supplierName";


        /**
         * Possible values for the supplier.
         */
        public static final int UNKNOWN = 0;
        public static final int KAMUEL = 1;
        public static final int WALKAIR = 2;
        public static final int DEPEDRO = 3;
        public static final int NIKE = 4;
        public static final int FOREX = 5;
        public static final int FORSCLASS = 6;

        /**
         * Returns whether or not the given gender is {@link #KAMUEL}, {@link #WALKAIR},
         * {@link #DEPEDRO}, {@link #NIKE}, {@link #FOREX},
         * {@link #FORSCLASS} or {@link #UNKNOWN}.
         */
        public static boolean isValidSupplier(int supplier) {
            if (supplier == KAMUEL || supplier == WALKAIR || supplier == DEPEDRO
                    || supplier == NIKE || supplier == FOREX || supplier == FORSCLASS || supplier == UNKNOWN) {
                return true;
            }
            return false;
        }

    }

}
