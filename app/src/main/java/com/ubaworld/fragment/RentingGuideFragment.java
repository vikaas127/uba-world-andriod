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

public class RentingGuideFragment extends Fragment {

    public RentingGuideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_renting_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.frame_PrivateRenting, R.id.frame_LandlordsRenting})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.frame_PrivateRenting:
                Intent intent = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent.putExtra("type", "private_Renting");
                startActivity(intent);
                break;

            case R.id.frame_LandlordsRenting:
                Intent intent1 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent1.putExtra("type", "landLords");
                startActivity(intent1);
                break;

        }
    }

}
