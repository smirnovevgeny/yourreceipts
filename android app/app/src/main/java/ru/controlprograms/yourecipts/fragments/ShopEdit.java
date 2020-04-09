package ru.controlprograms.yourecipts.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.util.Calendar;

import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.interfaces.Shop;
import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.Date;

/**
 * Created by evgeny on 23.08.16.
 */

public class ShopEdit extends Fragment implements Shop, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private  final String TIMEPICKERDIALOG = "TimePickerDialog";
    private static Receipt.Shop mShop;
    private final String DATEPICKERDIALOG = "DatePickerDialog";
    private EditText mName, mAddress;
    private Button mTime, mDate;
    private static Date mDateValue;
    public static final String TAG = "ShopEdit";
    public static ShopEdit newInstance(Receipt.Shop shop, String timeStamp) {
        mShop = shop;
        mDateValue = new Date(timeStamp);
        return new ShopEdit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_edit, container, false);
        mName = (EditText) view.findViewById(R.id.shop_name);
        mName.requestFocus();
        mAddress = (EditText) view.findViewById(R.id.shop_address);
        mDate = (Button) view.findViewById(R.id.date);
        mTime = (Button) view.findViewById(R.id.time);
        mTime.setText(mDateValue.getTime());
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(ShopEdit.this,
                        mDateValue.getHour(), mDateValue.getMinutes(), true);
                timePickerDialog.show(getFragmentManager(), TIMEPICKERDIALOG);
            }
        });
        mDate.setText(mDateValue.getDate());
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(ShopEdit.this,
                        mDateValue.getYear(), mDateValue.getMonth(), mDateValue.getDayOfMonth());
                datePickerDialog.show(getFragmentManager(), DATEPICKERDIALOG);
            }
        });
        if (mShop != null) {
            mName.setText(mShop.getName());
            mAddress.setText(mShop.getAddress());
        }

        return view;

    }

    @Override
    public String getName() {
        return mName.getText().toString();
    }

    @Override
    public String getAddress() {
        return mAddress.getText().toString();
    }

    @Override
    public void focusName() {
        mName.requestFocus();
    }

    @Override
    public void focusAddress() {
        mAddress.requestFocus();
    }

    @Override
    public long getShopId() {
        return mShop.getId();
    }

    @Override
    public String getDate() {
        return mDateValue.getTimeStampDataBase();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mDateValue.setDate(dayOfMonth, monthOfYear, year);
        mDate.setText(mDateValue.getDate());
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        mDateValue.setTime(hourOfDay, minute);
        mTime.setText(mDateValue.getTime());
    }
}
