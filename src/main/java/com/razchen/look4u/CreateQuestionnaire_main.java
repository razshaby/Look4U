package com.razchen.look4u;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class CreateQuestionnaire_main extends UserMenu {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_questionnaire_main);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final SampleFragmentPagerAdapter sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sampleFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        //Credit https://www.edureka.co/community/40326/how-to-refresh-data-in-viewpager-fragment?ranMID=42536&ranEAID=a1LgFw09t88&ranSiteID=a1LgFw09t88-hnmXkB1KlVMXSWWTTfrAyg&LSNSUBSITE=Omitted_a1LgFw09t88
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // do this instead, assuming your adapter reference
                // is named mAdapter:
                Fragment frag = sampleFragmentPagerAdapter.fragments[position];
                if (frag != null && frag instanceof FilterUsers_fragment) {
                    ((FilterUsers_fragment) frag).sendGetRequest();
                }

                if (frag != null && frag instanceof Create_questions_fragment) {
                    ((Create_questions_fragment) frag).sendGetRequest();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //Credit https://stackoverflow.com/questions/46009299/how-to-disable-of-select-some-tab-when-using-tablayout
        //cancel motion with tabs
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }


    }
}
