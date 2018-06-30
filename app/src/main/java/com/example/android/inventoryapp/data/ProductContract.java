package com.example.android.inventoryapp.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.products";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventoryapp/products/ is a valid path for
     * looking at product data. content://com.example.android.inventoryapp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCTS = "products";

    /**
     * Inner class that defines constant values for the store database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns{

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Name of database table for products in the store */
        public final static String TABLE_NAME = "store";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String PRODUCT_ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * Price of the one product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Quantity of the product in the store.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Supplier name.
         *
         * The only possible values are {@link #KAMUEL}, {@link #WALKAIR},
         * {@link #NIKE}, {@link #FOREX}, {@link #FORSCLASS},
         * or {@link #DEPEDRO}.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplierName";


        /**
         * Product supplier's phone number.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE ="supplierPhone";


        /**
         * Possible value for the supplier.
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
