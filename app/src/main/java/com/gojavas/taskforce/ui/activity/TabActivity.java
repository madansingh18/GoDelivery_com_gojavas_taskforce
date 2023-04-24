package com.gojavas.taskforce.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.ui.fragment.ViewPagerAdapter;
import com.gojavas.taskforce.ui.tab.SlidingTabLayout;

/**
 * Created by GJS280 on 21/4/2015.
 */
public class TabActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    SearchView mSearchView;
    private CharSequence mTitles[] = {"PENDING", "SUCCESS", "FAILED"};
    int mNumberOfTabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job List");

        registerViews();
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        modifyTabs();

        mViewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), mTitles, mNumberOfTabs);
        mViewPager.setAdapter(mViewPagerAdapter);

        // Setting the ViewPager For the SlidingTabsLayout
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });



    }




    /**
     * Modifying the tabs color
     */
    private void modifyTabs() {
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.app_color_dark);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
              finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu_tab, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
////                mViewPager.getCurrentItem();
//                PendingFragment.mDocketListAdapter.filter(newText.toLowerCase());
//                return false;
//            }
//        });
//        return  true;
//    }
}
