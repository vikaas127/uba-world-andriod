package com.ubaworld.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubaworld.R;
import com.ubaworld.activity.SplashActivity;
import com.ubaworld.model.AlarmRepeatTypeData;
import com.ubaworld.model.BillTypeData;
import com.ubaworld.model.GenderData;
import com.ubaworld.model.LoginData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Dialog mProgressDialog;
    private static final String TAG = Utils.class.getName();

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void showSuccessMessage(final Context context, final String message) {
        try {
            Activity activity = null;
            if (context instanceof Activity) {
                activity = (Activity) context;
                hideKeyboard(activity);
            }

            if (activity != null && !activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        FancyToast.makeText(context, message, 2000, FancyToast.SUCCESS, false).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showValidationError(final Context context, final String message) {
        try {
            Activity activity = null;
            if (context instanceof Activity) {
                activity = (Activity) context;
                hideKeyboard(activity);
            }

            if (activity != null && !activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        FancyToast.makeText(context, message, 2000, FancyToast.ERROR, false).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void failureHandle(Context context) {
        hideProgressDialog();
        showValidationError(context, context.getResources().getString(R.string.server_error_something_went_wrong));
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showViews(View... view) {
        for (View v : view) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(View... view) {
        for (View v : view) {
            v.setVisibility(View.GONE);
        }
    }

    public static void inVisibleViews(View... view) {
        for (View v : view) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    public static void actionDoneListenerToEditText(Context context, EditText editText) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard((Activity) context);
                editText.clearFocus();
                return true;
            }
            return false;
        });
    }

    public static boolean isConnectingToInternet(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Network[] networks = connectivityManager.getAllNetworks();
                    NetworkInfo networkInfo;
                    for (Network mNetwork : networks) {
                        networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                            return true;
                        }
                    }
                } else {
                    if (connectivityManager != null) {
                        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                        if (info != null) {
                            for (NetworkInfo anInfo : info) {
                                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            showValidationAlertDialog(context, context.getResources().getString(R.string.internet_connection_error));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void loadImage(Context context, String imageURL, ImageView imageView, int placeholder, ProgressBar progressBar) {
        if (context == null)
            return;
        if (imageView != null) {
            showViews(progressBar);
            Glide.with(context)
                    .load(imageURL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            hideViews(progressBar);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            hideViews(progressBar);
                            return false;
                        }
                    })
                    .placeholder(placeholder)
                    .into(imageView);
        }
    }

    public static void setLoginUserData(Context context, LoginData.Data user) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(Constants.LOGIN_USER, json);
        editor.commit();
    }

    public static LoginData.Data getLoginUserData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString(Constants.LOGIN_USER, null);
        Gson gson = new Gson();

        LoginData.Data loginUserModel = gson.fromJson(json, LoginData.Data.class);
        return loginUserModel;
    }

    public static void saveToUserDefaults(Context context, String key, String value) {
        if (context == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getFromUserDefaults(Context context, String key) {
        if (context == null) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void saveBooleanToUserDefaults(Context context, String key, boolean value) {
        if (context == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getBooleanFromUserDefaults(Context context, String key) {
        if (context == null) {
            return false;
        }
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void showProgressDialog(Context context) {
        hideProgressDialog();
        mProgressDialog = new Dialog(context);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setContentView(R.layout.layout_progress_dialog);

        ProgressWheel progressWheel = mProgressDialog.findViewById(R.id.progress_wheel);
        progressWheel.spin();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            progressWheel.setBarColor(context.getColor(R.color.orange));
        }

        if (!((Activity) context).isFinishing()) {
            mProgressDialog.show();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String logLine() {
        final StackTraceElement stackTrace = new Exception().getStackTrace()[1];

        String fileName = stackTrace.getFileName();
        if (fileName == null)
            fileName = "";

        final String info = "(" + fileName + ":" + stackTrace.getLineNumber() + ")";
        return (" :: " + info + " ");
    }

    public static void showDatePickerDialog_BirthDate(Context context, TextView textView) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) ->
                textView.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.set(mYear - 17, 11, 31);
        datePickerDialog.getDatePicker().setMaxDate(calendarMax.getTimeInMillis());
        datePickerDialog.show();

    }

    public static void showDatePickerDialog(Context context, TextView textView) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) ->
            textView.setText(formatDate(year, monthOfYear, dayOfMonth)), mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public static void showMinDatePickerDialog(Context context, TextView textView) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) ->
                textView.setText(formatDate(year, monthOfYear, dayOfMonth)), mYear, mMonth, mDay);

        c.set(mYear, mMonth, mDay + 1);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();

    }

    public static String formatDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        return sdf.format(date);
    }

    public static String simple_formatDate(String formatDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(formatDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String profile_formatDate(String formatDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy");

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(formatDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static android.app.AlertDialog showRequestAlertDialog(Context context, String title, String message, String positiveLabel,
                                                                 DialogInterface.OnClickListener positiveOnClick, String negativeLabel,
                                                                 DialogInterface.OnClickListener negativeOnClick) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        LogUtils.e(TAG, "CURRENT DATE ---> " + df.format(c.getTime()) + logLine());
        return df.format(c.getTime());
    }

    public static String getCurrentDate_Thread() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LogUtils.e(TAG, "CURRENT DATE THREAD---> " + df.format(c.getTime()) + logLine());
        return df.format(c.getTime());
    }

    public static long convertDate_Time_InMilliseconds(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = sdf.parse(dateString);
            calendar.setTime(date);
            LogUtils.e("DATE_TIME InMilliseconds", "--> " + calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    public static String convertDate(long dateInMilliseconds) {
        return DateFormat.format("dd/MM/yyyy hh:mm:ss", (dateInMilliseconds)).toString();
    }

    public static String convertTime(long dateInMilliseconds) {
        return DateFormat.format("hh:mm:ss", (dateInMilliseconds)).toString();
    }

    public static void showValidationAlertDialog(Context context, String msg) {
        hideProgressDialog();
        final Dialog alertDialogs = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        alertDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogs.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        alertDialogs.setContentView(R.layout.alert_dialog_with_single_btn);

        TextView tvMsg = alertDialogs.findViewById(R.id.tv_Message);
        TextView tvOk = alertDialogs.findViewById(R.id.tv_Ok);

        tvMsg.setText(msg);
        alertDialogs.setCancelable(false);
        alertDialogs.setCanceledOnTouchOutside(false);

        tvOk.setOnClickListener(v -> alertDialogs.dismiss());

        if (!((Activity) context).isFinishing()) {
            alertDialogs.show();
        }
    }

    public static void showValidationAlertDialog_withIntent(Context context, String msg, Class<?> activity) {
        final Dialog alertDialogs = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        alertDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogs.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        alertDialogs.setContentView(R.layout.alert_dialog_with_single_btn);

        TextView tvMsg = alertDialogs.findViewById(R.id.tv_Message);
        TextView tvOk = alertDialogs.findViewById(R.id.tv_Ok);

        tvMsg.setText(msg);
        alertDialogs.setCancelable(false);
        alertDialogs.setCanceledOnTouchOutside(false);

        tvOk.setOnClickListener(v -> {
            alertDialogs.dismiss();
            saveBooleanToUserDefaults(context, Constants.IS_LOGIN, false);
            saveToUserDefaults(context, Constants.AUTH_TOKEN, "");
            Intent homeIntent = new Intent(context, activity);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(homeIntent);
        });

        if (!((Activity) context).isFinishing()) {
            alertDialogs.show();
        }
    }

    public static List<GenderData> listOfGender() {
        JSONArray array = new JSONArray();
        String[] id = {"1", "2", "3"};
        String[] name = {"Male", "Female", "Prefer not to say"};
        for (int i = 0; i < id.length; i++) {
            JSONObject internalObject = new JSONObject();
            try {
                internalObject.put("gender", name[i]);
                internalObject.put("genderId", id[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(internalObject);
        }

        Type listType = new TypeToken<List<GenderData>>() {
        }.getType();
        return new Gson().fromJson(array.toString(), listType);
    }

    public static List<BillTypeData> listOfBillType() {
        JSONArray array = new JSONArray();
        String[] id = {"1", "2", "3", "4", "5", "6", "7"};
        String[] name = {"Electricity", "Water", "Gas", "Council Tax", "Broadband Subscription", "TV License", "Other"};
        for (int i = 0; i < id.length; i++) {
            JSONObject internalObject = new JSONObject();
            try {
                internalObject.put("billType", name[i]);
                internalObject.put("billId", id[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(internalObject);
        }

        Type listType = new TypeToken<List<BillTypeData>>() {
        }.getType();
        return new Gson().fromJson(array.toString(), listType);
    }

    public static List<AlarmRepeatTypeData> listOfRepeatType() {
        JSONArray array = new JSONArray();
        String[] id = {"1", "2", "3", "4", "5", "6"};
        String[] name = {"Every week", "Every 2 week", "Every Month", "Every 3 Month", "Every 6 Month", "Every Year"};
        for (int i = 0; i < id.length; i++) {
            JSONObject internalObject = new JSONObject();
            try {
                internalObject.put("repeatType", name[i]);
                internalObject.put("repeatId", id[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(internalObject);
        }

        Type listType = new TypeToken<List<AlarmRepeatTypeData>>() {
        }.getType();
        return new Gson().fromJson(array.toString(), listType);
    }

    public static void showNotification(Context context) {
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "NotificationManager_00";
        String channelName = "NotificationManager";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Reply in thread")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000});

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.ic_notification);
        } else {
            notification.setSmallIcon(R.mipmap.ic_launcher);
        }

        manager.notify(1, notification.build());
    }

}
