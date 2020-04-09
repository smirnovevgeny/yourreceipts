package ru.controlprograms.yourecipts.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import ru.controlprograms.yourecipts.asyncTasks.GetReceipt;
import ru.controlprograms.yourecipts.R;
import ru.controlprograms.yourecipts.adapters.PagesAdapter;
import ru.controlprograms.yourecipts.database.DbHelper;
import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.Image;


public class MainActivity extends AppCompatActivity{

    private static final int CAMERA_REQ = 0;
    private static final int GALLERY_REQ = 1;
    public static final int LAUNCH_RECEIPT_ACTIVITY = 2;
    public static final String ADD_RECEIPT_BUTTON = "add_receipt";
    private PagesAdapter mPagesAdapter;
    private ArrayList<ArrayList<Receipt.Preview>> mPreviewsPages;
    private ArrayList<String> mDateList;
    private int mReceiptAddId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, IntroductionActivity.class);
//        startActivity(intent);
        prepareAdapterData();
        mPagesAdapter = new PagesAdapter(getSupportFragmentManager(), mPreviewsPages, mDateList);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mPagesAdapter);

    }

    private void prepareAdapterData() {
        DbHelper dbHelper = new DbHelper(this);
        ArrayList<Receipt.Preview> previews = dbHelper.getReceiptPreview();
        mPreviewsPages = new ArrayList<>();
        mDateList = new ArrayList<>();
        ArrayList<Receipt.Preview> currentPreviews = new ArrayList<>();

        String currentDate, previousDate;
        int previewsN = previews.size();
        Receipt.Preview currentPreview;

        currentPreview = previews.get(0);
        previousDate = currentPreview.getDate().getDate();

        currentPreviews.add(currentPreview);
        mDateList.add(previousDate);

        for (int i=1; i < previewsN; i++) {
            currentPreview = previews.get(i);
            currentDate = currentPreview.getDate().getDate();
            if (currentDate.equals(previousDate)) {
                currentPreviews.add(currentPreview);
            } else {
                mDateList.add(currentDate);
                Collections.reverse(currentPreviews);
                mPreviewsPages.add(currentPreviews);
                currentPreviews = new ArrayList<>();
                currentPreviews.add(currentPreview);
            }
            previousDate = currentDate;
        }

        mDateList.add(previousDate);
        Collections.reverse(currentPreviews);
        mPreviewsPages.add(currentPreviews);

        dbHelper.close();
    }

    public void onAddInternet(View view) {
        if (isConnected()) {
            switch (view.getId()) {
                case R.id.gallery:
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQ);
                    break;
                case R.id.photo:
                    Intent cameraIntent = Image.capturePhotoIntent(this);
                    startActivityForResult(cameraIntent, CAMERA_REQ);
                    break;
            }
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onAddHandClick(View view) {
        Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
        intent.putExtra(ADD_RECEIPT_BUTTON, true);
        startActivityForResult(intent, LAUNCH_RECEIPT_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null && requestCode == CAMERA_REQ || requestCode == GALLERY_REQ) {

                if (requestCode == CAMERA_REQ) {
                    String path = Image.getFullFileName(this);
                    mReceiptAddId++;
                    new GetReceipt(this, path, mReceiptAddId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else if (requestCode == GALLERY_REQ) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String path = Image.getRealPathFromUri(this, uri);
                        mReceiptAddId++;
                        new GetReceipt(this, path, mReceiptAddId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            } else {
                if (requestCode == LAUNCH_RECEIPT_ACTIVITY) {
                    int changeType = data.getIntExtra(ReceiptActivity.CHANGE_TYPE, ReceiptActivity.NO_CHANGE);
                    String gsoned = data.getStringExtra(ReceiptActivity.RESULT_RECEIPT);
                    Gson gson = new Gson();
                    Receipt.Preview previewData =  gson.fromJson(gsoned, Receipt.Preview.class);
                    switch (changeType) {
                        case ReceiptActivity.INSERTED:
                            mPreviewsPages.get(0).add(previewData);
                            mPagesAdapter.setUpdatePosition(0);
                            break;
                        case ReceiptActivity.CHANGED:
                            int pagePosition = data.getIntExtra(ReceiptActivity.CHANGED_PAGE_POSITION, -1);
                            int itemPosition = data.getIntExtra(ReceiptActivity.CHANGED_ITEM_POSITION, -1);
                            mPreviewsPages.get(pagePosition).get(itemPosition).setPreview(previewData);
                            mPagesAdapter.setUpdatePosition(pagePosition);
                            break;
                    }
                    mPagesAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

}
