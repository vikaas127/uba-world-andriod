package com.ubaworld.activity;

import android.app.AlarmManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubaworld.R;
import com.ubaworld.adapter.AlarmRepeatTypeAdapter;
import com.ubaworld.adapter.BillTypeAdapter;
import com.ubaworld.model.AlarmRepeatTypeData;
import com.ubaworld.model.BillTypeData;
import com.ubaworld.model.ReminderData;
import com.ubaworld.reminderAlarm.MyReceiver;
import com.ubaworld.utils.DBHelper;
import com.ubaworld.utils.LogUtils;
import com.ubaworld.utils.NotificationScheduler;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ubaworld.utils.Utils.actionDoneListenerToEditText;
import static com.ubaworld.utils.Utils.hideKeyboard;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.inVisibleViews;
import static com.ubaworld.utils.Utils.listOfBillType;
import static com.ubaworld.utils.Utils.listOfRepeatType;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showDatePickerDialog;
import static com.ubaworld.utils.Utils.showMinDatePickerDialog;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showViews;

public class AddBillActivity extends AppCompatActivity {

    @BindView(R.id.tv_HeaderTitle)
    TextView tv_HeaderTitle;

    @BindView(R.id.llView)
    LinearLayout llView;

    @BindView(R.id.tv_BillType)
    TextView tv_BillType;

    @BindView(R.id.ll_OtherType)
    LinearLayout ll_OtherType;

    @BindView(R.id.et_OtherReminder)
    EditText et_OtherReminder;

    @BindView(R.id.et_Amount)
    EditText et_Amount;

    @BindView(R.id.tv_StartDate)
    TextView tv_StartDate;

    @BindView(R.id.tv_RepeatType)
    TextView tv_RepeatType;

    @BindView(R.id.toggle_EndRepeat)
    ToggleButton toggle_EndRepeat;

    @BindView(R.id.tv_EndDate)
    TextView tv_EndDate;

    private String billId;
    private String repeatId;

    private List<BillTypeData> billTypeList;
    private List<AlarmRepeatTypeData> repeatTypeList;

    private DBHelper dbHelper;

    private String type;
    private int reminder_Id;
    private int alarm_Id;
    private long time;

