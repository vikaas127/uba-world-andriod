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
import com.ubaworld.activity.CouncilTaxActivity;
import com.ubaworld.activity.HTMLWebViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class UtilitiesFragment extends Fragment {

    public UtilitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_utilities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.frame_UtilitiesFirst, R.id.frame_UtilitiesSecond, R.id.frame_UtilitiesThird})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.frame_UtilitiesFirst:
                Intent intent1 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent1.putExtra("type", "utilities_First");
                startActivity(intent1);
                break;

            case R.id.frame_UtilitiesSecond:
                Intent intent2 = new Intent(getActivity(), CouncilTaxActivity.class);
                startActivity(intent2);
                break;

            case R.id.frame_UtilitiesThird:
                Intent intent3 = new Intent(getActivity(), HTMLWebViewActivity.class);
                intent3.putExtra("type", "utilities_Third");
                startActivity(intent3);
                break;

        }
    }


}
