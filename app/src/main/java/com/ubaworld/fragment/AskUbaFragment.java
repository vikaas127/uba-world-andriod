package com.ubaworld.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.ubaworld.R;
import com.ubaworld.activity.askUba.AskUbaBuyingActivity;
import com.ubaworld.activity.askUba.AskUbaRentingActivity;
import com.ubaworld.activity.askUba.AskUbaUtilityActivity;
import com.ubaworld.activity.user.SignInActivity;
import com.ubaworld.model.NotificationData;
import com.ubaworld.model.UniversityData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;
import com.ubaworld.utils.SaveSharedPreference;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ubaworld.utils.Constants.UNAUTHORIZED;
import static com.ubaworld.utils.Utils.getFromUserDefaults;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog_withIntent;
import static com.ubaworld.utils.Utils.showValidationError;
import static com.ubaworld.utils.Utils.showViews;

public class AskUbaFragment extends Fragment {

    @BindView(R.id.iv_Renting)
    ImageView iv_Renting;

    @BindView(R.id.iv_Buying)
    ImageView iv_Buying;

    @BindView(R.id.iv_Utility)
    ImageView iv_Utility;

    private String authToken;
    private String user_Id;
    private int comment_type;
    private int is_read;

    public AskUbaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ask_uba, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        authToken = getFromUserDefaults(getActivity(), Constants.AUTH_TOKEN);
        user_Id = getFromUserDefaults(getActivity(), Constants.USER_ID);
    }

    @OnClick({R.id.frame_Renting, R.id.frame_Buying, R.id.frame_Utility})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.frame_Renting:
                Intent intent1 = new Intent(getActivity(), AskUbaRentingActivity.class);
                intent1.putExtra("comment_type", comment_type);
                intent1.putExtra("is_read", is_read);
                startActivity(intent1);
                break;

            case R.id.frame_Buying:
                Intent intent2 = new Intent(getActivity(), AskUbaBuyingActivity.class);
                intent2.putExtra("comment_type", comment_type);
                intent2.putExtra("is_read", is_read);
                startActivity(intent2);
                break;

            case R.id.frame_Utility:
                Intent intent3 = new Intent(getActivity(), AskUbaUtilityActivity.class);
                intent3.putExtra("comment_type", comment_type);
                intent3.putExtra("is_read", is_read);
                startActivity(intent3);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isConnectingToInternet(getActivity())) {
            getUniversityList();
            getNotification();
        }
    }

    private void getUniversityList() {
        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<UniversityData> call = apiService.list_University();

        call.enqueue(new Callback<UniversityData>() {
            @Override
            public void onResponse(Call<UniversityData> call, Response<UniversityData> response) {
                try {
                    if (response.isSuccessful()) {
                        SaveSharedPreference.saveStringToUserDefaults(getActivity(), Constants.UNIVERSITY_LIST, new Gson().toJson(response.body().data));
                        LogUtils.e("SIZE", "UNIVERSITY LIST ---> " + new Gson().toJson(response.body().data.size()) + logLine());

                    } else {
                        try {
                            LogUtils.e("ERROR", " ---> " + response.raw().message() + logLine());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UniversityData> call, Throwable t) {
                t.printStackTrace();
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void getNotification() {
        if (!isConnectingToInternet(getActivity()))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.notification(user_Id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        NotificationData json = new Gson().fromJson(response.body().string(), NotificationData.class);
                        if (json.data.size() > 0) {
                            for (int i = 0; i < json.data.size(); i++) {
                                comment_type = json.data.get(i).type;
                                is_read = json.data.get(i).is_read;
                                if (comment_type == 1 && is_read == 0)
                                    showViews(iv_Renting);
                                else if (comment_type == 2 && is_read == 0)
                                    showViews(iv_Buying);
                                else if (comment_type == 3 && is_read == 0)
                                    showViews(iv_Utility);
                            }
                        } else {
                            comment_type = 0;
                            hideViews(iv_Renting);
                            hideViews(iv_Buying);
                            hideViews(iv_Utility);
                        }

                        LogUtils.e("GET NOTIFICATION", "SUCCESS" + logLine());
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(getActivity(), jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(getActivity(), getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(getActivity(), jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(getActivity(), getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(getActivity(), getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

}
