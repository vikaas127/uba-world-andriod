package com.ubaworld.activity.thread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ubaworld.R;
import com.ubaworld.activity.user.SignInActivity;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ubaworld.utils.Constants.UNAUTHORIZED;
import static com.ubaworld.utils.Utils.getFromUserDefaults;
import static com.ubaworld.utils.Utils.hideKeyboard;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.loadImage;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog_withIntent;
import static com.ubaworld.utils.Utils.showValidationError;
import static com.ubaworld.utils.Utils.showViews;

public class UpdateCommentReplyActivity extends AppCompatActivity {

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.iv_Verified)
    ImageView iv_Verified;

    @BindView(R.id.iv_profile_pic)
    CircleImageView iv_profile_pic;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.et_Reply)
    EditText et_Reply;

    @BindView(R.id.btn_Cancel)
    Button btn_Cancel;

    @BindView(R.id.btn_Update)
    Button btn_Update;

    private String authToken;

    private String type;
    private int id;
    private int is_verify;
    private String profile_image;
    private String reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_comment_reply);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            id = bundle.getInt("id");
            is_verify = bundle.getInt("is_verify");
            profile_image = bundle.getString("profile_image");
            reply = bundle.getString("text");
        }

        authToken = getFromUserDefaults(this, Constants.AUTH_TOKEN);
        initView();
    }

    private void initView() {
        tv_HeaderTitle.setText(getResources().getString(R.string.str_title_edit));

        if (is_verify == 1)
            showViews(iv_Verified);
        else
            hideViews(iv_Verified);

        loadImage(this, profile_image, iv_profile_pic, R.mipmap.ic_user, progressBar);
        et_Reply.setText(reply);
        et_Reply.setSelection(reply.length());

    }

    @OnClick({R.id.ll_Left, R.id.btn_Cancel, R.id.btn_Update})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.ll_Left:

            case R.id.btn_Cancel:
                finish();
                break;

            case R.id.btn_Update:
                hideKeyboard(this);
                if (type.equalsIgnoreCase("comment"))
                    updateComment();
                else
                    updateReply();
                break;

        }
    }

    private void updateComment() {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.update_Comment(id, et_Reply.getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent();
                        intent.putExtra("comment", et_Reply.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                        LogUtils.e("UPDATE COMMENT", "SUCCESS" + logLine());
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(UpdateCommentReplyActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(UpdateCommentReplyActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(UpdateCommentReplyActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(UpdateCommentReplyActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(UpdateCommentReplyActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void updateReply() {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.update_Reply(id, et_Reply.getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        LogUtils.e("UPDATE REPLY", "SUCCESS" + logLine());
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(UpdateCommentReplyActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(UpdateCommentReplyActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(UpdateCommentReplyActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(UpdateCommentReplyActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(UpdateCommentReplyActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

}