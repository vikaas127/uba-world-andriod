package com.ubaworld.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubaworld.R;
import com.ubaworld.interfaces.ItemClickListener;
import com.ubaworld.model.GenderData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenderAdapter extends RecyclerView.Adapter<GenderAdapter.MyView>  {

    private Context context;
    private List<GenderData> list;

    private ItemClickListener listener;

    public GenderAdapter(Context context, List<GenderData> list) {
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
    public GenderAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_popup, null);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GenderAdapter.MyView holder, int position) {
        holder.tvTitle.setText(list.get(position).getGender());

        holder.itemView.setOnClickListener(v -> listener.onClick(holder.itemView, position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
