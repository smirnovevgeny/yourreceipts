package ru.controlprograms.yourecipts.database;

import android.provider.BaseColumns;

/**
 * Created by evgeny on 05.08.16.
 */

public class Contract {

    public Contract () {

    }

    public static abstract class Receipt implements BaseColumns {
        public static final String TABLE_NAME = "receipt";
        public static final String RECEIPT_ID = "receipt_id";
        public static final String SHOP_ID = "shop_id";
        public static final String TIMESTAMP ="timestamp";
        public static final String TIMESTAMP_ADD = "timestampt_add";
        public static final String TOTAL = "total";
        public static final String DISCOUNT = "discount";
        public static final String ACCEPTED = "accepted";
    }

    public static abstract class Good implements BaseColumns {
        public static final String TABLE_NAME = "good";
        public static final String GOOD_ID = "good_id";
        public static final String NAME = "name";
//        Todo вспомнить зачем нужно было дублировтать shopId
        public static final String SHOP_ID = "shop_id";
        public static final String FORITEM = "for_item";
    }

    public static abstract class Shop implements BaseColumns {
        public static final String TABLE_NAME = "shop";
        public static final String SHOP_ID = "shop_id";
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
    }

    public static abstract class ReceiptGood implements BaseColumns {
        public static final String TABLE_NAME = "receipt_good";
        public static final String RECEIPT_GOOD_ID = "receipt_good_id";
        public static final String RECEIPT_ID = "receipt_id";
        public static final String GOOD_ID = "good_id";
        public static final String QUANTITY = "quantity";
        public static final String DISCOUNT = "discount";
    }
}
