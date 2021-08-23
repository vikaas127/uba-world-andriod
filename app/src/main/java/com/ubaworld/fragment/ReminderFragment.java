package com.ubaworld.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ubaworld.R;
import com.ubaworld.activity.AddBillActivity;
import com.ubaworld.adapter.ReminderAdapter;
import com.ubaworld.interfaces.AdapterCallback;
import com.ubaworld.model.ReminderData;
import com.ubaworld.reminderAlarm.MyReceiver;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.DBHelper;
import com.ubaworld.utils.NotificationScheduler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.material.color.MaterialColors.ALPHA_FULL;
import static com.ubaworld.utils.Utils.getBooleanFromUserDefaults;
import static com.ubaworld.utils.Utils.saveBooleanToUserDefaults;

public class ReminderFragment extends Fragment implements AdapterCallback {

    @BindView(R.id.rcv_Reminder)
    RecyclerView rcv_Reminder;

    @BindView(R.id.tv_TotalBill)
    TextView tv_TotalBill;

    @BindView(R.id.toggle_Disable)
    ToggleButton toggle_Disable;

    private List<ReminderData> list = new ArrayList<>();
    private DBHelper dbHelper;
    private ReminderAdapter adapter;

    private Paint p = new Paint();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        dbHelper = new DBHelper(getActivity());

        boolean isNotify = getBooleanFromUserDefaults(getActivity(), Constants.DISABLE_NOTIFICATION);
        toggle_Disable.setChecked(isNotify);

        toggle_Disable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveBooleanToUserDefaults(ReminderFragment.this.getActivity(), Constants.DISABLE_NOTIFICATION, true);
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        NotificationScheduler.cancelReminder(getActivity(), MyReceiver.class, list.get(i).getAlarm_Id());
                    }
                }
            } else {
                saveBooleanToUserDefaults(ReminderFragment.this.getActivity(), Constants.DISABLE_NOTIFICATION, false);
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        NotificationScheduler.setReminder(ReminderFragment.this.getActivity(), MyReceiver.class, list.get(i).getBillType(),
                                list.get(i).getAlarm_Id(), list.get(i).getInterval(), list.get(i).getDate(), list.get(i).getEndDate());
                    }
                }
            }
        });
//        enableSwipe();
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
        list = dbHelper.getAllReminder();
        Log.e("SIZE", "BILL LIST ---> " + list.size());

        rcv_Reminder.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapter = new ReminderAdapter(getActivity(), list, ReminderFragment.this);
        rcv_Reminder.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        double amount = 0;
        for (int i = 0; i < list.size(); i++) {
            amount += Double.parseDouble(list.get(i).getAmount());
        }

        tv_TotalBill.setText(String.format("£%.2f", amount));

    }

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                final ReminderData data = list.get(position);
                if (direction == ItemTouchHelper.LEFT) {
                    dbHelper.deleteReminder(data);
                    NotificationScheduler.cancelReminder(getActivity(), MyReceiver.class, data.getAlarm_Id());
                    adapter.removeItem(position);
                    setData();
                } else {
                    Intent intent = new Intent(getActivity(), AddBillActivity.class);
                    intent.putExtra("type", "update");
                    intent.putExtra("reminder_Id", data.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#0773B5"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#FF3533"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_back);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rcv_Reminder);
    }

    @Override
    public void onMethodCallback() {
        setData();
    }
}
