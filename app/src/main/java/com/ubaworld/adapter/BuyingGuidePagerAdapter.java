package com.ubaworld.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ubaworld.fragment.GuideWebViewFragment;
import com.ubaworld.fragment.OtherBuyingGuideFragment;

public class BuyingGuidePagerAdapter extends FragmentPagerAdapter {

    private int operation;
    private int itemCount;

    public BuyingGuidePagerAdapter(FragmentManager fragmentManager, int itemCount, int operation) {
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
                    return GuideWebViewFragment.newInstance("buying_guide_first.html");
                default:
                    return null;
            }
        else if (operation == 2)
            switch (position) {
                case 0:
                    return GuideWebViewFragment.newInstance("buying_guide_second.html");
                default:
                    return null;
            }
        else if (operation == 3)
            switch (position) {
                case 0:
                    return GuideWebViewFragment.newInstance("buying_guide_third.html");
                default:
                    return null;
            }
        else
            switch (position) {
                case 0:
                    return new OtherBuyingGuideFragment();
                case 1:
                    return GuideWebViewFragment.newInstance("buying_guide_fourth.html");
                case 2:
                    return GuideWebViewFragment.newInstance("buying_guide_fifth.html");
                default:
                    return null;
            }

    }

}
