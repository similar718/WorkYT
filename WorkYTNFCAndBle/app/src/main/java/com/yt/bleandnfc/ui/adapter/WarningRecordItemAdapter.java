package com.yt.bleandnfc.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.AlarmFindAlarmByStateModel;
import com.yt.bleandnfc.databinding.ItemWarningRecordBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class WarningRecordItemAdapter extends Adapter<WarningRecordItemAdapter.BaseViewHolder> {

    List<AlarmFindAlarmByStateModel.ObjBean> modelList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public WarningRecordItemAdapter() { }

    public void setData(List<AlarmFindAlarmByStateModel.ObjBean> modelList){
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
        StringBuilder stringBuilder = new StringBuilder();
        // 操作员“XXX（工号：11111）”于2020年9月24日09点25分使用工作梯（编号：xxxx）发生违规行为，违规内容“违规停放”。
        stringBuilder.append("操作员“")
                .append(modelList.get(position).getUserName())
                .append("（工号：")
                .append(modelList.get(position).getUserId())
                .append("）”于")
                .append(modelList.get(position).getCreateTime())
                .append("使用工作梯（编号：")
                .append(modelList.get(position).getCarNumber())
                .append("）发生违规行为，违规内容“违规停放”。");
        dataBinding.tvContent.setText(stringBuilder);
//        dataBinding.tvType.setText(modelList.get(position).getMsg());
        dataBinding.tvTime.setText(modelList.get(position).getCreateTime());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (modelList == null || modelList.size() == 0) {
            return 0;
        }
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
