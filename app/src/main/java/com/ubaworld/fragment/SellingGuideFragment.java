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

public class SellingGuideFragment extends Fragment {

    public SellingGuideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selling_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.frame_SellingGuideFirst, R.id.frame_SellingGuideSecond})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.frame_SellingGuideFirst:
                Intent intent1 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent1.putExtra("type", "sellingGuide_First");
                startActivity(intent1);
                break;

            case R.id.frame_SellingGuideSecond:
                Intent intent2 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent2.putExtra("type", "sellingGuide_Second");
                startActivity(intent2);
                break;


        }
    }

}
