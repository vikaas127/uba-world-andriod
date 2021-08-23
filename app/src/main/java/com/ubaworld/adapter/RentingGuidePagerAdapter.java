package com.ubaworld.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ubaworld.fragment.GuideWebViewFragment;

public class RentingGuidePagerAdapter extends FragmentPagerAdapter {

    private int operation;
    private int itemCount;

    public RentingGuidePagerAdapter(FragmentManager fragmentManager, int itemCount, int operation) {
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
                    return GuideWebViewFragment.newInstance("private_renting_first.html");
                case 1:
                    return GuideWebViewFragment.newInstance("private_renting_second.html");
                case 2:
                    return GuideWebViewFragment.newInstance("private_renting_third.html");
                case 3:
                    return GuideWebViewFragment.newInstance("private_renting_fourth.html");
                case 4:
                    return GuideWebViewFragment.newInstance("private_renting_fifth.html");
                case 5:
                    return GuideWebViewFragment.newInstance("private_renting_sixth.html");
                case 6:
                    return GuideWebViewFragment.newInstance("private_renting_seventh.html");
                default:
                    return null;
            }
        else
            switch (position) {
                case 0:
                    return GuideWebViewFragment.newInstance("land_lords_first.html");
                case 1:
                    return GuideWebViewFragment.newInstance("land_lords_second.html");
                case 2:
                    return GuideWebViewFragment.newInstance("land_lords_third.html");
                case 3:
                    return GuideWebViewFragment.newInstance("land_lords_fourth.html");
                case 4:
                    return GuideWebViewFragment.newInstance("land_lords_fifth.html");
                default:
                    return null;
            }
    }

}
