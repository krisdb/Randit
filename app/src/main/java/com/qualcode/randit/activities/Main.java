package com.qualcode.randit.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.qualcode.randit.R;
    import com.qualcode.randit.fragments.PostListFragment;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {
    public ViewPager mViewPager;
    public MyFragmentPagerAdapter mMyFragmentPagerAdapter;
    private static List<String> mSubreddits = new ArrayList<String>();
    private static int NUMBER_OF_PAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSubreddits.add("all");
        mSubreddits.add("nba");
        mSubreddits.add("hearthstone");
        mSubreddits.add("heroesofthestorm");
        mSubreddits.add("android");

        mViewPager = (ViewPager)findViewById(R.id.pager);

        NUMBER_OF_PAGES = mSubreddits.size();

        mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMyFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
    }

    private static class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return PostListFragment.init(mSubreddits.get(position));
        }


        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
