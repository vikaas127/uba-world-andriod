package com.ubaworld.activity.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import static com.ubaworld.utils.Utils.hideKeyboard;
import static com.ubaworld.utils.Utils.hideProgressDialog;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.isValidEmail;
import static com.ubaworld.utils.Utils.isValidPassword;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.saveBooleanToUserDefaults;
import static com.ubaworld.utils.Utils.saveToUserDefaults;
import static com.ubaworld.utils.Utils.setLoginUserData;
import static com.ubaworld.utils.Utils.showDatePickerDialog_BirthDate;
import static com.ubaworld.utils.Utils.showProgressDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.et_FirstName)
    EditText et_FirstName;

    @BindView(R.id.et_LastName)
    EditText et_LastName;

    @BindView(R.id.et_BirthDate)
    TextView et_BirthDate;

    @BindView(R.id.et_Email)
    EditText et_Email;

    @BindView(R.id.et_Password)
    EditText et_Password;

    @BindView(R.id.et_ConfirmPassword)
    EditText et_ConfirmPassword;

    @BindView(R.id.tv_Spannable)
    TextView tv_Spannable;

    private String firstName;
    private String lastName;
    private String birthDate;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        initView();
        setSpannable();
    }

    private void initView() {
        tv_HeaderTitle.setText(getResources().getString(R.string.str_title_sign_up));

        actionDoneListenerToEditText(this, et_ConfirmPassword);
    }

    private void setSpannable() {
        String text = tv_Spannable.getText().toString();
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/terms-conditions/379e63ab95c4dac913d1d20f65f6b278"));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/privacy-policy/16a73a6d594e4adb6da967d13b5a5f15"));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan1, 50, 70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 74, 88, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_Spannable.setText(ss);
        tv_Spannable.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick({R.id.iv_Left, R.id.et_BirthDate, R.id.btn_SignUp})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.iv_Left:
                finish();
                break;

            case R.id.et_BirthDate:
                clearFocus();
                hideKeyboard(this);
                showDatePickerDialog_BirthDate(this, et_BirthDate);
                break;

            case R.id.btn_SignUp:
                if (isValid())
                    signUp();
                break;

        }
    }

    private void clearFocus() {
        et_FirstName.clearFocus();
        et_LastName.clearFocus();
        et_Email.clearFocus();
        et_Password.clearFocus();
        et_ConfirmPassword.clearFocus();
    }

    private void signUp() {
        if (!isConnectingToInternet(this))
            return;

        showProgressDialog(this);
        ApiService apiService = RetroFitWebService.generateService(ApiService.class);
        Call<LoginData> call = apiService.user_Register(firstName, lastName, birthDate, email, password);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                try {
                    hideProgressDialog();
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "USER REGISTER ---> " + new Gson().toJson(response.body()) + logLine());
                        saveBooleanToUserDefaults(SignUpActivity.this, Constants.IS_LOGIN, true);
                        setLoginUserData(SignUpActivity.this, response.body().data);
                        saveToUserDefaults(SignUpActivity.this, Constants.AUTH_TOKEN, response.body().data.token);
                        saveToUserDefaults(SignUpActivity.this, Constants.USER_ID, String.valueOf(response.body().data.id));
                        Intent homeIntent = new Intent(SignUpActivity.this, Dashboard.class);
                        homeIntent.putExtra("local_notification", false);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog(SignUpActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(SignUpActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(SignUpActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private boolean isValid() {
        firstName = et_FirstName.getText().toString();
        lastName = et_LastName.getText().toString();
        birthDate = et_BirthDate.getText().toString();
        email = et_Email.getText().toString();
        password = et_Password.getText().toString();
        String confirmPassword = et_ConfirmPassword.getText().toString();

//        if (TextUtils.isEmpty(firstName)) {
//            showValidationAlertDialog(this, getResources().getString(R.string.valid_first_name));
//            return false;
//        } else if (TextUtils.isEmpty(lastName)) {
//            showValidationAlertDialog(this, getResources().getString(R.string.valid_last_name));
//            return false;
//        } else if (TextUtils.isEmpty(birthDate)) {
//            showValidationAlertDialog(this, getResources().getString(R.string.valid_select_date));
//            return false;
//        } else
        if (TextUtils.isEmpty(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        } else if (!isValidEmail(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_password));
            return false;
//        } else if (!isValidPassword(password) || password.length() < 6) {
//            showValidationAlertDialog(this, getResources().getString(R.string.valid_password_length));
//            return false;
        } else if (password.length() < 6) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_password_length));
            return false;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_confirm_password));
            return false;
        } else if (confirmPassword.length() < 6) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_password_length));
            return false;
        } else if (!confirmPassword.equalsIgnoreCase(password)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_password_not_match));
            return false;
        }
        return true;
    }

}
