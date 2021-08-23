package com.ubaworld.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubaworld.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ubaworld.utils.Constants.ACKNOWLEDGMENT_URL;
import static com.ubaworld.utils.Constants.DISCLAIMER_URL;
import static com.ubaworld.utils.Constants.PRIVACY_POLICY_URL;
import static com.ubaworld.utils.Constants.TERMS_CONDITION_URL;
import static com.ubaworld.utils.Constants.WEBSITE_URL;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.showViews;

public class LegalWebViewActivity extends AppCompatActivity {

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.web_View)
    WebView web_View;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_web_view);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
        }

        initView();
    }

    private void initView() {
        web_View.setWebViewClient(new WebViewClient());
        web_View.getSettings().setLoadsImagesAutomatically(true);
        web_View.getSettings().setJavaScriptEnabled(true);
        web_View.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        if (type.equalsIgnoreCase("Privacy Policy")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_privacy_policy));
            web_View.loadUrl(PRIVACY_POLICY_URL);
        } else if (type.equalsIgnoreCase("Terms and Conditions")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_terms_and_conditions));
            web_View.loadUrl(TERMS_CONDITION_URL);
        } else if (type.equalsIgnoreCase("Acknowledgment")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_acknowledgment));
            web_View.loadUrl(ACKNOWLEDGMENT_URL);
        } else if (type.equalsIgnoreCase("Website")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_website));
            web_View.loadUrl(WEBSITE_URL);
        } else if (type.equalsIgnoreCase("Disclaimer")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_disclaimer));
            web_View.loadUrl(DISCLAIMER_URL);

        }
    }

    @OnClick({R.id.ll_Left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_Left:
                finish();
                break;

        }
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideViews(progressBar);
            showViews(web_View);
        }
    }

}
