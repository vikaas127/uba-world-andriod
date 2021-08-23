package com.ubaworld.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubaworld.R;
import com.ubaworld.activity.LegalWebViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LegalFragment extends Fragment {

    public LegalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_legal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.tv_PrivacyPolicy, R.id.tv_TermsCondition, R.id.tv_Acknowledgment, R.id.tv_Website, R.id.tv_Disclaimer})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.tv_PrivacyPolicy:
                callIntent("Privacy Policy");
                break;

            case R.id.tv_TermsCondition:
                callIntent("Terms and Conditions");
                break;

            case R.id.tv_Acknowledgment:
                callIntent("Acknowledgment");
                break;

            case R.id.tv_Website:
                callIntent("Website");
                break;

            case R.id.tv_Disclaimer:
                callIntent("Disclaimer");
                break;

        }
    }

    private void callIntent(String type) {
        Intent intent = new Intent(getActivity(), LegalWebViewActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

}
