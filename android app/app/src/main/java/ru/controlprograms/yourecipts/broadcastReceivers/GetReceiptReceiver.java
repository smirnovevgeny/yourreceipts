package ru.controlprograms.yourecipts.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import ru.controlprograms.yourecipts.adapters.PreviewAdapter;
import ru.controlprograms.yourecipts.model.Receipt;

public class GetReceiptReceiver extends BroadcastReceiver {

    public static final String TYPE = "type";
    public static final String ACTION = "GetReceiptReceiver";
    public static final byte START = 0;
    public static final byte FINISH = 1;
    private static final byte NO_TYPE = 2;
    private static final String RECEIPT_ADD_ID = "receiptAddId";

    private final IntentFilter mFilter;
    private final PreviewAdapter mAdapter;
    private final ArrayList<Receipt.Preview> mPreviews;
    private static Receipt.Preview sPreview;
    private static HashMap<Integer, Integer> insertedPositions;

    public GetReceiptReceiver(PreviewAdapter adapter, ArrayList<Receipt.Preview> previews) {
        mFilter = new IntentFilter(ACTION);
        mAdapter = adapter;
        mPreviews = previews;
        insertedPositions = new HashMap<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        byte action = intent.getByteExtra(TYPE, NO_TYPE);
        int receiptAddId = intent.getIntExtra(RECEIPT_ADD_ID, -1);
        switch (action) {
            case START:
                insertedPositions.put(receiptAddId, mPreviews.size());
                mPreviews.add(sPreview);
                mAdapter.notifyDataSetChanged();
                break;
            case FINISH:
                int updadePosition = insertedPositions.get(receiptAddId);
                mPreviews.set(updadePosition, sPreview);
                mAdapter.notifyDataSetChanged();
                insertedPositions.remove(receiptAddId);
                break;
        }
    }

    public static void notifyReceiver(Context context, byte action, Receipt.Preview preview, int receiptAddId) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(TYPE, action);
        intent.putExtra(RECEIPT_ADD_ID, receiptAddId);
        sPreview = preview;
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void register(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this, mFilter);
    }

    public void unregister(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
