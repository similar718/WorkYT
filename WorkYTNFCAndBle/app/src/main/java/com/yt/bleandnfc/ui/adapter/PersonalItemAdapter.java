package com.yt.bleandnfc.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.PersonalModel;
import com.yt.bleandnfc.databinding.ItemPersonalBinding;

import java.util.List;

public class PersonalItemAdapter extends Adapter<PersonalItemAdapter.BaseViewHolder> {

    List<PersonalModel> modelList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    private Context mContext;
    public PersonalItemAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<PersonalModel> modelList){
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    ItemPersonalBinding dataBinding;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),R.layout.item_personal,viewGroup,false);
        return new BaseViewHolder(dataBinding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (modelList.get(position).type == 0) { // offline
            dataBinding.ivHead.setImageResource(R.drawable.personal_offline);
            dataBinding.tvName.setText(modelList.get(position).name);
            dataBinding.tvName.setTextColor(mContext.getColor(R.color.tab_textcolor_normal));
            dataBinding.tvStatus.setText("离线");
            dataBinding.tvStatus.setTextColor(mContext.getColor(R.color.tab_textcolor_normal));
            dataBinding.tvTime.setText(modelList.get(position).time);
        } else if (modelList.get(position).type == 1) { // online
            dataBinding.ivHead.setImageResource(R.drawable.personal_online);
            dataBinding.tvName.setText(modelList.get(position).name);
            dataBinding.tvName.setTextColor(mContext.getColor(R.color.text_black));
            dataBinding.tvStatus.setText("在线");
            dataBinding.tvStatus.setTextColor(mContext.getColor(R.color.personal_blue));
            dataBinding.tvTime.setText(modelList.get(position).time);

        }
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
