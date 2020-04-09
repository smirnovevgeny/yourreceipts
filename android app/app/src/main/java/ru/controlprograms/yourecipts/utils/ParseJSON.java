package ru.controlprograms.yourecipts.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import ru.controlprograms.yourecipts.model.Receipt;

/**
 * Created by evgeny on 09.08.16.
 */

public class ParseJSON {

    private static final String SHOP_NAME = "shop_name";
    private static final String ADDRESS = "address";
    private static final String LINES = "lines";
    private static final String ITEM_TOTAL = "total";
    private static final String FOR_ITEM = "for_item";
    private static final String GOOD_NAME = "good_name";
    private static final String QUANTITY = "quantity";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String DISCOUNT = "discount";

    private JSONObject mJsonObject;
    private BigDecimal mTotal = BigDecimal.ZERO;
    private BigDecimal MINUS_ONE = new BigDecimal(-1);

    public ParseJSON(String jsonObject) throws JSONException {
        mJsonObject = new JSONObject(jsonObject);
    }

    public Receipt.Shop getShop() throws JSONException {
        String shopName = mJsonObject.getString(SHOP_NAME);
        String address = mJsonObject.getString(ADDRESS);
        return new Receipt.Shop(shopName, address);
    }



    public ArrayList<Receipt.Good> getGoods() throws JSONException {
        JSONArray jsonArray = mJsonObject.getJSONArray(LINES);
        ArrayList<Receipt.Good> receiptItems = new ArrayList<Receipt.Good>();
        JSONObject jsonObject;
        Receipt.Good item;
        BigDecimal currentTotal;
        String total;
        for (int i=0, size = jsonArray.length(); i < size; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            total = jsonObject.getString(ITEM_TOTAL);

            if (!mTotal.equals(MINUS_ONE)) {
                try {
                    currentTotal = new BigDecimal(total);
                    mTotal = mTotal.add(currentTotal);
                } catch (NumberFormatException e) {
                    mTotal = MINUS_ONE;
                }
            }

            item = new Receipt.Good(jsonObject.getString(GOOD_NAME),
                    jsonObject.getString(FOR_ITEM), jsonObject.getString(QUANTITY),
                    jsonObject.getString(ITEM_TOTAL), jsonObject.getString(DISCOUNT));
            receiptItems.add(item);
        }
        return receiptItems;
    }
    public Receipt getReceipt() throws JSONException {
        ArrayList<Receipt.Good> goods = getGoods();
        String date = mJsonObject.getString(DATE);

        if (date.isEmpty()) {
            date = Date.getCurrentDate();
        }
        String time = mJsonObject.getString(TIME);
        if (time.isEmpty()) {
            time = "00:00";
        }

        String timeStamp = date + " " + time;
        return new Receipt(getShop(), timeStamp, mTotal.doubleValue(),
                mJsonObject.getDouble(DISCOUNT), goods, false);
    }
}
