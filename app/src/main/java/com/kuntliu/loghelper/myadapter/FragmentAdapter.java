package com.kuntliu.loghelper.myadapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kuntliu.loghelper.TabFragment;

import java.util.ArrayList;
import java.util.List;


public class FragmentAdapter extends FragmentPagerAdapter {
    private final ArrayList<String> myTabs;
    private final List<TabFragment> tabFragmentList;

    public FragmentAdapter(@NonNull FragmentManager fm, ArrayList<String> myTabs, List<TabFragment> tabFragmentList) {
        super(fm);
        this.myTabs = myTabs;
        this.tabFragmentList = tabFragmentList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return tabFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return tabFragmentList.size();
    }

    public CharSequence getPageTitle(int position) {
        Log.d("FragmentAdapter", "getPageTitle: "+myTabs+position);
        return myTabs.get(position);
    }
}
