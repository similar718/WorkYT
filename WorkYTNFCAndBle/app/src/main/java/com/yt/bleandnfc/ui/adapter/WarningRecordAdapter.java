//package com.yt.bleandnfc.ui.adapter;
//
//import android.view.ViewGroup;
//
//import com.yt.base.customview.BaseCustomViewModel;
//import com.yt.base.recyclerview.BaseViewHolder;
//import com.yt.bleandnfc.mvvm.view.WarningRecordItemView;
//import com.yt.bleandnfc.mvvm.viewmodel.WarningRecordItemViewModel;
//
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class WarningRecordAdapter extends RecyclerView.Adapter<BaseViewHolder>{
//
//    private List<WarningRecordItemViewModel> mItems;
//
//    public void setData(List<WarningRecordItemViewModel> items) {
//        mItems = items;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (parent != null) {
//            return new BaseViewHolder(new WarningRecordItemView(parent.getContext()));
//        }
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
//        holder.bind(mItems.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        if (mItems != null) {
//            return mItems.size();
//        }
//        return 0;
//    }
//}
