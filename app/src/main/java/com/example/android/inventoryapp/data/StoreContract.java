package com.example.android.inventoryapp.data;


import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */
public final class StoreContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private StoreContract() {}

    /**
     * Inner class that defines constant values for the store database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns{

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
        public static final int KAMUEL = 0;
        public static final int WALKAIR = 1;
        public static final int DEPEDRO = 2;
        public static final int NIKE = 3;
        public static final int FOREX = 4;
        public static final int FORSCLASS = 5;
        public static final int UNKNOWN = 6;

    }
}
