package com.ubaworld.activity.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.ubaworld.R;
import com.ubaworld.activity.Dashboard;
import com.ubaworld.model.LoginData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ubaworld.utils.Utils.actionDoneListenerToEditText;
import static com.ubaworld.utils.Utils.hideProgressDialog;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.isValidEmail;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.saveBooleanToUserDefaults;
import static com.ubaworld.utils.Utils.saveToUserDefaults;
import static com.ubaworld.utils.Utils.setLoginUserData;
import static com.ubaworld.utils.Utils.setWindowFlag;
import static com.ubaworld.utils.Utils.showProgressDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.et_Email)
    EditText et_Email;

    @BindView(R.id.et_Password)
    EditText et_Password;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
    }

    private void initView() {
        actionDoneListenerToEditText(this, et_Password);
    }

    @OnClick({R.id.tv_ForgotPassword, R.id.btn_Login, R.id.tv_CreateAccount})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.tv_ForgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;

            case R.id.btn_Login:
                if (isValid())
                login();
                break;

            case R.id.tv_CreateAccount:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

    private void login() {
        if (!isConnectingToInternet(this))
            return;

        showProgressDialog(this);
        ApiService apiService = RetroFitWebService.generateService(ApiService.class);
        Call<LoginData> call = apiService.user_Login(email, password);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                try {
                    hideProgressDialog();
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "USER LOGIN ---> " + new Gson().toJson(response.body()) + logLine());
                        saveBooleanToUserDefaults(SignInActivity.this, Constants.IS_LOGIN, true);
                        setLoginUserData(SignInActivity.this, response.body().data);
                        saveToUserDefaults(SignInActivity.this, Constants.AUTH_TOKEN, response.body().data.token);
                        saveToUserDefaults(SignInActivity.this, Constants.USER_ID, String.valueOf(response.body().data.id));
                        Intent homeIntent = new Intent(SignInActivity.this, Dashboard.class);
                        homeIntent.putExtra("local_notification", false);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog(SignInActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(SignInActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(SignInActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private boolean isValid() {
        email = et_Email.getText().toString();
        password = et_Password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        } else if (!isValidEmail(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_password));
            return false;
        }
        return true;
    }


}
