package ru.controlprograms.yourecipts.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.asyncTasks.GetReceipt;
import ru.controlprograms.yourecipts.database.DbHelper;
import ru.controlprograms.yourecipts.fragments.Goods;
import ru.controlprograms.yourecipts.adapters.EditAdapter;
import ru.controlprograms.yourecipts.fragments.Preview;
import ru.controlprograms.yourecipts.fragments.ShopEdit;
import ru.controlprograms.yourecipts.fragments.ShopView;
import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.Date;

public class ReceiptActivity extends AppCompatActivity {

    private static final boolean NOT_ACCEPTED = false;
    private static final long NO_ITEM = -1;
    public static final String CHANGED_PAGE_POSITION = "acceptedPagePosition";
    public static final String CHANGED_ITEM_POSITION = "acceptedItemPosition";
    public static final String RESULT_RECEIPT = "resultReceipt";
    public static final String CHANGE_TYPE = "change_type";
    public static final int NO_CHANGE = -1;
    public static final int INSERTED = 0;
    public static final int CHANGED = 1;
    private Goods mFragmentGoods;
    private Fragment mShopFragment;
    private TextView mTotal;
    private boolean mEditMode;
    private Receipt mReceipt = null;
    private boolean mAddAction;
    private long mReceiptId;
    private boolean mInsertReceipt;
    private boolean mChanged;
    private int mChangedPagePosition;
    private int mChangedItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInsertReceipt = false;
        setContentView(R.layout.activity_receipt);
        Intent inputIntent = getIntent();
        mReceiptId = inputIntent.getLongExtra(Preview.RECEIPT_ID, NO_ITEM);
        boolean accepted = inputIntent.getBooleanExtra(Preview.ACCEPTED, NOT_ACCEPTED);
        mChangedPagePosition = inputIntent.getIntExtra(Preview.POSITION_PAGE, -1);
        mChangedItemPosition = inputIntent.getIntExtra(Preview.POSIONT_ITEM, -1);
        mEditMode = !accepted;
        mTotal = (TextView) findViewById(R.id.total);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        mAddAction = inputIntent.getBooleanExtra(MainActivity.ADD_RECEIPT_BUTTON, false);
        fillData();


    }

    private void fillData() {
        Intent inputIntent = getIntent();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (mReceipt != null) {
            fragmentTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        }
        String shopTag;
        String goodsTag = Goods.TAG;
        if (!mAddAction) {
            if (mReceipt == null) {
                String jsonString = inputIntent.getStringExtra(GetReceipt.RESULT_RECEIPTS);
                if (inputIntent.getStringExtra(Preview.TAG).equals(Preview.VALUE)) {
                    Gson gson = new Gson();
                    mReceipt = gson.fromJson(jsonString, Receipt.class);
                }
            }
            Receipt.Shop shop = mReceipt.getShop();
            mFragmentGoods = Goods.newInstance(mReceipt.getGoods(), mEditMode);
            if (mEditMode) {
                mShopFragment = ShopEdit.newInstance(shop, mReceipt.getTimeStamp());
                shopTag = ShopEdit.TAG;
            } else {
                mShopFragment = ShopView.newInstance(shop, mReceipt.getTimeStamp());
                shopTag = ShopView.TAG;
            }
            setTotal(mReceipt.getTotal());
        } else {
            mEditMode = true;
            mFragmentGoods = Goods.newInstance(null, true);
            goodsTag = Goods.TAG;
            mShopFragment = ShopEdit.newInstance(null, Date.getCurrentTimeStamp());
            shopTag = ShopView.TAG;
        }
        fragmentTransaction.replace(R.id.container_shop, mShopFragment, shopTag);
        fragmentTransaction.replace(R.id.container_list, mFragmentGoods, goodsTag);

        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mEditMode) {
            if (!mAddAction) {
                inflater.inflate(R.menu.appbar_receipt_edit, menu);
            } else {
                inflater.inflate(R.menu.appbar_receipt_add, menu);
            }
        } else {
            inflater.inflate(R.menu.appbar_receipt_view, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
                case R.id.action_receipt_edit:
                    mEditMode = true;
                    fillData();
                    invalidateOptionsMenu();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                case R.id.action_receipt_save:
                    saveReceipt();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    hideKeyboard();
                    return true;
                case R.id.action_recognition_bad:
                    return true;
                case R.id.action_receipt_add:
                    mFragmentGoods.addGood();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);

        }

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void saveReceipt() {
        RecyclerView view = (RecyclerView) mFragmentGoods.getView();
        ShopEdit shopEdit = (ShopEdit) mShopFragment;
        String shopName = shopEdit.getName();
        String address = shopEdit.getAddress();
        long shopId = shopEdit.getId();
        if (!shopName.isEmpty() && !address.isEmpty()) {
            EditAdapter editAdapter = (EditAdapter) view.getAdapter();
            String timeStamp = shopEdit.getDate();
            ArrayList<Receipt.Good> goods = editAdapter.getGoods(view);
            ArrayList<Long> deletedIds = editAdapter.getDeletedIds();
            if (goods != null) {
                double total = Double.valueOf(mTotal.getText().toString());
                Receipt.Shop shop = new Receipt.Shop(shopId, shopName, address);
                boolean acceptedState;
                try {
                    acceptedState = mReceipt.isAccepted();
                } catch (NullPointerException e) {
                    acceptedState = false;
                }
                Receipt receipt = new Receipt(mReceiptId, shop, timeStamp, total, 0, goods, true);
                receipt.setAccepted(true);
                DbHelper dbHelper = new DbHelper(this);
                if (mReceiptId == NO_ITEM) {
                    dbHelper.insertReceipt(receipt);
                    mInsertReceipt = true;
                } else {
                    dbHelper.updateReceipt(receipt, deletedIds);
                    mChanged = !receipt.getPreview().equals(mReceipt.getPreview());
                }
                dbHelper.close();
                mReceipt = receipt;
                mAddAction = false;
                mEditMode = false;
                mReceipt.setTimeStamp(Date.dataBaseToNormal(mReceipt.getTimeStamp()));
                fillData();
                invalidateOptionsMenu();
            }
        } else {
            Toast toast;
            if (shopName.isEmpty()) {
                toast = Toast.makeText(this, R.string.input_error_shop_name, Toast.LENGTH_LONG);
                shopEdit.focusName();
            } else {
                toast = Toast.makeText(this, R.string.input_error_address, Toast.LENGTH_LONG);
                shopEdit.focusAddress();
            }
            toast.show();
        }
    }
    public void setTotal(double total) {
        String text;
        if (total != -1) {
            text = String.valueOf(total);
        } else {
            text = this.getResources().getString(R.string.NA);
        }
        mTotal.setText(text);
    }

    @Override
    public void onBackPressed() {
        int resultCode;
        Intent resultIntent = null;

        if (mInsertReceipt || mChanged) {
            resultCode = RESULT_OK;
            Gson gson = new Gson();
            resultIntent = new Intent();
            resultIntent.putExtra(RESULT_RECEIPT, gson.toJson(mReceipt.getPreview()));
            if (mInsertReceipt) {
                resultIntent.putExtra(CHANGE_TYPE, INSERTED);
            }
            if (mChanged) {
                resultIntent.putExtra(CHANGE_TYPE, CHANGED);
                resultIntent.putExtra(CHANGED_PAGE_POSITION, mChangedPagePosition);
                resultIntent.putExtra(CHANGED_ITEM_POSITION, mChangedItemPosition);
            }


        } else {
            resultCode = RESULT_CANCELED;
        }
        setResult(resultCode, resultIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right);
    }

}
