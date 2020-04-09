package ru.controlprograms.yourecipts.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.InputCheck;

import java.math.BigDecimal;
import java.util.ArrayList;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder> {


    private ArrayList<Receipt.Good> mGoods;
    private InputCheck mInputCheck;
    private boolean mScrolling;
    private InputCheck.WrongPosition mWrongPosition;
    private GlobalTotalChanged mGlobalTotalChanged;
    private ArrayList<Long> mDeletedIds;

    public EditAdapter(ArrayList<Receipt.Good> goods, GlobalTotalChanged globalTotalChanged ) {
        mGoods = goods;
        mGlobalTotalChanged = globalTotalChanged;
        mInputCheck = new InputCheck();
        mScrolling = false;
        mDeletedIds = new ArrayList<Long>();
        mGlobalTotalChanged.globalTotalChanged(globalTotal());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_good_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Receipt.Good good = mGoods.get(position);
        String name = good.getName();
        holder.setName(name);
        mInputCheck.name(name, position);
        String forItem = good.getForItem();
        mInputCheck.forItem(forItem, position);
        holder.setForItem(forItem);
        String quantity = good.getQuantity();
        mInputCheck.quantity(quantity, position);
        holder.setQuantity(quantity);
        String total = good.getTotalString();
        mInputCheck.total(total, position);
        holder.setTotal(total);
        holder.bind(mGlobalTotalChanged, position);
        holder.setDiscount(good.getDiscount());


    }

    @Override
    public int getItemCount() {
        return mGoods.size();
    }

    public ArrayList<Long> getDeletedIds() {
        return mDeletedIds;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EditText mName;
        private EditText mForItem;
        private EditText mQuantity;
        private EditText mDiscount;
        private TextView mTotal;

        public ViewHolder(View view) {
            super(view);
            mName = (EditText) view.findViewById(R.id.good);
            mForItem = (EditText) view.findViewById(R.id.for_item);
            mQuantity = (EditText) view.findViewById(R.id.quantity);
            mTotal = (TextView) view.findViewById(R.id.total);
            mDiscount = (EditText) view.findViewById(R.id.discount);
            mName.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String shopName = editable.toString();
                    int position = getAdapterPosition();
                    mInputCheck.name(shopName, position);
                    mGoods.get(position).setName(shopName);
                }
            });

            mQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String quantity = editable.toString();
                    int position = getAdapterPosition();
                    mInputCheck.quantity(quantity, position);
                    mGoods.get(position).setQuantity(quantity);
                    setTotal(quantity, getTextForItem(), getDiscount());
                }
            });
            mForItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String forItem = editable.toString();
                    int position = getAdapterPosition();
                    mInputCheck.forItem(forItem, position);
                    mGoods.get(position).setForItem(forItem);
                    setTotal(getTextQuantity(), forItem, getDiscount());
                }
            });
            mDiscount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String discount = editable.toString();
                    if (discount.isEmpty()) {
                        discount = "0";
                    }
                    int position = getAdapterPosition();
                    mGoods.get(position).setDiscount(discount);
                    setTotal(getTextQuantity(), getTextForItem(), discount);
                }
            });
        }



        public String getTextName() {
            return mName.getText().toString();
        }

        public EditText getName() {
            return mName;
        }

        public void setName(String name) {
            mName.setText(name);
        }

        public void setForItem(String forItem) {
            mForItem.setText(forItem);
        }

        public String getTextForItem() {
            return mForItem.getText().toString();
        }
        public EditText getForItem() {
            return mForItem;
        }

        public String getTextQuantity() {
            return mQuantity.getText().toString();
        }

        public EditText getQuantity() {
            return mQuantity;
        }

        public void setQuantity(String quantity) {
            mQuantity.setText(quantity);
        }

        public String getTotal() {
            return mTotal.getText().toString();
        }

        public void setTotal(String total) {
            mTotal.setText(total);
        }
        public void setTotal(String quantity, String forItem, String discount) {

            try {
                BigDecimal quantityBig = new BigDecimal(quantity);
                BigDecimal forItemBig = new BigDecimal(forItem);
                BigDecimal discountBig = new BigDecimal(discount);
                BigDecimal total = quantityBig.multiply(forItemBig).subtract(discountBig);
                if (total.signum() == -1) {
                    total = BigDecimal.ZERO;
                }
                mTotal.setText(total.toString());

            } catch (NumberFormatException e) {
                mTotal.setText(itemView.getResources().getString(R.string.NA));
            }
        }

        private void bind(final GlobalTotalChanged globalTotalChanged, int position) {
            mTotal.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String total = editable.toString();
                    int position = getAdapterPosition();
                    mInputCheck.total(total, position);
                    double setValue = mGoods.get(position).setTotal(total);
                    if (setValue != -1) {
                        globalTotalChanged.globalTotalChanged(globalTotal());
                    }

                }
            });
        }

        public void setDiscount(String discount) {
            if (Double.valueOf(discount) != 0) {
                mDiscount.setText(discount);
            }
        }

        public String getDiscount() {
            String text = mDiscount.getText().toString();
            if (!text.isEmpty()) {
                return text;
            } else {
                return "0";
            }
        }
    }

    private double globalTotal() {
        BigDecimal globalTotal = BigDecimal.ZERO;
        BigDecimal total;
        BigDecimal minusOne = new BigDecimal(-1);
        for (Receipt.Good good : mGoods) {
            total = good.getTotalBigDecimal();
            if (!total.equals(minusOne)) {
                globalTotal = globalTotal.add(total);
            } else {
                globalTotal = minusOne;
                break;
            }
        }
        return Double.valueOf(globalTotal.toString());
    }

    public void onItemDismiss(int position) {
        mDeletedIds.add(mGoods.get(position).getId());
        mGoods.remove(position);
        mGlobalTotalChanged.globalTotalChanged(globalTotal());
        mInputCheck.onDismiss(position);
        notifyItemRemoved(position);
    }


    public ArrayList<Receipt.Good> getGoods(RecyclerView view) {
        InputCheck.WrongPosition wrongPosition = mInputCheck.getWrongPosition();
        if (wrongPosition == null) {
            return mGoods;
        } else {
            int position = wrongPosition.getPosition();
            view.smoothScrollToPosition(position);
            mScrolling = true;
            mWrongPosition = wrongPosition;
            return null;
        }
    }

    public boolean isScrolling() {
        return mScrolling;
    }
    public InputCheck.WrongPosition getWrongPosition() {
        return mWrongPosition;
    }

    public void finishedScrolling() {
        mScrolling = false;
    }

    public interface GlobalTotalChanged {
        void globalTotalChanged(double globalTotal);

    }

}
