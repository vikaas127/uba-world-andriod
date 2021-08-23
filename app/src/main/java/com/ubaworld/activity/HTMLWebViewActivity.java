package com.ubaworld.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.ubaworld.R;
import com.ubaworld.adapter.BuyingGuidePagerAdapter;
import com.ubaworld.adapter.RentingGuidePagerAdapter;
import com.ubaworld.adapter.SellingGuidePagerAdapter;
import com.ubaworld.adapter.UtilitiesGuidePagerAdapter;
import com.ubaworld.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;

import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.showViews;

public class HTMLWebViewActivity extends AppCompatActivity {

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.iv_Right)
    ImageView iv_Right;

    @BindView(R.id.view_pager)
    ViewPager view_pager;

    @BindView(R.id.tab_Dots)
    TabLayout tab_Dots;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_web_view);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
        }

        initView();
    }

    private void initView() {

        Glide.with(this).asGif().load(R.drawable.gif_next).into(iv_Right);

        //TODO : Renting Guide
        if (type.equalsIgnoreCase("private_Renting")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_private_renting));

            RentingGuidePagerAdapter adapter = new RentingGuidePagerAdapter(getSupportFragmentManager(),7,1);
            view_pager.setAdapter(adapter);
            tab_Dots.setupWithViewPager(view_pager);

        } else if (type.equalsIgnoreCase("landLords")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_landlords));

            RentingGuidePagerAdapter adapter = new RentingGuidePagerAdapter(getSupportFragmentManager(),5,2);
            view_pager.setAdapter(adapter);
            tab_Dots.setupWithViewPager(view_pager);

        }

        //TODO : Buying Guide
        else if (type.equalsIgnoreCase("buyingGuide_First")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_buying_selling_lingo));
            iv_Right.setVisibility(View.INVISIBLE);

            BuyingGuidePagerAdapter adapter = new BuyingGuidePagerAdapter(getSupportFragmentManager(),1,1);
            view_pager.setAdapter(adapter);

        } else if (type.equalsIgnoreCase("buyingGuide_Second")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_process_of_selling_a_property));
            iv_Right.setVisibility(View.INVISIBLE);

            BuyingGuidePagerAdapter adapter = new BuyingGuidePagerAdapter(getSupportFragmentManager(),1,2);
            view_pager.setAdapter(adapter);

        } else if (type.equalsIgnoreCase("buyingGuide_Third")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_fees_you_need_to_know_about));
            iv_Right.setVisibility(View.INVISIBLE);

            BuyingGuidePagerAdapter adapter = new BuyingGuidePagerAdapter(getSupportFragmentManager(),1,3);
            view_pager.setAdapter(adapter);

        } else if (type.equalsIgnoreCase("buyingGuide_Fourth")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_other_buying_options));
            showViews(iv_Right);

            BuyingGuidePagerAdapter adapter = new BuyingGuidePagerAdapter(getSupportFragmentManager(),3,4);
            view_pager.setAdapter(adapter);
            tab_Dots.setupWithViewPager(view_pager);

        }

        //TODO : Selling Guide
        else if (type.equalsIgnoreCase("sellingGuide_First")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_buying_selling_lingo));
            iv_Right.setVisibility(View.INVISIBLE);

            SellingGuidePagerAdapter adapter = new SellingGuidePagerAdapter(getSupportFragmentManager(),1,1);
            view_pager.setAdapter(adapter);

        } else if (type.equalsIgnoreCase("sellingGuide_Second")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_process_of_selling_a_property));

            SellingGuidePagerAdapter adapter = new SellingGuidePagerAdapter(getSupportFragmentManager(),3,2);
            view_pager.setAdapter(adapter);
            tab_Dots.setupWithViewPager(view_pager);

        }

        //TODO : Utilities
        else if (type.equalsIgnoreCase("utilities_First")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_electric_gas_water));

            UtilitiesGuidePagerAdapter adapter = new UtilitiesGuidePagerAdapter(getSupportFragmentManager(),5,1);
            view_pager.setAdapter(adapter);
            tab_Dots.setupWithViewPager(view_pager);

        } else if (type.equalsIgnoreCase("utilities_Third")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.title_broadband_tv_license));

            UtilitiesGuidePagerAdapter adapter = new UtilitiesGuidePagerAdapter(getSupportFragmentManager(),3,2);
            view_pager.setAdapter(adapter);
            tab_Dots.setupWithViewPager(view_pager);

        }

        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position != 0)
                    iv_Right.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.iv_Left})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_Left) {
            finish();
        }
    }

}
