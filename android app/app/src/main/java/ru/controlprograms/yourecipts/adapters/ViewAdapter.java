package ru.controlprograms.yourecipts.adapters;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.model.Receipt;

/**
 * Created by evgeny on 23.08.16.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    private ArrayList<Receipt.Good> mGoods;
    public ViewAdapter(ArrayList<Receipt.Good> goods) {
        mGoods = goods;
    }


    @Override
    public ViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_good_view, parent, false);
        return new ViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewAdapter.ViewHolder holder, int position) {
        Receipt.Good good = mGoods.get(position);
        holder.setName(good.getName());
        holder.setForItem(good.getForItem());
        holder.setQuantity(good.getQuantity());
        holder.setTotal(good.getTotalString());
        holder.setDiscount(good.getDiscount());
    }

    @Override
    public int getItemCount() {
        return mGoods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mName, mForItem, mQuantity, mTotal, mMinus, mDiscount;
        private ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.good);
            mForItem = (TextView) itemView.findViewById(R.id.for_item);
            mQuantity = (TextView) itemView.findViewById(R.id.quantity);
            mTotal = (TextView) itemView.findViewById(R.id.total);
            mMinus = (TextView) itemView.findViewById(R.id.minus);
            mDiscount = (TextView) itemView.findViewById(R.id.discount);
        }

        private void setName(String name) {
            mName.setText(name);
        }
        private void setForItem(String forItem) {
            mForItem.setText(forItem);
        }
        private void setQuantity(String quantity) {
            mQuantity.setText(quantity);
        }
        private void setTotal(String total) {
            mTotal.setText(total);
        }
        private void setDiscount(String discount) {
            if (Double.valueOf(discount) == 0) {
                mDiscount.setVisibility(View.GONE);
                mMinus.setVisibility(View.GONE);
            } else {
                mDiscount.setText(discount);
            }
        }
    }
}
