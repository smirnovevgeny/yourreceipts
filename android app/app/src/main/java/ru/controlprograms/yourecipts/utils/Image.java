package ru.controlprograms.yourecipts.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by evgeny on 03.08.16.
 */

public class Image {

    private static final String FILE_NAME = "capturedImage";
    private static final String IMAGE = "image";

    public static Intent capturePhotoIntent(Context context) {
        String fileName = getFullFileName(context);
        Uri uri = Uri.fromFile(new File(fileName));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    public static String getFullFileName(Context context) {
        File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (filesDir != null) {
            String path = filesDir.getPath();
            return path + "/" + FILE_NAME;
        } else {
            return null;
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int rotationAngle(String filename) throws IOException {
        ExifInterface exifInterface = new ExifInterface(filename);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationAngle = 0;

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationAngle = 90;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationAngle = 180;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationAngle = 270;
        }
        return rotationAngle;
    }


}
