package ru.controlprograms.yourecipts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.Date;

/**
 * Created by evgeny on 05.08.16.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String REFERENCES = " REFERENCES ";
    private static final String FOREIGN_KEY = "FOREIGN KEY";
    private static final String COMMA_SEP = ", ";
    private static final String BRACKET_LEFT = "(";
    private static final String BRACKET_RIGHT = ")";
    private static final String NOT_NULL = " NOT NULL";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String DEFAULT_TIMESTAMP = " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DataBase.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createShop = CREATE_TABLE + Contract.Shop.TABLE_NAME +
                BRACKET_LEFT +
                Contract.Shop.SHOP_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                Contract.Shop.NAME + TEXT_TYPE + COMMA_SEP +
                Contract.Shop.ADDRESS + TEXT_TYPE +
                BRACKET_RIGHT;
        sqLiteDatabase.execSQL(createShop);

        String createReceiptGood = CREATE_TABLE + Contract.ReceiptGood.TABLE_NAME +
                BRACKET_LEFT +
                Contract.ReceiptGood.RECEIPT_GOOD_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                Contract.ReceiptGood.RECEIPT_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                Contract.ReceiptGood.GOOD_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                Contract.ReceiptGood.DISCOUNT + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                Contract.ReceiptGood.QUANTITY + INTEGER_TYPE +
                BRACKET_RIGHT;
        sqLiteDatabase.execSQL(createReceiptGood);

        String createGood = CREATE_TABLE + Contract.Good.TABLE_NAME +
                BRACKET_LEFT +
                Contract.Good.GOOD_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                Contract.Good.NAME + TEXT_TYPE + COMMA_SEP +
                Contract.Good.FORITEM + INTEGER_TYPE + COMMA_SEP +
                Contract.Good.SHOP_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                FOREIGN_KEY + BRACKET_LEFT + Contract.Good.SHOP_ID + BRACKET_RIGHT + REFERENCES +
                Contract.Shop.TABLE_NAME + BRACKET_LEFT + Contract.Shop.SHOP_ID + BRACKET_RIGHT + COMMA_SEP +
                FOREIGN_KEY + BRACKET_LEFT + Contract.Good.GOOD_ID + BRACKET_RIGHT + REFERENCES +
                Contract.ReceiptGood.TABLE_NAME + BRACKET_LEFT + Contract.ReceiptGood.GOOD_ID + BRACKET_RIGHT +
                BRACKET_RIGHT;
        sqLiteDatabase.execSQL(createGood);

        String createReceipt = CREATE_TABLE + Contract.Receipt.TABLE_NAME +
                BRACKET_LEFT +
                Contract.Receipt.RECEIPT_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                Contract.Receipt.SHOP_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                Contract.Receipt.TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                Contract.Receipt.TOTAL +  INTEGER_TYPE + COMMA_SEP +
                Contract.Receipt.ACCEPTED + INTEGER_TYPE + COMMA_SEP +
                Contract.Receipt.DISCOUNT + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                Contract.Receipt.TIMESTAMP_ADD + DEFAULT_TIMESTAMP + COMMA_SEP +
                FOREIGN_KEY + BRACKET_LEFT + Contract.Good.SHOP_ID + BRACKET_RIGHT + REFERENCES +
                Contract.Shop.TABLE_NAME + BRACKET_LEFT + Contract.Shop.SHOP_ID + BRACKET_RIGHT + COMMA_SEP +
                FOREIGN_KEY + BRACKET_LEFT + Contract.Receipt.RECEIPT_ID + BRACKET_RIGHT + REFERENCES +
                Contract.ReceiptGood.TABLE_NAME + BRACKET_LEFT + Contract.Receipt.RECEIPT_ID + BRACKET_RIGHT +
                BRACKET_RIGHT;
        sqLiteDatabase.execSQL(createReceipt);




    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private long insertShop(Receipt.Shop shop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Shop.NAME, shop.getName());
        contentValues.put(Contract.Shop.ADDRESS, shop.getAddress());

        return db.insert(Contract.Shop.TABLE_NAME, null, contentValues);
    }



    private class QuantityId {
        private double mQuantity;
        private long mGoodId;
        private double mDiscount;

        private QuantityId(long goodId, double quantity, double discount) {
            mGoodId = goodId;
            mQuantity = quantity;
            mDiscount = discount;
        }

        private double getQuantity() {
            return mQuantity;
        }

        private long getGoodId() {
            return mGoodId;
        }

        private double getDiscount() {
            return mDiscount;
        }
    }
    private ArrayList<QuantityId> insertGoods(ArrayList<Receipt.Good> goods, long shop_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<QuantityId> quantityIds = new ArrayList<QuantityId>();
//        TODO use one transaction
        ContentValues contentValues;
        long good_id;
        double quantity, discount;
        for (Receipt.Good good : goods) {
            contentValues = new ContentValues();
            contentValues.put(Contract.Good.NAME, good.getName());
            contentValues.put(Contract.Good.FORITEM, good.getForItem());
            contentValues.put(Contract.Good.SHOP_ID, shop_id);
            good_id = db.insert(Contract.Good.TABLE_NAME, null, contentValues);
            try {
                quantity = Double.valueOf(good.getQuantity());
            } catch (NumberFormatException e) {
                quantity = 1;
            }
            try {
                discount = Double.valueOf(good.getDiscount());
            } catch (NumberFormatException e) {
                discount = 0;
            }
            quantityIds.add(new QuantityId(good_id, quantity, discount));
        }
        return quantityIds;
    }

    private void insertReceiptGood(ArrayList<QuantityId> quantityIds, long receipt_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;
        for (QuantityId quantityId : quantityIds ) {
            contentValues = new ContentValues();
            contentValues.put(Contract.ReceiptGood.GOOD_ID, quantityId.getGoodId());
            contentValues.put(Contract.ReceiptGood.QUANTITY, quantityId.getQuantity());
            contentValues.put(Contract.ReceiptGood.DISCOUNT, quantityId.getDiscount());
            contentValues.put(Contract.ReceiptGood.RECEIPT_ID, receipt_id);
            db.insert(Contract.ReceiptGood.TABLE_NAME, null, contentValues);
        }
    }

    public long insertReceipt(Receipt receipt) {
        SQLiteDatabase db = this.getWritableDatabase();
        long shop_id = insertShop(receipt.getShop());
        ArrayList<QuantityId> quantityIds = insertGoods(receipt.getGoods(), shop_id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Receipt.SHOP_ID, shop_id);
        contentValues.put(Contract.Receipt.ACCEPTED, receipt.isAccepted());
        contentValues.put(Contract.Receipt.TIMESTAMP, receipt.getTimeStamp());
        contentValues.put(Contract.Receipt.TOTAL, receipt.getTotal());
        contentValues.put(Contract.Receipt.DISCOUNT, receipt.getDiscount());

        long receipt_id = db.insert(Contract.Receipt.TABLE_NAME, null, contentValues);
        insertReceiptGood(quantityIds, receipt_id);

        return receipt_id;
    }

    private void updateBase(Receipt.Base base) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Receipt.TIMESTAMP, base.getTimeStamp());
        contentValues.put(Contract.Receipt.TOTAL, base.getTotal());
        contentValues.put(Contract.Receipt.ACCEPTED, base.isAccepted());
        String selection = Contract.Receipt.RECEIPT_ID + "=" + base.getReceiptId();
        db.update(Contract.Receipt.TABLE_NAME, contentValues, selection, null);
    }

    public void updateShop(Receipt.Shop shop) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Shop.NAME, shop.getName());
        contentValues.put(Contract.Shop.ADDRESS, shop.getAddress());
        String selection = Contract.Shop.SHOP_ID + "=" + String.valueOf(shop.getId());
        db.update(Contract.Shop.TABLE_NAME, contentValues, selection, null);
    }

    public void updateReceipt(Receipt receipt, ArrayList<Long> deletedIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        updateBase(receipt.getBase());
        updateShop(receipt.getShop());

        long receiptId = receipt.getId();
        String selection;
        ContentValues contentValues;
        ArrayList<Receipt.Good> goodsForInsert = new ArrayList<Receipt.Good>();
        for (Receipt.Good good : receipt.getGoods()) {
            if (good.getId() != -1) {
                selection = Contract.ReceiptGood.RECEIPT_ID + "="
                        + receiptId + " and " + Contract.ReceiptGood.GOOD_ID + " = " + good.getId();
                contentValues = new ContentValues();
                contentValues.put(Contract.ReceiptGood.QUANTITY, good.getQuantity());
                db.update(Contract.ReceiptGood.TABLE_NAME, contentValues, selection, null);
                selection = Contract.ReceiptGood.GOOD_ID + " = " + good.getId();
                contentValues = new ContentValues();
                contentValues.put(Contract.Good.NAME, good.getName());
                contentValues.put(Contract.Good.FORITEM, good.getForItem());
                db.update(Contract.Good.TABLE_NAME, contentValues, selection, null);
            } else {
                goodsForInsert.add(good);
            }
        }
        if (!goodsForInsert.isEmpty()) {
            ArrayList<QuantityId> quantityIds = insertGoods(goodsForInsert, receipt.getShop().getId());
            insertReceiptGood(quantityIds, receiptId);
        }
        for (long id : deletedIds) {
            selection = Contract.ReceiptGood.GOOD_ID + "=" + String.valueOf(id) +
                    " and " + Contract.ReceiptGood.RECEIPT_ID + "=" + String.valueOf(receiptId);
            db.delete(Contract.ReceiptGood.TABLE_NAME, selection, null);
            selection = Contract.Good.GOOD_ID + "=" + String.valueOf(id);
            db.delete(Contract.Good.TABLE_NAME, selection, null);
        }
        db.close();
    }

    public ArrayList<Receipt.Preview> getReceiptPreview() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                Contract.Shop.NAME,
                Contract.Receipt.TIMESTAMP,
                Contract.Receipt.TOTAL,
                Contract.Receipt.ACCEPTED,
                Contract.Receipt.RECEIPT_ID,
                Contract.Receipt.DISCOUNT,
                Contract.Shop.ADDRESS,
                Contract.Receipt.TIMESTAMP_ADD,
                Contract.Shop.TABLE_NAME + "." + Contract.Shop.SHOP_ID,
        };
        String sortOrder = Contract.Receipt.TIMESTAMP_ADD + " DESC";
        String joinStatement = " LEFT OUTER JOIN " + Contract.Shop.TABLE_NAME +
                " ON " + Contract.Receipt.TABLE_NAME + "." +  Contract.Receipt.SHOP_ID + "=" + Contract.Shop.TABLE_NAME +
                "." + Contract.Shop.SHOP_ID;

        Cursor cursor = db.query(Contract.Receipt.TABLE_NAME + joinStatement, projection, null, null, null, null, sortOrder);
        ArrayList<Receipt.Preview> receiptPreviews = new ArrayList<Receipt.Preview>();
        int columnName, columnTimeStamp, columnTotal, columnAccepted, columnReceiptId, columnDiscount,
                columnAddress, columnTimeStampAdd, columnShopId;
        if (cursor.moveToFirst()) {
            columnName = cursor.getColumnIndex(projection[0]);
            columnTimeStamp = cursor.getColumnIndex(projection[1]);
            columnTotal = cursor.getColumnIndex(projection[2]);
            columnAccepted = cursor.getColumnIndex(projection[3]);
            columnReceiptId = cursor.getColumnIndex(projection[4]);
            columnDiscount = cursor.getColumnIndex(projection[5]);
            columnAddress = cursor.getColumnIndex(projection[6]);
            columnTimeStampAdd = cursor.getColumnIndex(projection[7]);
            columnShopId = cursor.getColumnIndex(Contract.Shop.SHOP_ID);
            Receipt.Base base;
            Receipt.Shop shop;
            do {
                base = new Receipt.Base(cursor.getLong(columnReceiptId), Date.getTimeStampFromDataBase(cursor.getString(columnTimeStamp)),
                        cursor.getInt(columnTotal), cursor.getDouble(columnDiscount), (cursor.getInt(columnAccepted) == 1));
                shop = new Receipt.Shop(cursor.getLong(columnShopId), cursor.getString(columnName), cursor.getString(columnAddress));
                Timestamp timestamp = Timestamp.valueOf(cursor.getString(columnTimeStampAdd));

                receiptPreviews.add(new Receipt.Preview(base, shop, new Date(timestamp.getTime())));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return receiptPreviews;
    }

    public Receipt getReceipt(Receipt.Preview preview) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
            Contract.Good.NAME,
            Contract.Good.FORITEM,
            Contract.ReceiptGood.QUANTITY,
            Contract.ReceiptGood.DISCOUNT,
            Contract.ReceiptGood.TABLE_NAME + "." + Contract.ReceiptGood.GOOD_ID
        };
        long receipt_id = preview.getBase().getReceiptId();
        String joinStatement = " LEFT OUTER JOIN " + Contract.ReceiptGood.TABLE_NAME +
                " ON " + Contract.Good.TABLE_NAME + "." +  Contract.Good.GOOD_ID + "=" + Contract.ReceiptGood.TABLE_NAME +
                "." + Contract.ReceiptGood.GOOD_ID;
        String selection = Contract.ReceiptGood.RECEIPT_ID + "=" + Long.toString(receipt_id);

        Cursor cursor = db.query(Contract.Good.TABLE_NAME + joinStatement, projection, selection, null, null, null, null);
        ArrayList<Receipt.Good> goods = new ArrayList<Receipt.Good>();
        int columnName, columnForItem, columnQuantity, columnDiscount, columnId;
        if (cursor.moveToFirst()) {
            columnName = cursor.getColumnIndex(projection[0]);
            columnForItem = cursor.getColumnIndex(projection[1]);
            columnQuantity = cursor.getColumnIndex(projection[2]);
            columnDiscount = cursor.getColumnIndex(projection[3]);
            columnId = cursor.getColumnIndex(Contract.ReceiptGood.GOOD_ID);

            BigDecimal forItem, quantity, total;
            double discount;
            do {
                forItem = BigDecimal.valueOf(cursor.getDouble(columnForItem));
                quantity = BigDecimal.valueOf(cursor.getDouble(columnQuantity));
                total = forItem.multiply(quantity);
                discount = cursor.getDouble(columnDiscount);

                goods.add(new Receipt.Good(cursor.getLong(columnId), cursor.getString(columnName),
                        String.valueOf(forItem), String.valueOf(quantity), String.valueOf(total),
                        String.valueOf(discount)));
            } while (cursor.moveToNext());
        }

        return new Receipt(preview.getBase(), preview.getShop(), goods);

    }

}
