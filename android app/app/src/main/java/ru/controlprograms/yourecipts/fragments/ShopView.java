package ru.controlprograms.yourecipts.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.interfaces.Shop;
import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.Date;

/**
 * Created by evgeny on 23.08.16.
 */

public class ShopView extends Fragment {

    private static Receipt.Shop mShop;
    private TextView mName, mAddress, mDate, mTime;
    private static Date mDateValue;
    public static final String TAG = "ShopView";
    public static ShopView newInstance(Receipt.Shop shop, String timeStamp) {
        mShop = shop;
        mDateValue = new Date(timeStamp);
        return new ShopView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_view, container, false);
        mName = (TextView) view.findViewById(R.id.shop_name);
        mAddress = (TextView) view.findViewById(R.id.shop_address);
        mDate = (TextView) view.findViewById(R.id.date);
        mTime = (TextView) view.findViewById(R.id.time);

        mDate.setText(mDateValue.getDate());
        mTime.setText(mDateValue.getTime());
        mName.setText(mShop.getName());
        mAddress.setText(mShop.getAddress());

        return view;

    }
}
