package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public final class SaleContract {


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private SaleContract() {}

    /**
     * Inner class that defines constant values for the store database table.
     * Each entry in the table represents a single pet.
     */
    public static final class SaleEntry implements BaseColumns {

        /** Name of database table for products in the store */
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
        public static final int KAMUEL = 0;
        public static final int WALKAIR = 1;
        public static final int DEPEDRO = 2;
        public static final int NIKE = 3;
        public static final int FOREX = 4;
        public static final int FORSCLASS = 5;
        public static final int UNKNOWN = 6;

    }

}
