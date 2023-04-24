package com.gojavas.taskforce.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.gojavas.taskforce.ui.activity.LoginActivity;

import java.io.File;

/**
 * Created by GJS280 on 18/8/2015.
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

DownloadManager
        downloadmanager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);

         String Folder_Name="GoJavasApk";

        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (completeDownloadId == LoginActivity.downloadId) {

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(completeDownloadId);
                Cursor c = downloadmanager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String fileName= c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));

//                        Toast.makeText(LoginActivity.this, completeDownloadId + "", Toast.LENGTH_LONG).show();
//                initData();
//                updateView();
                        // if download successful, install apk
                        String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                                .append(File.separator).append(Folder_Name).append(File.separator)
                                .append(fileName).toString();
                        Log.i("apkpath",apkFilePath);

                        Toast.makeText(context,apkFilePath,Toast.LENGTH_LONG).show();
                        install(context, apkFilePath);

                    }
                }


            }
        }

    }



    /**
     * install app
     *
     * @param context
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            return true;
        }
        return false;
    }
}
