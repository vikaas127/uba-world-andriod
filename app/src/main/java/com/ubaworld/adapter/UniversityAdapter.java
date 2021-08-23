package com.ubaworld.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubaworld.R;
import com.ubaworld.model.UniversityData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.MyView> implements Filterable {

    private Context context;
    private List<UniversityData.Data> list;
    private List<UniversityData.Data> filterUniversityList;
    private ValueFilter valueFilter;

    private RecyclerViewClickListener clickListener;

    public UniversityAdapter(Context context, List<UniversityData.Data> list) {
        this.context = context;
        this.list = list;
        this.filterUniversityList = list;
    }

    public void setClickListener(RecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class MyView extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvId)
        TextView tvId;

        MyView(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);
            tvTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, getName(tvTitle), getId(tvId));
        }

        private String getName(TextView tvTitle) {
            return tvTitle.getText().toString();
        }

        private String getId(TextView tvId) {
            return tvId.getText().toString();
        }
    }

    @NonNull
    @Override
    public UniversityAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_popup, null);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UniversityAdapter.MyView holder, int position) {
        holder.tvTitle.setText(list.get(position).name);
        holder.tvId.setText(String.valueOf(list.get(position).id));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, String name, String id);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<UniversityData.Data> filterList = new ArrayList<>();
                for (int i = 0; i < filterUniversityList.size(); i++) {
                    if ((filterUniversityList.get(i)).name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        UniversityData.Data data = new UniversityData.Data(filterUniversityList.get(i).name, filterUniversityList.get(i).id);
                        filterList.add(data);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filterUniversityList.size();
                results.values = filterUniversityList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<UniversityData.Data>) results.values;
            notifyDataSetChanged();
        }
    }

}
