package com.razchen.look4u;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter_main extends FragmentPagerAdapter {

    private int numOfTabs;
    public PageAdapter_main(FragmentManager fm,int numOfTabs) {
        super(fm);
        this.numOfTabs=numOfTabs;
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
        }
    }

    @Override
    public int getCount() {

        return numOfTabs;
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
