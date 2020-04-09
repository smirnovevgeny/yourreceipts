package ru.controlprograms.yourecipts.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.controlprograms.yourecipts.broadcastReceivers.GetReceiptReceiver;
import ru.controlprograms.yourecipts.database.DbHelper;
import ru.controlprograms.yourecipts.model.Receipt;
import ru.controlprograms.yourecipts.utils.Image;
import ru.controlprograms.yourecipts.utils.ParseJSON;

/**
 * Created by evgeny on 04.08.16.
 */

public class GetReceipt extends AsyncTask {

    private Context mContext;
    private String mPath;
    private ProgressDialog progress;
    private String ADDRESS_URL = "http://52.43.234.125/sendImage/";
    private String REQUEST_POST = "POST";
    private String ORIENTATION = "ORIENTATION";
    public static String RESULT_RECEIPTS = "resultReceipts";
    private int mReceiptAddId;

    public GetReceipt(Context context, String path, int receiptAddId) {
        mContext = context;
        mPath = path;
        mReceiptAddId = receiptAddId;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        HttpURLConnection httpURLConnection = null;
        FileInputStream fileInputStream = null;
        String fileName = "filename";
        DataOutputStream dataOutputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        try {
            File image = new File(mPath);
            fileInputStream = new FileInputStream(image);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            URL url = new URL(ADDRESS_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true); // Allow Inputs
            httpURLConnection.setDoOutput(true); // Allow Outputs
            httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
            httpURLConnection.setRequestMethod(REQUEST_POST);
            GetReceiptReceiver.notifyReceiver(mContext, GetReceiptReceiver.START, null, mReceiptAddId);
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty(ORIENTATION, Integer.toString(Image.rotationAngle(mPath)));
            httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setRequestProperty("uploaded_file", fileName);
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0){
                //write the bytes read from inputstream
                Log.d("loop", "again");
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int responseCode = httpURLConnection.getResponseCode();

            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : ");
            System.out.println("Response Code : " + responseCode);

            final StringBuilder output = new StringBuilder("Request URL " + url);
            output.append(System.getProperty("line.separator") + "Request Parameters ");
            output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
            output.append(System.getProperty("line.separator")  + "Type " + "POST");
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = "";
            StringBuilder receiptResult = new StringBuilder();
            while((line = br.readLine()) != null ) {
                receiptResult.append(line);
                Log.d("line", line);
            }
            br.close();

            ParseJSON parseJSON = new ParseJSON(receiptResult.toString());
            Receipt receipt = parseJSON.getReceipt();

            DbHelper dbHelper = new DbHelper(mContext);
            long receipt_id = dbHelper.insertReceipt(receipt);
            receipt.setReceiptId(receipt_id);
            if (responseCode == 200) {
                GetReceiptReceiver.notifyReceiver(mContext, GetReceiptReceiver.FINISH, receipt.getPreview(), mReceiptAddId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.flush();
                    dataOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
