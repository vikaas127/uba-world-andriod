package com.ubaworld.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubaworld.R;
import com.ubaworld.adapter.UniversityAdapter;
import com.ubaworld.model.UniversityData;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.ubaworld.utils.SaveSharedPreference.getStringFromUserDefaults;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.saveToUserDefaults;
import static com.ubaworld.utils.Utils.setWindowFlag;

public class SelectUniversityActivity extends AppCompatActivity {

    @BindView(R.id.main_view)
    RelativeLayout main_view;

    @BindView(R.id.tvUniversity)
    TextView tvUniversity;

    private List<UniversityData.Data> universityList = new ArrayList<>();
    private UniversityAdapter adapter_University;

    private String university;
    private String universityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_university);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        university = getStringFromUserDefaults(this, Constants.UNIVERSITY_LIST);
        initView();
    }

    private void initView() {
        Type type = new TypeToken<List<UniversityData.Data>>() {
        }.getType();
        universityList = new Gson().fromJson(university, type);
        tvUniversity.setText(universityList.get(0).name);
        LogUtils.e("list_Reply", " ---> " + universityList.size() + logLine());
    }

    @OnClick({R.id.llUniversity, R.id.btn_Select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llUniversity:
                showUniversityPopup();
                break;

            case R.id.btn_Select:
                saveToUserDefaults(this, Constants.UNIVERSITY_NAME, tvUniversity.getText().toString());
                saveToUserDefaults(this, Constants.UNIVERSITY_ID, universityId);
                finish();
                break;

        }
    }

    private void showUniversityPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) SelectUniversityActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_popup_dialog, null);

        TextView tvHeader = view.findViewById(R.id.tvHeader);
        SearchView searchView = view.findViewById(R.id.search_view);
        final RecyclerView rcvAlertPopup = view.findViewById(R.id.rcvAlertPopup);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(main_view, Gravity.CENTER, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) SelectUniversityActivity.this.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        tvHeader.setText(getResources().getString(R.string.str_select_university));

        adapter_University = new UniversityAdapter(SelectUniversityActivity.this, universityList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(SelectUniversityActivity.this, 1);
        rcvAlertPopup.setLayoutManager(layoutManager);
        rcvAlertPopup.setAdapter(adapter_University);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter_University.getFilter().filter(newText);
                return false;
            }
        });

        adapter_University.setClickListener((view1, name, id) -> {
            popupWindow.dismiss();
            tvUniversity.setText(name);
            universityId = id;
            LogUtils.e("UNIVERSITY", "TITLE ---> " + name + " || ID ---> " + id + logLine());
        });
    }

}