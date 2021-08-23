package com.ubaworld.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ubaworld.fragment.GuideWebViewFragment;

public class UtilitiesGuidePagerAdapter extends FragmentPagerAdapter {

    private int itemCount;
    private int operation;

    public UtilitiesGuidePagerAdapter(FragmentManager fragmentManager, int itemCount, int operation) {
        super(fragmentManager);
        this.itemCount = itemCount;
        this.operation = operation;
    }

    @Override
    public int getCount() {
        return itemCount;
    }

    @Override
    public Fragment getItem(int position) {
        if (operation == 1)
            switch (position) {
                case 0:
                    return GuideWebViewFragment.newInstance("utilities_guide_first.html");
                case 1:
                    return GuideWebViewFragment.newInstance("utilities_guide_second.html");
                case 2:
                    return GuideWebViewFragment.newInstance("utilities_guide_third.html");
                case 3:
                    return GuideWebViewFragment.newInstance("utilities_guide_fourth.html");
                case 4:
                    return GuideWebViewFragment.newInstance("utilities_guide_fifth.html");
                default:
                    return null;
            }
        else
            switch (position) {
                case 0:
                    return GuideWebViewFragment.newInstance("utilities_guide_sixth.html");
                case 1:
                    return GuideWebViewFragment.newInstance("utilities_guide_seventh.html");
                case 2:
                    return GuideWebViewFragment.newInstance("utilities_guide_eighth.html");
                default:
                    return null;
            }

    }

}
