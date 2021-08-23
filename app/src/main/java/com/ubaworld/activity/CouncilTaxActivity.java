package com.ubaworld.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ubaworld.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouncilTaxActivity extends AppCompatActivity {

    @BindView(R.id.llView)
    LinearLayout llView;

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_council_tax);
        ButterKnife.bind(this);

        tv_HeaderTitle.setText(getResources().getString(R.string.str_council_tax));
    }

    @OnClick({R.id.iv_Left, R.id.ll_CouncilFirst, R.id.ll_CouncilSecond, R.id.ll_CouncilThird, R.id.ll_CouncilFourth})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.iv_Left:
                finish();
                break;

            case R.id.ll_CouncilFirst:
                showPopupWindow(getResources().getString(R.string.str_title_council_first),
                        getResources().getString(R.string.str_description_council_first), false);
                break;

            case R.id.ll_CouncilSecond:
                showPopupWindow(getResources().getString(R.string.str_title_council_second),
                        getResources().getString(R.string.str_description_council_second), false);
                break;

            case R.id.ll_CouncilThird:
                showPopupWindow(getResources().getString(R.string.str_title_council_third),
                        getResources().getString(R.string.str_description_council_third), false);
                break;

            case R.id.ll_CouncilFourth:
                showPopupWindow(getResources().getString(R.string.str_title_council_fourth),
                        getResources().getString(R.string.str_description_council_fourth), true);
                break;

        }
    }

    private void showPopupWindow(String s1, String s2, Boolean isSpannable) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.council_tax_popup_dialog, null);

        TextView tv_Title = view.findViewById(R.id.tv_Title);
        TextView tv_Description = view.findViewById(R.id.tv_Description);
        Button btn_Done = view.findViewById(R.id.btn_Done);

        tv_Title.setText(s1);

        if (isSpannable) {
//            SpannableStringBuilder builder = new SpannableStringBuilder();

            SpannableString ss = new SpannableString(s2);
//            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 23, 36, 0);
//            builder.append(ss);
//            tv_Description.setText(builder, TextView.BufferType.SPANNABLE);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gov.uk/find-local-council"));
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.blue));
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(clickableSpan, 23, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_Description.setText(ss);
            tv_Description.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tv_Description.setText(s2);
        }

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.CENTER, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        btn_Done.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
    }

}
