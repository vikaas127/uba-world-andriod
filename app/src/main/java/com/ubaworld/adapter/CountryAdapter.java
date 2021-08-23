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
import com.ubaworld.interfaces.ItemClickListener;
import com.ubaworld.model.CountryData;
import com.ubaworld.model.GenderData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyView> implements Filterable {

    private Context context;
    private List<CountryData> list;
    private List<CountryData> filterCountryList;
    private ValueFilter valueFilter;

    private RecyclerViewClickListener clickListener;

    public CountryAdapter(Context context, List<CountryData> list) {
        this.context = context;
        this.list = list;
        this.filterCountryList = list;
    }

    public void setClickListener(RecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class MyView extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        MyView(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);
            tvTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, getName(tvTitle));
        }

        private String getName(TextView tvTitle) {
            return tvTitle.getText().toString();
        }

    }

    @NonNull
    @Override
    public CountryAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_popup, null);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CountryAdapter.MyView holder, int position) {
        holder.tvTitle.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, String name);
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
                ArrayList<CountryData> filterList = new ArrayList<>();
                for (int i = 0; i < filterCountryList.size(); i++) {
                    if ((filterCountryList.get(i)).getName().toLowerCase().contains(constraint.toString().toLowerCase())) {

                        CountryData data = new CountryData(filterCountryList.get(i).getName(), filterCountryList.get(i).getCode());
                        filterList.add(data);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filterCountryList.size();
                results.values = filterCountryList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<CountryData>) results.values;
            notifyDataSetChanged();
        }
    }

}
