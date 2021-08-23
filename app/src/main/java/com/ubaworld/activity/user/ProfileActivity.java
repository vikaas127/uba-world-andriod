package com.ubaworld.activity.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubaworld.R;
import com.ubaworld.adapter.CountryAdapter;
import com.ubaworld.adapter.GenderAdapter;
import com.ubaworld.model.CountryData;
import com.ubaworld.model.GenderData;
import com.ubaworld.model.LoginData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ubaworld.utils.Constants.PERMISSION_REQUEST_CODE_IMAGE;
import static com.ubaworld.utils.Constants.SETTINGS_REQUEST_CODE_IMAGE;
import static com.ubaworld.utils.Constants.UNAUTHORIZED;
import static com.ubaworld.utils.Utils.getFromUserDefaults;
import static com.ubaworld.utils.Utils.getLoginUserData;
import static com.ubaworld.utils.Utils.hideProgressDialog;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.isValidEmail;
import static com.ubaworld.utils.Utils.isValidPassword;
import static com.ubaworld.utils.Utils.listOfGender;
import static com.ubaworld.utils.Utils.loadImage;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.profile_formatDate;
import static com.ubaworld.utils.Utils.saveToUserDefaults;
import static com.ubaworld.utils.Utils.setLoginUserData;
import static com.ubaworld.utils.Utils.showDatePickerDialog_BirthDate;
import static com.ubaworld.utils.Utils.showProgressDialog;
import static com.ubaworld.utils.Utils.showRequestAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog_withIntent;
import static com.ubaworld.utils.Utils.showViews;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.llView)
    LinearLayout llView;

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.iv_profile_pic)
    CircleImageView ivProfilePic;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.iv_Verified)
    ImageView iv_Verified;

    @BindView(R.id.et_FirstName)
    EditText et_FirstName;

    @BindView(R.id.et_LastName)
    EditText et_LastName;

    @BindView(R.id.et_BirthDate)
    EditText et_BirthDate;

    @BindView(R.id.et_Gender)
    EditText et_Gender;

    @BindView(R.id.et_Country)
    EditText et_Country;

    @BindView(R.id.et_Email)
    EditText et_Email;

    @BindView(R.id.check_Role1)
    CheckBox check_Role1;

    @BindView(R.id.check_Role2)
    CheckBox check_Role2;

    @BindView(R.id.check_Role3)
    CheckBox check_Role3;

    @BindView(R.id.check_Role4)
    CheckBox check_Role4;

    @BindView(R.id.check_Role5)
    CheckBox check_Role5;

    @BindView(R.id.llOther)
    LinearLayout llOther;

    @BindView(R.id.et_ChangePassword)
    EditText et_ChangePassword;

    @BindView(R.id.et_ConfirmPassword)
    EditText et_ConfirmPassword;

    @BindView(R.id.tv_CreateDate)
    TextView tv_CreateDate;

    private LoginData.Data userData;
    private String authToken;
    List<String> userType = new ArrayList<String>();

    private File path = null;
    private String genderId;
    private String email;
    private String changePassword;

    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private List<GenderData> genderList;
    private List<CountryData> countryList;

    private GenderAdapter adapter_Gender;
    private CountryAdapter adapter_Country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        authToken = getFromUserDefaults(this, Constants.AUTH_TOKEN);
        initView();
        setListener();
        addItemsFromJSON();

    }

    private void initView() {
        tv_HeaderTitle.setText(getResources().getString(R.string.str_title_edit_profile));

        if (getLoginUserData(this) != null) {
            userData = getLoginUserData(this);
        }

        genderList = listOfGender();
        setData(userData);
    }

    private void setData(LoginData.Data userData) {
        loadImage(this, userData.profile_image, ivProfilePic, R.mipmap.ic_user, progressBar);
        if (userData.is_verify == 1)
            showViews(iv_Verified);
        else
            hideViews(iv_Verified);

        et_FirstName.setText(userData.first_name);
        et_LastName.setText(userData.last_name);
        et_BirthDate.setText(userData.date_of_birth);
        genderId = userData.gender;
        tv_CreateDate.setText("Joined UbaWorld " + profile_formatDate(userData.created_at));
        for (int i = 0; i < genderList.size(); i++) {
            if (genderId.equals(genderList.get(i).getGenderId()))
                et_Gender.setText(genderList.get(i).getGender());
        }
        et_Country.setText(userData.country);
        et_Email.setText(userData.email);

        String s = userData.user_type;
        String[] type = s.split(", ");
        LogUtils.e("SELECTION", " ---> " + s + logLine());
        for (String value : type) {
            switch (value) {
                case "1":
                    check_Role1.setChecked(true);
                    userType.add("1");
                    break;
                case "2":
                    check_Role2.setChecked(true);
                    userType.add("2");
                    break;
                case "3":
                    check_Role3.setChecked(true);
                    userType.add("3");
                    break;
                case "4":
                    check_Role4.setChecked(true);
                    userType.add("4");
                    break;
                case "5":
                    check_Role5.setChecked(true);
//                    showViews(llOther);
                    userType.add("5");
                    break;

                default:
                    break;
            }
        }

    }

    private void setListener() {
        check_Role1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                userType.add("1");
            else {
                for (int i = 0; i < userType.size(); i++) {
                    if (userType.get(i).equals("1"))
                        userType.remove(i);
                }
            }
        });

        check_Role2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                userType.add("2");
            else {
                for (int i = 0; i < userType.size(); i++) {
                    if (userType.get(i).equals("2"))
                        userType.remove(i);
                }
            }
        });

        check_Role3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                userType.add("3");
            else {
                for (int i = 0; i < userType.size(); i++) {
                    if (userType.get(i).equals("3"))
                        userType.remove(i);
                }
            }
        });

        check_Role4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                userType.add("4");
            else {
                for (int i = 0; i < userType.size(); i++) {
                    if (userType.get(i).equals("4"))
                        userType.remove(i);
                }
            }
        });

        check_Role5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                userType.add("5");
                showViews(llOther);
            } else {
                for (int i = 0; i < userType.size(); i++) {
                    if (userType.get(i).equals("5")) {
                        userType.remove(i);
                        hideViews(llOther);
                    }
                }
            }
        });

    }

    private void addItemsFromJSON() {
        try {
            InputStream is = getAssets().open("country.json");

            Gson gson = new Gson();
            Reader reader = new InputStreamReader(is);
            Type listType = new TypeToken<List<CountryData>>() {
            }.getType();
            countryList = gson.fromJson(reader, listType);
            LogUtils.e("Size", "COUNTRY LIST :: " + countryList.size() + logLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.iv_Left, R.id.iv_profile_pic, R.id.et_BirthDate, R.id.et_Gender, R.id.et_Country, R.id.btn_Save})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.iv_Left:
                finish();
                break;

            case R.id.iv_profile_pic:
                checkAndRequestPermission();
                break;

            case R.id.et_BirthDate:
                showDatePickerDialog_BirthDate(this, et_BirthDate);
                break;

            case R.id.et_Gender:
                showGenderPopup();
                break;

            case R.id.et_Country:
                showCountryPopup();
                break;

            case R.id.btn_Save:
                if (isValid())
                    editProfile();
                break;

        }
    }

    private void editProfile() {
        if (!isConnectingToInternet(this))
            return;

        showProgressDialog(this);
        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);

        MultipartBody.Part body = null;
        if (path != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), path);
            body = MultipartBody.Part.createFormData("profile_image", path.getName(), requestFile);
        }

        RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), et_FirstName.getText().toString());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), et_LastName.getText().toString());
        RequestBody birthDate = RequestBody.create(MediaType.parse("text/plain"), et_BirthDate.getText().toString());
        RequestBody email_Address = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), genderId);
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), et_Country.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), changePassword);
        String s = userType.toString().replace("[", "");
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), s.replace("]", ""));

        Map<String, RequestBody> params = new HashMap<>();
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("date_of_birth", birthDate);
        params.put("email", email_Address);
        params.put("gender", gender);
        params.put("country", country);
        if (password != null)
            params.put("password", password);
        params.put("user_type", type);

        Call<LoginData> call;
        if (path != null)
            call = apiService.user_EditProfile(params, body);
        else
            call = apiService.user_EditProfile(params);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                try {
                    hideProgressDialog();
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "USER EDIT PROFILE ---> " + new Gson().toJson(response.body()) + logLine());
                        setLoginUserData(ProfileActivity.this, response.body().data);
                        saveToUserDefaults(ProfileActivity.this, Constants.USER_ID, String.valueOf(response.body().data.id));
                        finish();
                    } else if (response.raw().code() == UNAUTHORIZED) {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog_withIntent(ProfileActivity.this, jsonObject.get("message").toString(), SignInActivity.class);
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(ProfileActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    } else {
                        try {
                            String str = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.has("message")) {
                                showValidationAlertDialog(ProfileActivity.this, jsonObject.get("message").toString());
                            }
                        } catch (Exception e) {
                            showValidationAlertDialog(ProfileActivity.this, getString(R.string.server_error_something_went_wrong));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                t.printStackTrace();
                showValidationAlertDialog(ProfileActivity.this, getString(R.string.server_error_something_went_wrong));
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

    private boolean isValid() {
        email = et_Email.getText().toString();
        changePassword = et_ChangePassword.getText().toString();
        String confirmPassword = et_ConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        } else if (!isValidEmail(email)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_email));
            return false;
        }

        if (!TextUtils.isEmpty(changePassword)) {
//            if (!isValidPassword(changePassword) || changePassword.length() < 6) {
//                showValidationAlertDialog(this, getResources().getString(R.string.valid_password_length));
//                return false;
            if (changePassword.length() < 6) {
                showValidationAlertDialog(this, getResources().getString(R.string.valid_password_length));
                return false;
            } else if (!confirmPassword.equalsIgnoreCase(changePassword)) {
                showValidationAlertDialog(this, getResources().getString(R.string.valid_password_not_match));
                return false;
            }
        }
        return true;
    }

    private void showGenderPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_popup_dialog, null);

        TextView tvHeader = view.findViewById(R.id.tvHeader);
        SearchView searchView = view.findViewById(R.id.search_view);
        final RecyclerView rcvAlertPopup = view.findViewById(R.id.rcvAlertPopup);

        hideViews(searchView);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.CENTER, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        tvHeader.setText(getResources().getString(R.string.str_select_gender));

        adapter_Gender = new GenderAdapter(this, genderList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        rcvAlertPopup.setLayoutManager(layoutManager);
        rcvAlertPopup.setAdapter(adapter_Gender);

        adapter_Gender.setClickListener((itemView, position) -> {
            popupWindow.dismiss();
            et_Gender.setText(genderList.get(position).getGender());
            genderId = genderList.get(position).getGenderId();
            LogUtils.e("GENDER", "TITLE ---> " + et_Gender.getText().toString() + " || ID ---> " + genderId + logLine());
        });

    }

    private void showCountryPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_popup_dialog, null);

        TextView tvHeader = view.findViewById(R.id.tvHeader);
        SearchView searchView = view.findViewById(R.id.search_view);
        final RecyclerView rcvAlertPopup = view.findViewById(R.id.rcvAlertPopup);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.CENTER, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        tvHeader.setText(getResources().getString(R.string.str_select_country));

        adapter_Country = new CountryAdapter(this, countryList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        rcvAlertPopup.setLayoutManager(layoutManager);
        rcvAlertPopup.setAdapter(adapter_Country);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter_Country.getFilter().filter(newText);
                return false;
            }
        });

        adapter_Country.setClickListener((view1, name) -> {
            popupWindow.dismiss();
            et_Country.setText(name);
            LogUtils.e("COUNTRY", "TITLE ---> " + name + logLine());
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void checkAndRequestPermission() {
        String[] appPermission = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermission) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE_IMAGE);
            }
        } else {
            showPictureDialog();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_IMAGE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount == 0) {
                showPictureDialog();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    if (shouldShowRequestPermissionRationale(permName)) {
                        showRequestAlertDialog(this, "", getString(R.string.app_needs_permission),
                                getString(R.string.yes_grant_permission), (dialog, which) -> {
                                    dialog.dismiss();
                                    checkAndRequestPermission();
                                }, getString(R.string.no), (dialog, which) -> dialog.dismiss());
                    } else {
                        showRequestAlertDialog(this, "", getString(R.string.apply_permission_setting),
                                getString(R.string.goto_setting), (dialog, which) -> {
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, SETTINGS_REQUEST_CODE_IMAGE);

                                }, getString(R.string.no), (dialog, which) -> dialog.dismiss());
                    }
                    break;
                }
            }
        }
    }

    private void showPictureDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popup_profile_image, null);

        ImageView ivCow = view.findViewById(R.id.ivCow);
        ImageView ivDog = view.findViewById(R.id.ivDog);
        ImageView ivMonkey = view.findViewById(R.id.ivMonkey);
        ImageView ivPenguin = view.findViewById(R.id.ivPenguin);
        ImageView ivTiger = view.findViewById(R.id.ivTiger);
        TextView tvCamera = view.findViewById(R.id.tvCamera);
        TextView tvGallery = view.findViewById(R.id.tvGallery);
        TextView tvCancel = view.findViewById(R.id.tvCancel);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.BOTTOM, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        ivCow.setOnClickListener(v -> {
            popupWindow.dismiss();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_cow);
            ivProfilePic.setImageBitmap(bitmap);
            Uri uri = getImageUri_Bitmap(bitmap);
            String imagePath = getPath_Uri(uri);
            this.path = new File(imagePath);
        });

        ivDog.setOnClickListener(v -> {
            popupWindow.dismiss();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_dog);
            ivProfilePic.setImageBitmap(bitmap);
            Uri uri = getImageUri_Bitmap(bitmap);
            String imagePath = getPath_Uri(uri);
            this.path = new File(imagePath);
        });

        ivMonkey.setOnClickListener(v -> {
            popupWindow.dismiss();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_monkey);
            ivProfilePic.setImageBitmap(bitmap);
            Uri uri = getImageUri_Bitmap(bitmap);
            String imagePath = getPath_Uri(uri);
            this.path = new File(imagePath);
        });

        ivPenguin.setOnClickListener(v -> {
            popupWindow.dismiss();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_penguin);
            ivProfilePic.setImageBitmap(bitmap);
            Uri uri = getImageUri_Bitmap(bitmap);
            String imagePath = getPath_Uri(uri);
            this.path = new File(imagePath);
        });

        ivTiger.setOnClickListener(v -> {
            popupWindow.dismiss();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_tiger);
            ivProfilePic.setImageBitmap(bitmap);
            Uri uri = getImageUri_Bitmap(bitmap);
            String imagePath = getPath_Uri(uri);
            this.path = new File(imagePath);
        });

        tvCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
            popupWindow.dismiss();
        });

        tvGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_FILE);
            popupWindow.dismiss();
        });

        tvCancel.setOnClickListener(v -> popupWindow.dismiss());

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SETTINGS_REQUEST_CODE_IMAGE) {
//            checkAndRequestPermission();
//            return;
//        }
//
//        if (resultCode == RESULT_OK && requestCode == Picture.REQUEST_CODE_CHOOSE) {
//            final List<Uri> resultUri = Matisse.obtainResult(data);
//            if (resultUri == null)
//                return;
//
//            picture.result(requestCode, resultCode, data, uri -> {
//                Glide.with(ProfileActivity.this).load(uri).into(ivProfilePic);
//                path = new File(getPath(uri));
//            });
//        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                ivProfilePic.setImageBitmap(bmp);
                Uri uri = getImageUri_Bitmap(bmp);
                String imagePath = getPath_Uri(uri);
                this.path = new File(imagePath);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                ivProfilePic.setImageURI(selectedImageUri);
                String imagePath = getPath_Uri(selectedImageUri);
                this.path = new File(imagePath);
            }
        }

    }

    private String getPath_Uri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private Uri getImageUri_Bitmap(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
