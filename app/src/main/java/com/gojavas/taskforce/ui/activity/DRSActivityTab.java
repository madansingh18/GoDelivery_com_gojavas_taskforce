package com.gojavas.taskforce.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.gojavas.taskforce.R;

/**
 * Created by MadanS on 12/23/2017.
 */
public class DRSActivityTab extends TabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drs_tab);

        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
//        TabHost tabHost=getTabHost();
        // Android tab
        Intent intentAndroid = new Intent().setClass(this, DRSActivity.class);
        TabHost.TabSpec tabSpecAndroid = tabHost
                .newTabSpec("Android")
                .setContent(intentAndroid);

        // Apple tab
        Intent intentApple = new Intent().setClass(this, DRSActivity.class);
        TabHost.TabSpec tabSpecApple = tabHost
                .newTabSpec("Apple")
                .setContent(intentApple);

        // add all tabs
        tabHost.addTab(tabSpecAndroid);
        tabHost.addTab(tabSpecApple);

        //set Windows tab as default (zero based)
        tabHost.setCurrentTab(1);
    }

}
