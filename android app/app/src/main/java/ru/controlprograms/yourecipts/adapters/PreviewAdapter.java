package ru.controlprograms.yourecipts.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.model.Receipt;

import java.util.ArrayList;

import static ru.controlprograms.yourecipts.model.Receipt.Preview.LOAD;
import static ru.controlprograms.yourecipts.model.Receipt.Preview.NORMAL;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {


    private ArrayList<Receipt.Preview> mReceiptPreviews;
    private OnItemClickListener mListener;

    public PreviewAdapter(ArrayList<Receipt.Preview> receiptPreviews, OnItemClickListener listener) {
        mReceiptPreviews = receiptPreviews;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int inversedPosition = mReceiptPreviews.size() - position - 1;
        if (mReceiptPreviews.get(inversedPosition) != null) {
            return NORMAL;
        } else {
            return LOAD;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == LOAD)
                layoutId = R.layout.fragment_preview_load;
        else {
                layoutId = R.layout.fragment_preview;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int inversedPosition = getItemCount() - position - 1;
        Receipt.Preview receiptPreview = mReceiptPreviews.get(inversedPosition);
        if (receiptPreview != null) {
            Receipt.Base base = receiptPreview.getBase();
            holder.showImageEdit(!base.isAccepted());
            double total = base.getTotal();
            holder.setTotal(total);
            holder.setShopName(receiptPreview.getShop().getName());
            holder.setTimeStamp(base.getTimeStamp());
            holder.bind(mListener, inversedPosition);
        }
    }

    @Override
    public int getItemCount() {
        return mReceiptPreviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mShopName;
        private TextView mTimeStamp;
        private TextView mTotal;
        private ImageView mImageEdit;

        private ViewHolder(View view) {
            super(view);
            mShopName = (TextView) view.findViewById(R.id.shop_name);
            mTimeStamp = (TextView) view.findViewById(R.id.time_stamp);
            mTotal = (TextView) view.findViewById(R.id.total);
            mImageEdit = (ImageView) view.findViewById(R.id.imageEdit);
        }

        private void setShopName(String shopName) {
            mShopName.setText(shopName);
        }

        private void setTimeStamp(String timeStamp) {
            mTimeStamp.setText(timeStamp);
        }

        public TextView getTotal() {
            return mTotal;
        }

        private void showImageEdit(boolean show) {
            if (show) {
                mImageEdit.setVisibility(View.VISIBLE);
            } else {
                mImageEdit.setVisibility(View.GONE);
            }
        }


        public void setTotal(double total) {
            if (total != -1) {
                mTotal.setText(Double.toString(total));
            } else {
                mTotal.setText(itemView.getResources().getString(R.string.NA));
            }
        }

        private void bind(final OnItemClickListener mListener, final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(view, position);
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
