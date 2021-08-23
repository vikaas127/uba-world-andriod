package com.ubaworld.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.ubaworld.R;
import com.ubaworld.activity.askUba.AskUbaBuyingActivity;
import com.ubaworld.activity.askUba.AskUbaRentingActivity;
import com.ubaworld.activity.askUba.AskUbaUtilityActivity;
import com.ubaworld.activity.thread.ThreadActivity;
import com.ubaworld.activity.user.ProfileActivity;
import com.ubaworld.activity.user.SignInActivity;
import com.ubaworld.adapter.CommentAdapter;
import com.ubaworld.fragment.AskUbaFragment;
import com.ubaworld.fragment.BuyingGuideFragment;
import com.ubaworld.fragment.CompanyFragment;
import com.ubaworld.fragment.LegalFragment;
import com.ubaworld.fragment.ReminderFragment;
import com.ubaworld.fragment.RentingGuideFragment;
import com.ubaworld.fragment.SellingGuideFragment;
import com.ubaworld.fragment.UniversityFragment;
import com.ubaworld.fragment.UtilitiesFragment;
import com.ubaworld.model.CommentData;
import com.ubaworld.model.LoginData;
import com.ubaworld.model.NotificationData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
import static com.ubaworld.utils.Utils.getLoginUserData;
import static com.ubaworld.utils.Utils.hideProgressDialog;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.loadImage;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.saveBooleanToUserDefaults;
import static com.ubaworld.utils.Utils.saveToUserDefaults;
import static com.ubaworld.utils.Utils.showProgressDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog_withIntent;
import static com.ubaworld.utils.Utils.showValidationError;
import static com.ubaworld.utils.Utils.showViews;

public class Dashboard extends AppCompatActivity {

    @BindView(R.id.iv_Left)
    ImageView iv_Left;

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @BindView(R.id.iv_Header)
    ImageView iv_Header;

    @BindView(R.id.tv_Right)
    TextView tv_Right;

    @BindView(R.id.iv_Right)
    ImageView iv_Right;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.iv_profile_pic)
    CircleImageView ivProfilePic;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.iv_Verified)
    ImageView iv_Verified;

    @BindView(R.id.tv_UserName)
    TextView tv_UserName;

    @BindView(R.id.iv_AskUba)
    ImageView iv_AskUba;

    @BindView(R.id.iv_Insight)
    ImageView iv_Insight;

    @BindView(R.id.iv_Student)
    ImageView iv_Student;

    @BindView(R.id.llInsightView)
    LinearLayout llInsightView;

    private Boolean show = false;
    private Boolean addBill = false;
    private Boolean selectUniversity = false;

    private LoginData.Data userData;

    private ApiService apiService;
    private String authToken;
    private String user_Id;
    private int comment_type;
    private int is_read;
    boolean isFromMessage;
    int commentId;
    int commentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ButterKnife.bind(this);

        authToken = getFromUserDefaults(this, Constants.AUTH_TOKEN);
        user_Id = getFromUserDefaults(this, Constants.USER_ID);
        FirebaseApp.initializeApp(Dashboard.this);
        if (getIntent() != null && getIntent().hasExtra("isFromMessage")) {
            isFromMessage = getIntent().getExtras().getBoolean("isFromMessage");
        }
        if (getIntent() != null && getIntent().hasExtra("comment_id")) {
            commentId = getIntent().getExtras().getInt("comment_id");
        }
        if (getIntent() != null && getIntent().hasExtra("comment_type")) {
            commentType = getIntent().getExtras().getInt("comment_type");
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Dashboard.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                registerDeviceToken(newToken);
            }
        });

        initView();
        if (isFromMessage) {
            Log.e("foocomment",commentId+" "+commentType);
            getComment();
        }
    }


    private void getComment() {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call;
        call = apiService.list_Comment(commentType, "id", 1);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        CommentData commentData = new Gson().fromJson(response.body().string(), CommentData.class);
                        for (int i = 0; i < commentData.data.dataItems.size(); i++) {
                            if (commentData.data.dataItems.get(i).id == commentId) {
                                Intent intent = new Intent(Dashboard.this, ThreadActivity.class);
                                intent.putExtra("is_verify", commentData.data.dataItems.get(i).is_verify);
                                intent.putExtra("comment_id", commentData.data.dataItems.get(i).id);
                                intent.putExtra("user_id", commentData.data.dataItems.get(i).user_id);
                                intent.putExtra("profile_image", commentData.data.dataItems.get(i).profile_image);
                                intent.putExtra("comment", commentData.data.dataItems.get(i).comment);
                                intent.putExtra("like", commentData.data.dataItems.get(i).likes);
                                intent.putExtra("type", String.valueOf(commentData.data.dataItems.get(i).type));
                                intent.putExtra("is_read", String.valueOf(is_read));
                                startActivityForResult(intent, Constants.INTENT_COMMENT);
                            }
                        }

                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(Dashboard.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(Dashboard.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_COMMENT && resultCode == RESULT_OK) {
            String type = data.getStringExtra("type");
            String isRead = data.getStringExtra("is_read");
            if (type.equals("1")) {
                Intent intent2 = new Intent(this, AskUbaRentingActivity.class);
                intent2.putExtra("comment_type", Integer.parseInt(type));
                intent2.putExtra("is_read", Integer.parseInt(isRead));
                startActivity(intent2);
            } else if (type.equals("2")) {
                Intent intent2 = new Intent(this, AskUbaBuyingActivity.class);
                intent2.putExtra("comment_type", Integer.parseInt(type));
                intent2.putExtra("is_read", Integer.parseInt(isRead));
                startActivity(intent2);
            } else if (type.equals("3")) {
                Intent intent2 = new Intent(this, AskUbaUtilityActivity.class);
                intent2.putExtra("comment_type", Integer.parseInt(type));
                intent2.putExtra("is_read", Integer.parseInt(isRead));
                startActivity(intent2);
            } else if (type.equals("4")) {
                loadUniversityFragment();
            }
        }
    }


    private void initView() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close);
