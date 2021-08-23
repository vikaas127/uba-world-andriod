package com.ubaworld.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubaworld.R;
import com.ubaworld.activity.HTMLWebViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BuyingGuideFragment extends Fragment {

    public BuyingGuideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buying_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.frame_BuyingGuideFirst, R.id.frame_BuyingGuideSecond, R.id.frame_BuyingGuideThird, R.id.frame_BuyingGuideFourth})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.frame_BuyingGuideFirst:
                Intent intent1 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent1.putExtra("type", "buyingGuide_First");
                startActivity(intent1);
                break;

            case R.id.frame_BuyingGuideSecond:
                Intent intent2 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent2.putExtra("type", "buyingGuide_Second");
                startActivity(intent2);
                break;

            case R.id.frame_BuyingGuideThird:
                Intent intent3 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent3.putExtra("type", "buyingGuide_Third");
                startActivity(intent3);
                break;

            case R.id.frame_BuyingGuideFourth:
                Intent intent4 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent4.putExtra("type", "buyingGuide_Fourth");
                startActivity(intent4);
                break;

        }
    }

}
