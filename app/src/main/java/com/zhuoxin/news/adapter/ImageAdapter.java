package com.zhuoxin.news.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zhuoxin.news.R;
import com.zhuoxin.news.entity.GirlsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */

public class ImageAdapter extends RecyclerView.Adapter <ImageAdapter.ViewHolder>{
    //数据
    List<GirlsInfo.ResultsBean>resultsBeanList = new ArrayList<>();

    public List<GirlsInfo.ResultsBean> getResultsBeanList() {
        return resultsBeanList;
    }

    public void setResultsBeanList(List<GirlsInfo.ResultsBean> resultsBeanList) {
        this.resultsBeanList = resultsBeanList;
    }

    Context context;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_image;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }
    @Override
    public int getItemCount() {
        return resultsBeanList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,null);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context)
                .load(resultsBeanList.get(position).getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.shape_button_red)
                .into(holder.iv_image);

    }
}
