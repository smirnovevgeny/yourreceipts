package ru.controlprograms.yourecipts.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.controlprograms.yourecipts.database.DbHelper;
import ru.controlprograms.yourecipts.fragments.Preview;
import ru.controlprograms.yourecipts.model.Receipt;

/**
 * Created by evgeny on 03.08.16.
 */

public class PagesAdapter extends FragmentStatePagerAdapter {

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    private ArrayList<ArrayList<Receipt.Preview>> mPreviewsPages;
    private ArrayList<String> mDateList;
    private int mUpdatePosition;
    public PagesAdapter(FragmentManager fm, ArrayList<ArrayList<Receipt.Preview>> previewsPages,
                        ArrayList<String> dateList) {
        super(fm);
        mPreviewsPages = previewsPages;
        mDateList = dateList;
        mUpdatePosition = -1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.setTag(position);
        return super.instantiateItem(container, position);

    }

    @Override
    public int getItemPosition(Object object) {
        Preview preview = (Preview) object;
        int position = preview.getPosition();
        if (position == mUpdatePosition) {
            mUpdatePosition = -1;
            return POSITION_NONE;
        } else {
            return super.getItemPosition(object);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return new Preview(mPreviewsPages.get(position), position);
    }

    @Override
    public int getCount() {
        return mPreviewsPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDateList.get(position);
    }

    public void setUpdatePosition(int position) {
        mUpdatePosition = position;
    }

}
