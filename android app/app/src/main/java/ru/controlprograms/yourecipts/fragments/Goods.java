package ru.controlprograms.yourecipts.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.activities.ReceiptActivity;
import ru.controlprograms.yourecipts.adapters.EditAdapter;
import ru.controlprograms.yourecipts.adapters.ViewAdapter;
import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.InputCheck;


public class Goods extends Fragment {

    public static final String TAG = "Goods";
    private static ArrayList<Receipt.Good> mGoods;
    private static boolean mEditMode = false;
    private RecyclerView mRecyclerView;

    public static Goods newInstance(ArrayList<Receipt.Good> goods, boolean editMode) {
        Goods fragment = new Goods();
        mGoods = goods;
        mEditMode = editMode;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_list, container, false);
        final Context context = view.getContext();
        mRecyclerView = (RecyclerView) view;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (mGoods == null) {
            mGoods = new ArrayList<>();
            for (int i=0; i < 2; i++) {
                mGoods.add(new Receipt.Good());
            }
        }
        if (mEditMode) {
            final EditAdapter adapter = new EditAdapter(mGoods, new EditAdapter.GlobalTotalChanged() {
                @Override
                public void globalTotalChanged(double globalTotal) {
                    ((ReceiptActivity) getActivity()).setTotal(globalTotal);
                }
            });
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                int previous = RecyclerView.SCROLL_STATE_IDLE;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (previous != RecyclerView.SCROLL_STATE_IDLE &&
                            newState == RecyclerView.SCROLL_STATE_IDLE && adapter.isScrolling()) {
                        InputCheck.WrongPosition wrongPosition = adapter.getWrongPosition();
                        int position = wrongPosition.getPosition();
                        EditAdapter.ViewHolder holder = (EditAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        TextView editText;
                        int stringId;
                        switch (wrongPosition.getType()) {
                            case InputCheck.QUANTITY:
                                stringId = R.string.input_error_quantity;
                                editText = holder.getQuantity();
                                break;
                            case InputCheck.FOR_ITEM:
                                stringId = R.string.input_error_for_item;
                                editText = holder.getForItem();
                                break;
                            case InputCheck.NAME:
                                stringId = R.string.input_error_name;
                                editText = holder.getName();
                                break;
                            default:
                                stringId = -1;
                                editText = null;
                                break;
                        }
                        Toast toast = Toast.makeText(getActivity(), stringId, Toast.LENGTH_LONG);
                        toast.show();
                        if (editText != null) {
                            editText.requestFocus();
                        }
                        adapter.finishedScrolling();
                    }
                    previous = newState;
                }
            });
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.onItemDismiss(viewHolder.getAdapterPosition());

                }
            });
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
            mRecyclerView.setAdapter(adapter);
        } else {
            ViewAdapter adapter = new ViewAdapter(mGoods);
            mRecyclerView.setAdapter(adapter);
        }
        return view;
    }

    public void addGood() {
        mGoods.add(new Receipt.Good());
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
