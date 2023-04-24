package com.gojavas.taskforce.manager;

import android.app.IntentService;
import android.content.Intent;

import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by GJS280 on 27/5/2015.
 */
public class UploadManager extends IntentService {

    public static final String UPLOAD_FILE_PATH = "UploadFilePath";

    private String mUploadFilePath;
    private String mUploadFileName;
    private String mUploadUrl = Constants.UPLOAD_URL;

    public UploadManager() {
        super("Upload Manager");
    }
    
    public UploadManager(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mUploadFilePath = intent.getStringExtra(UPLOAD_FILE_PATH);
        mUploadFileName = Utility.getFileNameFromPath(mUploadFilePath);
        System.out.println(mUploadFileName + " : uploading start");
        String result = fileUpload(mUploadUrl, mUploadFilePath, mUploadFileName);
        System.out.println("uploading response: " + result);
    }

    /**
     * Uploade file
     * @param uploadUrl
     * @param filePath
     */
    public static String fileUpload(String uploadUrl, String filePath, String fileName) {
        System.out.println("upload url: " + uploadUrl);
        System.out.println("filePath: " + filePath);
        System.out.println("fileUrl: " + fileName);
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*100*1024; // 512 KB
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath) );
            URL url = new URL(uploadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data;name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes("Content-Type: image/png" + lineEnd);
            dos.writeBytes(lineEnd);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                System.out.println(fileName + " === " + bytesRead + " : uploading");
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();
            dos.close();
        }
        catch (MalformedURLException murle) {
            murle.printStackTrace();
            System.out.println(fileName + " : uploading exception 1");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println(fileName + " : uploading exception 2");
        }

        // Read server response
        String serverResponse = "";
        try {
            inStream = new DataInputStream ( conn.getInputStream() );
            String response = "";
            while (( response = inStream.readLine()) != null) {
                serverResponse += response;
                System.out.println("response: " + serverResponse);
            }
            inStream.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println(fileName + " : uploading exception 3");
        }
        return serverResponse;
    }
}
