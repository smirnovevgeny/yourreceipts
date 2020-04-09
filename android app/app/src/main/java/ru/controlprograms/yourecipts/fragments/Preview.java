package ru.controlprograms.yourecipts.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.activities.MainActivity;
import ru.controlprograms.yourecipts.activities.ReceiptActivity;
import ru.controlprograms.yourecipts.adapters.PreviewAdapter;
import ru.controlprograms.yourecipts.asyncTasks.GetReceipt;
import ru.controlprograms.yourecipts.broadcastReceivers.GetReceiptReceiver;
import ru.controlprograms.yourecipts.database.DbHelper;
import ru.controlprograms.yourecipts.model.Receipt;

import java.util.ArrayList;

/**
 * A fragment representing a list of Goods.
 * <p/>
 * interface.
 */
public class Preview extends Fragment {

    public static final String RECEIPT_ITEM = "item";
    public static final String TAG = "TAG";
    public static final String RECEIPT_ID = "receiptId";
    public static final String ACCEPTED = "accepted";
    public static final String VALUE = "ReceiptListFragment";
    public static final String POSITION_PAGE = "positionPage";
    public static final String POSIONT_ITEM = "positionItem";
    private GetReceiptReceiver mReceiver;
    private PreviewAdapter mAdapter;
    private ArrayList<Receipt.Preview> mPreviews;
    private boolean mUpdatable;
    private int mPosition;

    public Preview() {
        mPreviews = null;
        mUpdatable = false;
    }

    public Preview(ArrayList<Receipt.Preview> previews, int position) {
        mPreviews = previews;
        mUpdatable = position == 0;
        mPosition = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipts_list, container, false);

        final Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final DbHelper dbHelper = new DbHelper(context);
        mAdapter = new PreviewAdapter(mPreviews, new PreviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Receipt.Preview receiptPreview = mPreviews.get(position);
                Receipt receipt = dbHelper.getReceipt(receiptPreview);
                Intent intent = new Intent(getActivity(), ReceiptActivity.class);
                Gson gson = new Gson();
                String gsoned = gson.toJson(receipt);
                intent.putExtra(GetReceipt.RESULT_RECEIPTS, gsoned);
                intent.putExtra(TAG, VALUE);
                intent.putExtra(ACCEPTED, receipt.isAccepted());
                intent.putExtra(RECEIPT_ID, receipt.getId());
                intent.putExtra(POSITION_PAGE, mPosition);
                intent.putExtra(POSIONT_ITEM, position);
                getActivity().startActivityForResult(intent, MainActivity.LAUNCH_RECEIPT_ACTIVITY);
                getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
            }
        });
        if (mUpdatable) {
            mReceiver = new GetReceiptReceiver(mAdapter, mPreviews);
        }
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUpdatable) {
            mReceiver.register(getContext());
        }
    }

    @Override
    public void onPause() {
        if (mUpdatable) {
            mReceiver.unregister(getContext());
        }
        super.onPause();
    }

    public int getPosition() {
        return mPosition;
    }

}
