package com.ubaworld.fragment;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ubaworld.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideWebViewFragment extends Fragment {

    @BindView(R.id.web_View)
    WebView web_View;

    private String html_FileName;

    public GuideWebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide_web_view, container, false);
    }

    public static GuideWebViewFragment newInstance(String fileName) {
        GuideWebViewFragment fragment = new GuideWebViewFragment();
        Bundle args = new Bundle();
        args.putString("fileName", fileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        html_FileName = getArguments().getString("fileName");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loadHtmlFile();
    }

    private void loadHtmlFile() {
        try {
            AssetManager assetManager = getActivity().getAssets();
            InputStream stream = assetManager.open(html_FileName);
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append("\n");
            }
            web_View.loadDataWithBaseURL(null, total.toString(), "text/html", "UTF-8", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
