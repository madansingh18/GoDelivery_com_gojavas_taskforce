package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gjs331 on 8/21/2015.
 */

public class FileDeleteService extends IntentService {


    public FileDeleteService() {
        super("FileDelete");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        long currenttime=System.currentTimeMillis();

        Date d = new Date( currenttime- (1000 * 60 * 60 * 24));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday=sdf.format(d);

        Date dd = new Date( currenttime- (1000 * 60 * 60 * 24*2));
        String yesterdaydaybefore=sdf.format(dd);

        String rootPath = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE);
        File rootFile = new File(rootPath);

        if(rootFile.exists()) {
            // Clear saved images
            File[] files = rootFile.listFiles();

            for(File file : files) {

                String filename=file.getName();

                if (filename.contains(today) || filename.contains(yesterday) || filename.contains
                        (yesterdaydaybefore)){

                    continue;
                }

                file.delete();

            }
        }
    }
}