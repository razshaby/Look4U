package com.razchen.look4u;

import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "שלב 1\nבחירת קטגוריה", "שלב 2\nסינון משתמשים", "שלב 3\nבחירת שאלות" };
    public Fragment[] fragments = new Fragment[tabTitles.length];

    public SampleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new selectCategory_fragment();
            case 1:
                return new FilterUsers_fragment();
            case 2:
                return new Create_questions_fragment();
            default:
                return null;
        }    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        fragments[position]  = createdFragment;
        return createdFragment;
    }
}