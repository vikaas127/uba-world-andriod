package com.ubaworld.activity.askUba;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.ubaworld.R;
import com.ubaworld.activity.user.SignInActivity;
import com.ubaworld.adapter.CommentAdapter;
import com.ubaworld.interfaces.AdapterCallback;
import com.ubaworld.model.CommentData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

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
import static com.ubaworld.utils.Utils.hideKeyboard;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog_withIntent;
import static com.ubaworld.utils.Utils.showValidationError;
import static com.ubaworld.utils.Utils.showViews;

public class AskUbaUtilityActivity extends AppCompatActivity implements AdapterCallback {

    @BindView(R.id.tv_Top)
    TextView tv_Top;

    @BindView(R.id.tv_Newest)
    TextView tv_Newest;

    @BindView(R.id.tv_My)
    TextView tv_My;

    @BindView(R.id.iv_My)
    ImageView iv_My;

    @BindView(R.id.frame_Shimmer)
    ShimmerFrameLayout frame_Shimmer;

    @BindView(R.id.rcv_Comment)
    RecyclerView rcv_Comment;

    @BindView(R.id.et_Comment)
    EditText et_Comment;

    private String authToken;
    private int user_id;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_uba_utility);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int comment_type = bundle.getInt("comment_type");
            int is_read = bundle.getInt("is_read");
            if (comment_type == 3 && is_read == 0)
                showViews(iv_My);
        }

        authToken = getFromUserDefaults(this, Constants.AUTH_TOKEN);
        user_id = Integer.parseInt(getFromUserDefaults(this, Constants.USER_ID));
        getComment(1);
    }

    private void getComment(int i) {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        type = i;
        Call<ResponseBody> call;
        if (i == 1)
            call = apiService.list_Comment(3, "id", 1);
        else if (i == 2)
            call = apiService.list_Comment(3, "likes", 1);
        else
            call = apiService.list_CommentMy(3, user_id, 1);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        CommentData data = new Gson().fromJson(response.body().string(), CommentData.class);

                        LogUtils.e("SIZE", "UTILITY COMMENT LIST ---> " + data.data.dataItems.size() + logLine());

                        frame_Shimmer.stopShimmer();
                        hideViews(frame_Shimmer);

                        if (data != null) {
                            showViews(rcv_Comment);
                            rcv_Comment.setLayoutManager(new GridLayoutManager(AskUbaUtilityActivity.this, 1));
                            CommentAdapter adapter = new CommentAdapter(AskUbaUtilityActivity.this, data, 3);
                            rcv_Comment.setAdapter(adapter);
                        } else {
                            hideViews(rcv_Comment);
                        }
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(AskUbaUtilityActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(AskUbaUtilityActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    @OnClick({R.id.ll_Left, R.id.tv_Top, R.id.tv_Newest, R.id.tv_My, R.id.iv_Send})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.ll_Left:
                finish();
                break;

            case R.id.tv_Top:
                updateTab(tv_Top);
                hideViews(rcv_Comment);
                showViews(frame_Shimmer);
                frame_Shimmer.startShimmer();
                getComment(2);
                break;

            case R.id.tv_Newest:
                updateTab(tv_Newest);
                hideViews(rcv_Comment);
                showViews(frame_Shimmer);
                frame_Shimmer.startShimmer();
                getComment(1);
                break;

            case R.id.tv_My:
                updateTab(tv_My);
                hideViews(iv_My);
                hideViews(rcv_Comment);
                showViews(frame_Shimmer);
                frame_Shimmer.startShimmer();
                readNotification();
                getComment(3);
                break;

            case R.id.iv_Send:
                hideKeyboard(this);
                addComment();
                break;
        }
    }

    private void readNotification() {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.read_Notification(user_id, 3);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {

                        LogUtils.e("READ NOTIFICATION", "SUCCESS" + logLine());
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(AskUbaUtilityActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(AskUbaUtilityActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_COMMENT && resultCode == RESULT_OK) {
            getComment(type);
        }
    }

    private void addComment() {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.add_Comment(et_Comment.getText().toString(), 3);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        et_Comment.setText("");
                        getComment(type);
                        LogUtils.e("COMMENT", "SUCCESS" + logLine());
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(AskUbaUtilityActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(AskUbaUtilityActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(AskUbaUtilityActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void updateTab(TextView tv) {
        tv_Top.setBackgroundResource(R.drawable.bg_tab_bar_gray_corner);
        tv_Top.setTextColor(getResources().getColor(R.color.black));

        tv_Newest.setBackgroundResource(R.drawable.bg_tab_bar_gray_corner);
        tv_Newest.setTextColor(getResources().getColor(R.color.black));

        tv_My.setBackgroundResource(R.drawable.bg_tab_bar_gray_corner);
        tv_My.setTextColor(getResources().getColor(R.color.black));

        tv.setBackgroundResource(R.drawable.bg_tab_bar_orange_corner);
        tv.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onMethodCallback() {
        getComment(type);
    }
}
