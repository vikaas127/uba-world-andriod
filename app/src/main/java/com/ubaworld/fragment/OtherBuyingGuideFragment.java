package com.ubaworld.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubaworld.R;
import com.ubaworld.adapter.OtherBuyingOptionAdapter;
import com.ubaworld.model.OtherBuyingData;
import com.ubaworld.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static com.ubaworld.utils.Utils.logLine;

public class OtherBuyingGuideFragment extends Fragment {

    @BindView(R.id.llView)
    LinearLayout llView;

    @BindView(R.id.rcv_OtherBuyingOption)
    RecyclerView rcv_OtherBuyingOption;

    private List<OtherBuyingData> list = new ArrayList<>();

    public OtherBuyingGuideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_buying_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        loadJSONFile();
    }

    private void loadJSONFile() {
        try {
            InputStream is = getActivity().getAssets().open("buying_guide_fourth_one.json");

            Gson gson = new Gson();
            Reader reader = new InputStreamReader(is);
            Type listType = new TypeToken<List<OtherBuyingData>>(){}.getType();
            list = gson.fromJson(reader, listType);
            LogUtils.e("list", ":: " + list.size() + logLine());

            OtherBuyingOptionAdapter adapter = new OtherBuyingOptionAdapter(getActivity(), list);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
            rcv_OtherBuyingOption.setLayoutManager(layoutManager);
            rcv_OtherBuyingOption.setAdapter(adapter);

            adapter.setClickListener((view, position) -> showPopupWindow(position));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showPopupWindow(int i) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.council_tax_popup_dialog, null);

        TextView tv_Title = view.findViewById(R.id.tv_Title);
        TextView tv_Description = view.findViewById(R.id.tv_Description);
        Button btn_Done = view.findViewById(R.id.btn_Done);

        String title = list.get(i).getTitle();
        String description = list.get(i).getDescription();
        title = title.replaceAll("\n", "<br/>");

        tv_Title.setText(Html.fromHtml(title));
        tv_Description.setText(description);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.CENTER, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        btn_Done.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
    }

}
