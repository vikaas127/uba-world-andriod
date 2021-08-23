package com.ubaworld.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ubaworld.fragment.GuideWebViewFragment;

public class SellingGuidePagerAdapter extends FragmentPagerAdapter {

    private int itemCount;
    private int operation;

    public SellingGuidePagerAdapter(FragmentManager fragmentManager, int itemCount, int operation) {
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
        else
            switch (position) {
                case 0:
                    return GuideWebViewFragment.newInstance("selling_guide_second_one.html");
                case 1:
                    return GuideWebViewFragment.newInstance("selling_guide_second_two.html");
                case 2:
                    return GuideWebViewFragment.newInstance("selling_guide_second_three.html");
                default:
                    return null;
            }

    }

}