//        drawerLayout.setDrawerListener(toggle);
//        toggle.syncState();

        String fragment_type = getIntent().getStringExtra(Constants.TYPE);
        if (fragment_type != null && fragment_type.equalsIgnoreCase(Constants.CONFIG_REMINDER)) {
            intentReminder();
        } else {
            loadFragment(new AskUbaFragment());
            toolbar_title.setText(getResources().getString(R.string.str_ask_uba));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getLoginUserData(this) != null) {
            userData = getLoginUserData(this);
        }

        getNotification();

        if (userData.is_verify == 1)
            showViews(iv_Verified);
        else
            hideViews(iv_Verified);

        loadImage(this, userData.profile_image, ivProfilePic, R.mipmap.ic_user, progressBar);
//        if (userData.first_name != null && userData.last_name != null)
//            tv_UserName.setText(String.format("%s %s", userData.first_name, userData.last_name));
//        else if (userData.first_name != null && userData.last_name == null)
//            tv_UserName.setText(userData.first_name);
//        else if (userData.first_name == null && userData.last_name != null)
//            tv_UserName.setText(userData.first_name);
//        else
        tv_UserName.setText("");

    }

    @OnClick({R.id.iv_Left, R.id.iv_ProfileControl, R.id.llAskUba, R.id.llInsight, R.id.llRentingGuide, R.id.llBuyingGuide, R.id.llSellingGuide,
            R.id.llUtilities, R.id.llStudents, R.id.llReminder, R.id.llLegal, R.id.llCompany, R.id.llLogout, R.id.iv_Right,
            R.id.tv_Right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_Left:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;

            case R.id.iv_ProfileControl:
                drawerLayout.closeDrawers();
                startActivity(new Intent(this, ProfileActivity.class));
                break;

            case R.id.llAskUba:
                addBill = false;
                selectUniversity = false;
                drawerLayout.closeDrawers();
                loadFragment(new AskUbaFragment());
                toolbar_title.setText(getResources().getString(R.string.str_ask_uba));
                hideViews(iv_Header);
                hideViews(tv_Right);
                hideViews(iv_Right);
                break;

            case R.id.llInsight:
                if (!show) {
                    showViews(llInsightView);
                    show = true;
                } else {
                    hideViews(llInsightView);
                    show = false;
                }
                break;

            case R.id.llRentingGuide:
                addBill = false;
                selectUniversity = false;
                drawerLayout.closeDrawers();
                loadFragment(new RentingGuideFragment());
                toolbar_title.setText(getResources().getString(R.string.str_renting_guide));
                hideViews(iv_Header);
                hideViews(tv_Right);
                hideViews(iv_Right);
                break;

            case R.id.llBuyingGuide:
                addBill = false;
                selectUniversity = false;
                drawerLayout.closeDrawers();
                loadFragment(new BuyingGuideFragment());
                toolbar_title.setText(getResources().getString(R.string.str_buying_guide));
                hideViews(iv_Header);
                hideViews(tv_Right);
                hideViews(iv_Right);
                break;

            case R.id.llSellingGuide:
                addBill = false;
                selectUniversity = false;
                drawerLayout.closeDrawers();
                loadFragment(new SellingGuideFragment());
                toolbar_title.setText(getResources().getString(R.string.str_selling_guide));
                hideViews(iv_Header);
                hideViews(tv_Right);
                hideViews(iv_Right);
                break;

            case R.id.llUtilities:
                addBill = false;
                selectUniversity = false;
                drawerLayout.closeDrawers();
                loadFragment(new UtilitiesFragment());
                toolbar_title.setText(getResources().getString(R.string.str_utilities));
                hideViews(iv_Header);
                hideViews(tv_Right);
                hideViews(iv_Right);
                break;

            case R.id.llStudents:
                loadUniversityFragment();
                break;

            case R.id.llReminder:
                intentReminder();
                break;

            case R.id.llLegal:
                addBill = false;
                selectUniversity = false;
                drawerLayout.closeDrawers();
                loadFragment(new LegalFragment());
                toolbar_title.setText(getResources().getString(R.string.str_legal));
                hideViews(iv_Header);
                hideViews(tv_Right);
                hideViews(iv_Right);
                break;

            case R.id.llCompany:
                addBill = false;
                selectUniversity = false;
                drawerLayout.closeDrawers();
                loadFragment(new CompanyFragment());
                toolbar_title.setText(getResources().getString(R.string.str_company));
                hideViews(iv_Header);
                hideViews(tv_Right);
                hideViews(iv_Right);
                break;

            case R.id.llLogout:
                attempt_Logout();
                break;

            case R.id.iv_Right:
                if (addBill) {
                    Intent intent = new Intent(this, AddBillActivity.class);
                    intent.putExtra("type", "create");
                    startActivity(intent);
                }
                break;

            case R.id.tv_Right:
                if (selectUniversity)
                    startActivity(new Intent(this, SelectUniversityActivity.class));
                break;
        }
    }

    private void intentReminder() {
        addBill = true;
        selectUniversity = false;
        drawerLayout.closeDrawers();
        loadFragment(new ReminderFragment());
        toolbar_title.setText("");
        iv_Header.setImageResource(R.drawable.ic_time);
        iv_Right.setImageResource(R.drawable.ic_add);
        showViews(iv_Header);
        hideViews(tv_Right);
        showViews(iv_Right);
    }

    private void getNotification() {
        if (!isConnectingToInternet(this))
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
                                if (comment_type < 4 && is_read == 0) {
                                    showViews(iv_AskUba);
                                } else if (comment_type == 4 && is_read == 0) {
                                    showViews(iv_Insight);
                                    showViews(iv_Student);
                                }
                            }
                        } else {
                            hideViews(iv_AskUba);
                            hideViews(iv_Insight);
                            hideViews(iv_Student);
                        }

                        LogUtils.e("GET NOTIFICATION", "SUCCESS" + logLine());
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(Dashboard.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(Dashboard.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void attempt_Logout() {
        final Dialog alertDialogs = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        alertDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogs.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        alertDialogs.setContentView(R.layout.alert_dialog_logout);

        TextView tv_NotNow = alertDialogs.findViewById(R.id.tv_NotNow);
        TextView tv_Yes = alertDialogs.findViewById(R.id.tv_Yes);

        alertDialogs.setCancelable(false);
        alertDialogs.setCanceledOnTouchOutside(false);

        tv_NotNow.setOnClickListener(v -> alertDialogs.dismiss());
        tv_Yes.setOnClickListener(v -> {
            alertDialogs.dismiss();
            logout();
        });

        if (!isFinishing()) {
            alertDialogs.show();
        }
    }

    private void logout() {
        if (!isConnectingToInternet(this))
            return;

        showProgressDialog(this);
        apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.user_Logout();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    hideProgressDialog();
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "USER LOGOUT ---> " + new Gson().toJson(response.body()) + logLine());
                        saveBooleanToUserDefaults(Dashboard.this, Constants.IS_LOGIN, false);
                        saveToUserDefaults(Dashboard.this, Constants.AUTH_TOKEN, "");
                        Intent homeIntent = new Intent(Dashboard.this, SignInActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(Dashboard.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(Dashboard.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void registerDeviceToken(String token) {
        if (!isConnectingToInternet(this))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.registerDeviceToken(token);

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
                                showValidationAlertDialog_withIntent(Dashboard.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationError(Dashboard.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(Dashboard.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private void loadUniversityFragment() {
        addBill = false;
        selectUniversity = true;
        drawerLayout.closeDrawers();
//                loadFragment(new UniversityFragment());
        Bundle bundle = new Bundle();
        bundle.putInt("comment_type", comment_type);
        bundle.putInt("is_read", is_read);
        UniversityFragment fragment = new UniversityFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        fragment.setArguments(bundle);
        transaction.commit();
        toolbar_title.setText(getResources().getString(R.string.str_students));
        hideViews(iv_Header);
        showViews(tv_Right);
        hideViews(iv_Right);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void setActionBarTitle(String title) {
        toolbar_title.setText(title);
    }

}
