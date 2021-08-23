package com.ubaworld.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubaworld.R;
import com.ubaworld.interfaces.ItemClickListener;
import com.ubaworld.model.AlarmRepeatTypeData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmRepeatTypeAdapter extends RecyclerView.Adapter<AlarmRepeatTypeAdapter.MyView>  {

    private Context context;
    private List<AlarmRepeatTypeData> list;

    private ItemClickListener listener;

    public AlarmRepeatTypeAdapter(Context context, List<AlarmRepeatTypeData> list) {
        this.context = context;
        this.list = list;
    }

    public void setClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    class MyView extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        MyView(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public AlarmRepeatTypeAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_popup, null);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmRepeatTypeAdapter.MyView holder, int position) {
        holder.tvTitle.setText(list.get(position).getRepeatType());

        holder.itemView.setOnClickListener(v -> listener.onClick(holder.itemView, position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