    private String bill_type;
    private String amount;
    private String start_date;
    private String repeat_type;
    private String end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            reminder_Id = bundle.getInt("reminder_Id");
        }

        dbHelper = new DBHelper(this);
        initView();
    }

    private void initView() {
        billTypeList = listOfBillType();
        repeatTypeList = listOfRepeatType();

        if (type.equalsIgnoreCase("update")) {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_title_edit_bill));
            ReminderData data = dbHelper.getReminder(reminder_Id);
            for (int i = 0; i < billTypeList.size() - 1; i++) {
                if (listOfBillType().get(i).getBillType().equalsIgnoreCase(data.getBillType())) {
                    tv_BillType.setText(data.getBillType());
                    hideViews(ll_OtherType);
                    et_OtherReminder.setText("");
                    break;
                } else {
                    if (data.getBillType().equalsIgnoreCase("Other"))
                        et_OtherReminder.setText("");
                    else
                        et_OtherReminder.setText(data.getBillType());
                    tv_BillType.setText("Other");
                    showViews(ll_OtherType);
                }
            }
            String amount = data.getAmount();
            et_Amount.setText(amount);
            et_Amount.setSelection(amount.length());
            tv_StartDate.setText(data.getDate());
            tv_RepeatType.setText(data.getRepeatOption());
            tv_EndDate.setText(data.getEndDate());
            alarm_Id = data.getAlarm_Id();
            time = data.getTime();

            if (!TextUtils.isEmpty(tv_EndDate.getText().toString())) {
                toggle_EndRepeat.setChecked(true);
                showViews(tv_EndDate);
            }
        } else {
            tv_HeaderTitle.setText(getResources().getString(R.string.str_title_add_bill));
        }

        toggle_EndRepeat.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked)
                showViews(tv_EndDate);
            else {
                inVisibleViews(tv_EndDate);
                tv_EndDate.setText("");
            }
        });

    }

    @OnClick({R.id.iv_Left, R.id.tv_BillType, R.id.tv_StartDate, R.id.tv_RepeatType, R.id.tv_EndDate, R.id.btn_Save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_Left:
                finish();
                break;

            case R.id.tv_BillType:
                hideKeyboard(this);
                et_Amount.clearFocus();
                showBillTypePopup();
                break;

            case R.id.tv_StartDate:
                hideKeyboard(this);
                et_Amount.clearFocus();
                showDatePickerDialog(this, tv_StartDate);
                break;

            case R.id.tv_RepeatType:
                hideKeyboard(this);
                et_Amount.clearFocus();
                showRepeatTypePopup();
                break;

            case R.id.tv_EndDate:
                hideKeyboard(this);
                et_Amount.clearFocus();
                showMinDatePickerDialog(this, tv_EndDate);
                break;

            case R.id.btn_Save:
                if (isValid()) {
                    long interval = 0;
                    if (tv_RepeatType.getText().toString().equalsIgnoreCase("Every week"))
                        interval = AlarmManager.INTERVAL_DAY * 7;
                    else if (tv_RepeatType.getText().toString().equalsIgnoreCase("Every 2 week"))
                        interval = AlarmManager.INTERVAL_DAY * 14;
                    else if (tv_RepeatType.getText().toString().equalsIgnoreCase("Every Month"))
                        interval = AlarmManager.INTERVAL_DAY * 30;
                    else if (tv_RepeatType.getText().toString().equalsIgnoreCase("Every 3 Month"))
                        interval = AlarmManager.INTERVAL_DAY * 90;
                    else if (tv_RepeatType.getText().toString().equalsIgnoreCase("Every 6 Month"))
                        interval = AlarmManager.INTERVAL_DAY * 182;
                    else if (tv_RepeatType.getText().toString().equalsIgnoreCase("Every Year"))
                        interval = AlarmManager.INTERVAL_DAY * 364;

                    if (tv_BillType.getText().toString().equals("Other") && !et_OtherReminder.getText().toString().isEmpty()) {
                        bill_type = et_OtherReminder.getText().toString();
                    } else {
                        bill_type = tv_BillType.getText().toString();
                    }

                    if (type.equalsIgnoreCase("create")) {
                        Random random = new Random();
                        alarm_Id = 100000;
                        alarm_Id = random.nextInt(alarm_Id);
                        time = System.currentTimeMillis();

                        ReminderData new_data = new ReminderData(bill_type, amount, start_date, repeat_type, end_date, alarm_Id, interval, time);
                        dbHelper.addReminder(new_data);
                    } else {
                        ReminderData update_data = new ReminderData(reminder_Id, bill_type, amount, start_date, repeat_type, end_date, alarm_Id,
                                interval, time);
                        dbHelper.updateReminder(update_data);
                    }
                    NotificationScheduler.setReminder(this, MyReceiver.class, bill_type, alarm_Id, interval, start_date, end_date);
                    finish();
                }
                break;

        }
    }

    private boolean isValid() {
        bill_type = tv_BillType.getText().toString();
        amount = et_Amount.getText().toString();
        start_date = tv_StartDate.getText().toString();
        repeat_type = tv_RepeatType.getText().toString();
        end_date = tv_EndDate.getText().toString();

        if (TextUtils.isEmpty(bill_type)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_bill_type));
            return false;
        } else if (TextUtils.isEmpty(amount)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_bill_amount));
            return false;
        } else if (TextUtils.isEmpty(start_date)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_start_date));
            return false;
        } else if (TextUtils.isEmpty(repeat_type)) {
            showValidationAlertDialog(this, getResources().getString(R.string.valid_repeat_option));
            return false;
        }

        return true;
    }

    private void showBillTypePopup() {
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

        tvHeader.setText(getResources().getString(R.string.str_select_type));

        BillTypeAdapter adapter = new BillTypeAdapter(this, billTypeList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        rcvAlertPopup.setLayoutManager(layoutManager);
        rcvAlertPopup.setAdapter(adapter);

        adapter.setClickListener((itemView, position) -> {
            popupWindow.dismiss();
            tv_BillType.setText(billTypeList.get(position).getBillType());
            billId = billTypeList.get(position).getBillId();
            if (billId.equals("7"))
                showViews(ll_OtherType);
            else
                hideViews(ll_OtherType);
            LogUtils.e("BILL TYPE", "TITLE ---> " + tv_BillType.getText().toString() + " || ID ---> " + billId + logLine());
        });
    }

    private void showRepeatTypePopup() {
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

        tvHeader.setText(getResources().getString(R.string.str_select_repeat_option));

        AlarmRepeatTypeAdapter adapter = new AlarmRepeatTypeAdapter(this, repeatTypeList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        rcvAlertPopup.setLayoutManager(layoutManager);
        rcvAlertPopup.setAdapter(adapter);

        adapter.setClickListener((itemView, position) -> {
            popupWindow.dismiss();
            tv_RepeatType.setText(repeatTypeList.get(position).getRepeatType());
            repeatId = repeatTypeList.get(position).getRepeatId();
            LogUtils.e("REPEAT TYPE", "TITLE ---> " + tv_RepeatType.getText().toString() + " || ID ---> " + repeatId + logLine());
        });
    }

}
