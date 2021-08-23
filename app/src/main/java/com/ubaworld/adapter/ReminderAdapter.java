package com.ubaworld.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.ubaworld.R;
import com.ubaworld.activity.AddBillActivity;
import com.ubaworld.fragment.ReminderFragment;
import com.ubaworld.model.ReminderData;
import com.ubaworld.reminderAlarm.MyReceiver;
import com.ubaworld.utils.DBHelper;
import com.ubaworld.utils.NotificationScheduler;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderAdapter extends RecyclerSwipeAdapter<ReminderAdapter.MyView> {

    private ReminderFragment activity;
    private Context context;
    private List<ReminderData> list;
    private DBHelper dbHelper;

    public ReminderAdapter(Context context, List<ReminderData> list, ReminderFragment activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    class MyView extends RecyclerView.ViewHolder {
        @BindView(R.id.userSwipe)
        SwipeLayout swipeLayout;

        @BindView(R.id.tv_BillType)
        TextView tv_BillType;

        @BindView(R.id.tv_Amount)
        TextView tv_Amount;

        @BindView(R.id.tv_Date)
        TextView tv_Date;

        @BindView(R.id.ll_Update_Wrapper)
        LinearLayout llUpdateWrapper;

        @BindView(R.id.ll_Delete_Wrapper)
        LinearLayout llDeleteWrapper;

        @BindView(R.id.ll_Main_Wrapper)
        LinearLayout llMainWrapper;

        MyView(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public ReminderAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_reminder_new, null);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderAdapter.MyView holder, int position) {
        dbHelper = new DBHelper(context);
        ReminderData data = list.get(position);
        holder.tv_BillType.setText(data.getBillType());
        double amount = Double.parseDouble(data.getAmount());
        holder.tv_Amount.setText(String.format("£%.2f", amount));
        holder.tv_Date.setText(data.getDate());

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.llDeleteWrapper);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.llUpdateWrapper);
        mItemManger.bindView(holder.itemView, position);

        holder.llDeleteWrapper.setOnClickListener(v -> {
            dbHelper.deleteReminder(data);
            NotificationScheduler.cancelReminder(context, MyReceiver.class, data.getAlarm_Id());
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            activity.onMethodCallback();
        });

        holder.llUpdateWrapper.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddBillActivity.class);
            intent.putExtra("type", "update");
            intent.putExtra("reminder_Id", data.getId());
            activity.startActivity(intent);
        });

    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.userSwipe;
    }

}
