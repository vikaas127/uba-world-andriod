package com.ubaworld.activity.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ubaworld.R;
import com.ubaworld.model.LoginData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.LogUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ubaworld.utils.Utils.hideProgressDialog;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.isValidEmail;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showProgressDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog_withIntent;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.et_Email)
    EditText et_Email;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tv_HeaderTitle.setText(getResources().getString(R.string.str_title_forget_password));
    }

    @OnClick({R.id.iv_Left, R.id.btn_Submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.iv_Left:
                finish();
                break;

            case R.id.btn_Submit:
                if (isValid())
                    forgotPassword();
                break;

        }
    }

    private void forgotPassword() {
        if (!isConnectingToInternet(this))
            return;

        showProgressDialog(this);
        ApiService apiService = RetroFitWebService.generateService(ApiService.class);
        Call<LoginData> call = apiService.user_ForgotPassword(email);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                try {
                    hideProgressDialog();
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "USER FORGOT PASSWORD ---> " + new Gson().toJson(response.body()) + logLine());
                        showValidationAlertDialog_withIntent(ForgotPasswordActivity.this, response.body().message, SignInActivity.class);
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog(ForgotPasswordActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(ForgotPasswordActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(ForgotPasswordActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private boolean isValid() {
        email = et_Email.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        } else if (!isValidEmail(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        }
        return true;
    }

}
