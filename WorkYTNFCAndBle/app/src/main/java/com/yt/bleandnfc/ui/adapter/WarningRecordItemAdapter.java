package com.yt.bleandnfc.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.databinding.ItemWarningRecordBinding;
import com.yt.bleandnfc.mvvm.viewmodel.WarningRecordItemViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class WarningRecordItemAdapter extends Adapter<WarningRecordItemAdapter.BaseViewHolder> {

    List<WarningRecordItemViewModel> modelList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public WarningRecordItemAdapter() { }

    public void setData(List<WarningRecordItemViewModel> modelList){
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    ItemWarningRecordBinding dataBinding;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),R.layout.item_warning_record,viewGroup,false);
        return new BaseViewHolder(dataBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        dataBinding.tvContent.setText(modelList.get(position).contentStr);
        dataBinding.tvType.setText(modelList.get(position).typeStr);
        dataBinding.tvTime.setText(modelList.get(position).timeStr);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder{

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v,getLayoutPosition());
                }
            });
        }
    }
}
