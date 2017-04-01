package com.nirhart.shortrain.tutorial;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.nirhart.shortrain.R;

public class TutorialViewPagerAdapter extends FragmentPagerAdapter {

    public TutorialViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TutorialFragment.create(R.raw.tutorial_1, R.string.tutorial_1);
            case 1:
                return TutorialFragment.create(R.raw.tutorial_2, R.string.tutorial_2);
            case 2:
                return TutorialFragment.create(R.raw.tutorial_3, R.string.tutorial_3);
            case 3:
                return TutorialFragment.create(R.raw.tutorial_4, R.string.tutorial_4);
            case 4:
                return TutorialFragment.create(R.raw.tutorial_5, R.string.tutorial_5);
        }

        throw new UnsupportedOperationException("Each tutorial page must have its fragment");
    }

    @Override
    public int getCount() {
        return 5;
    }
}
