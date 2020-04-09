package ru.controlprograms.yourecipts.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import ru.controlprograms.yourecipts.utils.Date;

/**
 * Created by evgeny on 03.08.16.
 */

public class Receipt {
    private static final long NO_ID = -1;
    private static long NO_TOTAL = -1;
    private long mId;
    private Shop mShop;
    private String mTimeStamp;
    private double mTotal;
    private ArrayList<Good> mGoods;
    private Boolean mAccepted;
    private double mDiscount;

    public Receipt(Shop shop, String timeStamp, double total, double discount, ArrayList<Good> goods, boolean accepted) {
        mShop = shop;
        mTimeStamp = timeStamp;
        mTotal = total;
        mGoods = goods;
        mAccepted = accepted;
        mId = NO_ID;
        mDiscount = discount;
    }
    public Receipt(long id, Shop shop, String timeStamp, double total, double discount, ArrayList<Good> goods, boolean accepted) {
        mId = id;
        mShop = shop;
        mTimeStamp = timeStamp;
        mTotal = total;
        mGoods = goods;
        mAccepted = accepted;
        mDiscount = discount;
    }

    public Receipt(Base base, Shop shop, ArrayList<Good> goods) {
        mShop = shop;
        mTimeStamp = base.getTimeStamp();
        mTotal = base.getTotal();
        mGoods = goods;
        mAccepted = base.isAccepted();
        mId = base.getReceiptId();
        mDiscount = base.getDiscount();
    }

    public Base getBase() {
        return new Base(mId, mTimeStamp, mTotal, mDiscount, mAccepted);
    }

    public Shop getShop() {
        return mShop;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public double getTotal() {
        return mTotal;
    }

    public boolean isAccepted() {
        return mAccepted;
    }

    public long getId() {
        return mId;
    }

    public Preview getPreview() {
        return new Preview(getBase(), getShop());
    }

    public void setAccepted(boolean accepted) {
        this.mAccepted = accepted;
    }

    public void setReceiptId(long receiptId) {
        this.mId = receiptId;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public double getDiscount() {
        return mDiscount;
    }

    public static class Preview {
        public static final byte NORMAL = 0;
        public static final byte LOAD = 1;
        private Base mBase;
        private Shop mShop;
        private Date mDate;

        public Preview(Base base, Shop shop) {
            mBase = base;
            mShop = shop;
        }

        public Preview(Base base, Shop shop, Date date) {
            mBase = base;
            mShop = shop;
            mDate = date;
        }

        public Base getBase() {
            return mBase;
        }

        public Shop getShop() {
            return mShop;
        }

        public Date getDate() {
            return mDate;
        }

        public void setPreview(Preview preview) {
            mBase = preview.getBase();
            mShop = preview.getShop();
        }
    }

    public static class Base {
        private String mTimeStamp;
        private double mTotal;
        private Boolean mAccepted;
        private long mReceiptId;
        private double mDiscount;


        public Base(long receiptId, String timeStamp, double total, double discount, boolean accepted) {
            mReceiptId = receiptId;
            mTimeStamp = timeStamp;
            mTotal = total;
            mAccepted = accepted;
            mDiscount = discount;
        }

        public String getTimeStamp() {
            return mTimeStamp;
        }

        public double getTotal() {
            return mTotal;
        }

        public boolean isAccepted() {
            return mAccepted;
        }
        public long getReceiptId() {
            return mReceiptId;
        }

        public double getDiscount() {
            return mDiscount;
        }

        public void setAccepted(boolean accepted) {
            mAccepted = accepted;
        }
    }

    public static class Good {
        private long mId;
        private String mName;
        private String mForItem;
        private String mQuantity;
        private String mTotalString;
        private double mTotaldouble;
        private String mDiscount;

        public Good(long id, String name, String forItem, String quantity, String total, String discount) {
            mId = id;
            mName = name;
            mForItem = forItem;
            mQuantity = quantity;
            mTotalString = total;
            mDiscount = discount;
            setTotalDouble(total);
        }

        public Good(String name, String forItem, String quantity, String total, String discount) {
            mId = NO_ID;
            mName = name;
            mForItem = forItem;
            mQuantity = quantity;
            mTotalString = total;
            mDiscount = discount;
            setTotalDouble(total);
        }

        private void setTotalDouble(String total) {

            try {
                mTotaldouble = Double.valueOf(total);
            } catch (NumberFormatException e) {
                mTotaldouble = NO_TOTAL;
            }
        }

        public Good() {
            mName = "";
            mForItem = "";
            mQuantity = "";
            mTotalString = "";
            mTotaldouble = NO_TOTAL;
            mId = NO_ID;
            mDiscount = "0";
        }

        public long getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public String getForItem() {
            return mForItem;
        }

        public String getQuantity() {
            return mQuantity;
        }

        public String getTotalString() {
            return mTotalString;
        }
        public BigDecimal getTotalBigDecimal() {
            return new BigDecimal(mTotaldouble);
        }

        public void setName(String name) {
            mName = name;
        }

        public void setForItem(String forItem) {
            mForItem = forItem;
        }

        public void setQuantity(String quantity) {
            mQuantity = quantity;
        }

        public double setTotal(String total) {
            mTotalString = total;
            setTotalDouble(total);
            return mTotaldouble;
        }

        public String getDiscount() {
            return mDiscount;
        }

        public void setDiscount(String discount) {
            mDiscount = discount;
        }
    }

    public static class Shop {
        private long mId;
        private String mName;
        private String mAddress;
        public Shop(long id, String name, String address) {
            mId = id;
            mName = name;
            mAddress = address;
        }
        public Shop( String name, String address) {
            mId = NO_ID;
            mName = name;
            mAddress = address;
        }

        public Shop() {
            mName = "";
            mAddress = "";
            mId = NO_ID;
        }

        public String getName() {
            return mName;
        }

        public String getAddress() {
            return mAddress;
        }
        public long getId() {
            return mId;
        }
    }

    public ArrayList<Good> getGoods() {
        return mGoods;
    }

}
