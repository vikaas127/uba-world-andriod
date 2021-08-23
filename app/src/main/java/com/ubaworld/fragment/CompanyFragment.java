package com.ubaworld.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubaworld.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompanyFragment extends Fragment {

    public CompanyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initView();
    }

    private void initView() {
//        SpannableStringBuilder str = new SpannableStringBuilder(getResources().getString(R.string.str_company_line3));
//        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 4, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        str.setSpan(new ForegroundColorSpan(Color.BLACK), 4, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tv_Spannable.setText(str);
    }

}
