package com.ubaworld.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubaworld.R;
import com.ubaworld.model.OtherBuyingData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherBuyingOptionAdapter extends RecyclerView.Adapter<OtherBuyingOptionAdapter.MyView> {

    private Context context;
    private List<OtherBuyingData> list;

    private RecyclerViewClickListener clickListener;

    public OtherBuyingOptionAdapter(Context context, List<OtherBuyingData> list) {
        this.context = context;
        this.list = list;
    }

    public void setClickListener(RecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class MyView extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_Title)
        TextView tv_Title;

        MyView(@NonNull final View view, RecyclerViewClickListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, getAdapterPosition());
        }

    }

    @NonNull
    @Override
    public OtherBuyingOptionAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_other_buying, null);
        return new MyView(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final OtherBuyingOptionAdapter.MyView holder, int position) {
        holder.tv_Title.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}
