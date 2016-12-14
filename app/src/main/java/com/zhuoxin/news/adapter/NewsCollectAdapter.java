package com.zhuoxin.news.adapter;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhuoxin.news.R;
import com.zhuoxin.news.activity.NewsActivity;
import com.zhuoxin.news.entity.NewsInfo;
import com.zhuoxin.news.utils.BitmapUtil;
import com.zhuoxin.news.utils.DBUtil;
import com.zhuoxin.news.utils.MyOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */

public class NewsCollectAdapter extends RecyclerView.Adapter<NewsCollectAdapter.MyViewHolder> {
    //数据
    List<NewsInfo> newsInfosList = new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;
    public NewsCollectAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public List<NewsInfo> getNewsInfosList() {
        return newsInfosList;
    }

    public void setNewsInfosList(List<NewsInfo> newsInfosList) {
        this.newsInfosList = newsInfosList;
    }


    /**
     * 建立ViewHolder,继承自RecyclerView中的ViewHolder
     * 创建一个构造函数，构造函数中传入一个itemView，itemView类似ListView中的布局
     * 其他的用法和之前写的ViewHolder一样
     */
    static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_news;
        TextView tv_title;
        TextView tv_type;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_news = (ImageView) itemView.findViewById(R.id.iv_news);
            tv_title = (TextView) itemView.findViewById(R.id.tv_news_title);
            tv_type = (TextView) itemView.findViewById(R.id.tv_news_type);
        }
    }
    @Override
    public int getItemCount() {
        return newsInfosList.size();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_news,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NewsInfo newsInfo = newsInfosList.get(position);
        final int posit = position;
        //获取holder的所有view
        View view = holder.itemView;
        //对每一个view设置点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先找到要进行转场动画的view

                Intent intent = new Intent(context, NewsActivity.class);
                String url = newsInfo.getUrl();
                String largeImageURL = newsInfo.getLargeImageURL();
                String title = newsInfo.getTitle();
                intent.putExtra("url", newsInfo.getUrl());
                intent.putExtra("largeImageURL", newsInfo.getLargeImageURL());
                intent.putExtra("title", newsInfo.getTitle());
                //如果版本大于21(5.0),就运行转场动画,否则,直接跳转
                context.startActivity(intent);

            }
        });
        //对每一个view设置长按事件
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("是否取消收藏?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MyOpenHelper myDBOpenHelper = new MyOpenHelper(context, "News.db", null, 1);
                                DBUtil.delete(myDBOpenHelper.getWritableDatabase(), newsInfo);
                                newsInfosList.remove(newsInfo);
                                //1.直接改变整个view,无动画
                                //notifyDataSetChanged();
                                //2.通知范围改变,有动画
                                 notifyItemRemoved(posit);
                                 notifyItemRangeChanged(posit,newsInfosList.size());
                                //3.直接获取数据改变后的位置,有动画
                                //notifyItemRemoved(holder.getLayoutPosition());
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                alertDialog.show();
                return true;
            }
        });
        //设置图标和文字
        Picasso.with(context)
                .load(newsInfo.getImageURL())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.shape_button_red)
                .into(holder.iv_news);
        holder.tv_title.setText(newsInfo.getTitle());
        holder.tv_type.setText(newsInfo.getType());

    }
}


