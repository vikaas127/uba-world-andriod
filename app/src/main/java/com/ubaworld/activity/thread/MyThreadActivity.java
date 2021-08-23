package com.ubaworld.activity.thread;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ubaworld.R;
import com.ubaworld.activity.user.SignInActivity;
import com.ubaworld.adapter.ReplyAdapter;
import com.ubaworld.interfaces.AdapterCallback;
import com.ubaworld.model.ReplyData;
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
import static com.ubaworld.utils.Utils.convertDate_Time_InMilliseconds;
import static com.ubaworld.utils.Utils.getCurrentDate_Thread;
import static com.ubaworld.utils.Utils.getFromUserDefaults;
import static com.ubaworld.utils.Utils.hideProgressDialog;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.loadImage;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showProgressDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog_withIntent;
import static com.ubaworld.utils.Utils.showValidationError;
import static com.ubaworld.utils.Utils.showViews;

public class MyThreadActivity extends AppCompatActivity implements AdapterCallback {

    @BindView(R.id.llView)
    LinearLayout llView;

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.iv_Delete)
    ImageView iv_Delete;

    @BindView(R.id.iv_Verified)
    ImageView iv_Verified;

    @BindView(R.id.iv_profile_pic)
    CircleImageView iv_profile_pic;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.tv_Comment)
    TextView tv_Comment;

    @BindView(R.id.tv_Like)
    TextView tv_Like;

    @BindView(R.id.rcv_Reply)
    RecyclerView rcv_Reply;

    @BindView(R.id.et_Reply)
    EditText et_Reply;

    private String authToken;

    private int is_verify;
    private String profile_image;
    private String comment;
    private int comment_id;
    private int user_id;
    private int like;
    private long createDate;
    private long currentDate;
    private long diff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_thread);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            comment_id = bundle.getInt("comment_id");
            user_id = bundle.getInt("user_id");
            is_verify = bundle.getInt("is_verify");
            profile_image = bundle.getString("profile_image");
            comment = bundle.getString("comment");
            like = bundle.getInt("like");
            createDate = bundle.getLong("create_date");
        }

        authToken = getFromUserDefaults(this, Constants.AUTH_TOKEN);
        initView();
        Log.e("token", authToken);

        getReplies(true);
    }

    private void initView() {
        tv_HeaderTitle.setText(getResources().getString(R.string.str_title_thread));
        currentDate = convertDate_Time_InMilliseconds(getCurrentDate_Thread());

        if (is_verify == 1)
            showViews(iv_Verified);
        else
            hideViews(iv_Verified);

        loadImage(this, profile_image, iv_profile_pic, R.drawable.ic_profile_penguin, progressBar);
        tv_Comment.setText(comment);
        tv_Like.setText(String.valueOf(like));

        diff = (currentDate - createDate) / 1000;
        LogUtils.e("TIME", " :: " + diff + " sec" + logLine());
        if (diff > 60)
            if (is_verify == 1)
                showViews(iv_Delete);
            else
                hideViews(iv_Delete);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(comment_id);
    }

    @OnClick({R.id.ll_Left, R.id.iv_Edit, R.id.iv_Delete, R.id.iv_Send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_Left:
                finish();
                break;

            case R.id.iv_Edit:
                Intent intent = new Intent(this, UpdateCommentReplyActivity.class);
                intent.putExtra("type", "comment");
                intent.putExtra("is_verify", is_verify);
                intent.putExtra("id", comment_id);
                intent.putExtra("profile_image", profile_image);
                intent.putExtra("text", comment);
                startActivityForResult(intent, Constants.INTENT_THREAD_COMMENT);
                break;

            case R.id.iv_Delete:
                currentDate = convertDate_Time_InMilliseconds(getCurrentDate_Thread());
                diff = (currentDate - createDate) / 1000;
                if (iv_Verified.isShown()) {
                    deleteComment();
                    showViews(iv_Delete);
                } else {
                    if (diff < 61)
                        deleteComment();
                    else
                        showValidationAlertDialog(this, "You don't have permission to delete this question.");
                }
                break;

            case R.id.iv_Send:
                addReply();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_THREAD_COMMENT && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            tv_Comment.setText(bundle.getString("comment"));
        } else if (requestCode == Constants.INTENT_THREAD_REPLY && resultCode == RESULT_OK) {
            getReplies(true);
        }
    }

    private void getReplies(boolean isShow) {
        if (!isConnectingToInternet(this))
            return;

        if (isShow)
            showProgressDialog(this);
        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.list_Reply(comment_id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgressDialog();
                try {
                    if (response.isSuccessful()) {
                        ReplyData replyData = new Gson().fromJson(response.body().string(), ReplyData.class);

                        LogUtils.e("SIZE", "REPLY LIST ---> " + replyData.data.size() + logLine());

                        if (replyData != null) {
                            showViews(rcv_Reply);
                            rcv_Reply.setLayoutManager(new GridLayoutManager(MyThreadActivity.this, 1));
                            ReplyAdapter adapter = new ReplyAdapter(MyThreadActivity.this, replyData, llView, 1);
                            rcv_Reply.setAdapter(adapter);
                        } else {
                            hideViews(rcv_Reply);
                        }
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(MyThreadActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(MyThreadActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void deleteComment() {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.delete_Comment(comment_id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "COMMENT DELETE" + logLine());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(MyThreadActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(MyThreadActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void showThreadPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popup_thread_report, null);

        TextView tv_Report = view.findViewById(R.id.tv_Report);
        TextView tv_Cancel = view.findViewById(R.id.tv_Cancel);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.BOTTOM, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        tv_Cancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void addReply() {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.add_Reply(comment_id, et_Reply.getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        et_Reply.setText("");
                        getReplies(false);
                        LogUtils.e("REPLY", "MY THREAD SUCCESS" + logLine());
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(MyThreadActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(MyThreadActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(MyThreadActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    @Override
    public void onMethodCallback() {
        getReplies(false);
    }
}