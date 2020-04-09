package ru.controlprograms.yourecipts.utils;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ru.controlprograms.yourecipts.R;

/**
 * Created by evgeny on 11.08.16.
 */

public class InputCheck {
    private HashSet<Integer> mTotal, mForItem, mQuantity, mName;
    public static final int FOR_ITEM = 0;
    public static final int QUANTITY = 1;
    public static final int TOTAL = 2;
    public static final int NAME = 3;

    public InputCheck() {
        mForItem = new HashSet<Integer>();
        mQuantity = new HashSet<Integer>();
        mTotal = new HashSet<Integer>();
        mName = new HashSet<Integer>();
    }

    public void total(String string, int position) {
        doubleCheck(mTotal, string, position);
    }

    public void forItem(String string, int position) {
        doubleCheck(mForItem, string, position);
    }

    public void quantity(String string, int position) {
        doubleCheck(mQuantity, string, position);
    }

    public void name(String string, int position) {
        if (string.isEmpty()) {
            mName.add(position);
        } else {
            if (mName.contains(position)) {
                mName.remove(position);
            }
        }
    }

    public WrongPosition getWrongPosition() {
        int position = -1;
        int itemType = -1;
        int checkPosition;
        try {
            position = Collections.min(mForItem);
            itemType = FOR_ITEM;
            } catch (NoSuchElementException e) {

        }
        try {
            checkPosition = Collections.min(mQuantity);
            if (position == -1 || position != -1 && checkPosition < position) {
                position = checkPosition;
                itemType = QUANTITY;
            }
        } catch (NoSuchElementException e) {

        }
        try {
            checkPosition = Collections.min(mTotal);
            if (position == -1 || position != -1 && checkPosition < position) {
                position = checkPosition;
                itemType = TOTAL;
            }
        } catch (NoSuchElementException e) {

        }
        try {
            checkPosition = Collections.min(mName);
            if (position == -1 || position != -1 && checkPosition < position) {
                position = checkPosition;
                itemType = NAME;
            }
        } catch (NoSuchElementException e) {

        }
        if (position != -1) {
            return new WrongPosition(position, itemType);
        } else {
            return null;
        }
    }

    private void doubleCheck(HashSet<Integer> set, String string, int position) {
        if (getDouble(string) != null) {
            if (set.contains(position)) {
                set.remove(position);
            }
        } else {
            set.add(position);
        }

    }

    private Double getDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void onDismiss(int position) {
        update(mForItem, position);
        update(mTotal, position);
        update(mName, position);
        update(mQuantity, position);
    }

    private void update(HashSet<Integer> set, int position) {
        HashSet<Integer> addition = new HashSet<Integer>();
        HashSet<Integer> deduction = new HashSet<Integer>();
        System.out.print(set.isEmpty());
        for (Integer i : set) {
            if (i >= position) {
                deduction.add(i);
                if (i != position) {
                    addition.add(i-1);
                }
            }
        }
        set.removeAll(deduction);
        set.addAll(addition);
    }


    public class WrongPosition {
        private int mPosition, mType;

        public WrongPosition(int position, int type) {
            mPosition = position;
            mType = type;
        }

        public int getPosition() {
            return mPosition;
        }

        public int getType() {
            return mType;
        }

    }
}
