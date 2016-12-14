package com.zhuoxin.news.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhuoxin.news.R;
import com.zhuoxin.news.activity.NewsActivity;
import com.zhuoxin.news.adapter.NewsAdapter;
import com.zhuoxin.news.entity.NewsInfo;
import com.zhuoxin.news.entity.NewsOfJuhe;
import com.zhuoxin.news.utils.DBUtil;
import com.zhuoxin.news.utils.HttpClientUtil;
import com.zhuoxin.news.utils.MyOpenHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created by Administrator on 2016/12/1.
 */

public class NewsFragment extends Fragment {
    private String type = "top";
    public static final String TYPE_TOP = "top";
    public static final String TYPE_GUOJI = "guoji";
    public static final String TYPE_KEJI = "keji";
    public static final String TYPE_SHEHUI = "shehui";
    public static final String TYPE_SHISHANG = "shishang";
    public static final String TYPE_YULE = "yule";
    public static final String TYPE_JUNSHI = "junshi";
    public static final String TYPE_TIYU = "tiyu";
    public static final String TYPE_GUONEI = "guonei";

    View view = null;
    //找到ListView
    @InjectView(R.id.lv_home_news)
    ListView lv_home_news;
    @InjectView(R.id.srl_news)
    SwipeRefreshLayout srl_news;

    final int JSON_STATUS_SUCCESS = 0;
    final int JSON_STATUS_FAIL = 1;
    final int JSON_STATUS_EXCEPTION = 2;

    NewsAdapter newsAdapter;
    List<NewsInfo> newsInfoList = new ArrayList<NewsInfo>();
    Context context;
    //异步处理步骤
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case JSON_STATUS_SUCCESS:
                    //如果成功，则取消刷新
                    Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                    srl_news.setRefreshing(false);
                    newsAdapter.notifyDataSetChanged();
                    break;
                case JSON_STATUS_FAIL:
                    Toast.makeText(context, "请求失败,响应码为：" + msg.arg1, Toast.LENGTH_SHORT).show();
                    //如果成功，则取消刷新
                    srl_news.setRefreshing(false);
                    break;
                case JSON_STATUS_EXCEPTION:
                    Exception e = (Exception) msg.obj;
                    Toast.makeText(context, "请求异常,异常信息为：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    //如果成功，则取消刷新
                    srl_news.setRefreshing(false);
                    break;
            }
        }
    };

    public NewsFragment() {

    }

    @SuppressLint("ValidFragment")
    public NewsFragment(String type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fradment_news, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //从Fragment中可以获取到依附的Activity
        context = getActivity();
        //执行初始化ListView操作
        newsAdapter = new NewsAdapter(context);
        newsAdapter.setDataList(newsInfoList);
        lv_home_news.setAdapter(newsAdapter);
        lv_home_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        newsAdapter.isFlying = true;
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        newsAdapter.isFlying = false;
                        break;
                    case SCROLL_STATE_IDLE:
                        newsAdapter.isFlying = false;
                        newsAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        lv_home_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View iv_news = view.findViewById(R.id.iv_news);
                Intent intent = new Intent(context, NewsActivity.class);
                NewsInfo newsInfo = newsAdapter.getItem(position);
                String url = newsInfo.getUrl();
                String largeImageURL = newsInfo.getLargeImageURL();
                String title = newsInfo.getTitle();
                intent.putExtra("url", url);
                intent.putExtra("largeImageURL", largeImageURL);
                intent.putExtra("title", title);
                if (Build.VERSION.SDK_INT >= 21) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), iv_news, "news").toBundle());

                } else {
                    startActivity(intent);
                }

            }
        });
        lv_home_news.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //创建popupWindow
                final PopupWindow popupWindow = new PopupWindow();
                //设置宽高和自定义View布局
                View popupView = LayoutInflater.from(context).inflate(R.layout.layout_popupwindow, null);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setContentView(popupView);
                //点击外侧消失
                popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
                popupWindow.setOutsideTouchable(true);
                //展示popupWindow
                popupWindow.showAsDropDown(view,50,-50);
                //popupWindow.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
                //popupWindow.showAsDropDown(view);
                //对按钮设置单击事件
                popupView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyOpenHelper myOpenHelper = new MyOpenHelper(context, "News.db", null, 1);
                        SQLiteDatabase database = myOpenHelper.getReadableDatabase();
                        if (DBUtil.isNewsExist(database, newsAdapter.getItem(position))) {
                            Toast.makeText(context, "已收藏", Toast.LENGTH_SHORT).show();
                        } else {
                            DBUtil.insert(database, newsAdapter.getItem(position));
                            Toast.makeText(context, "成功收藏", Toast.LENGTH_SHORT).show();
                        }

                        popupWindow.dismiss();
                    }
                });
                popupView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                return true;
            }
        });
        initListView();
        //刷新操作
        srl_news.setColorSchemeResources(R.color.colorPrimary);
        srl_news.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initListView();
            }
        });
    }

    private void initListView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL targetURL = null;
                try {
                    //根据新闻类型，访问相应的接口
                    targetURL = new URL("http://v.juhe.cn/toutiao/index?type=" + type + "&key=d728ab4e75e137c4f23aec12ed3ee6cd");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpClientUtil.getJson(targetURL, onJsonGetListener);
            }
        }).start();
    }

    private HttpClientUtil.OnJsonGetListener onJsonGetListener = new HttpClientUtil.OnJsonGetListener() {
        @Override
        public void jsonGetSuccess(String json) {
            //使用Gson解析数据
            Gson gson = new Gson();
            NewsOfJuhe newsData = gson.fromJson(json, NewsOfJuhe.class);
            NewsOfJuhe.Result result = newsData.getResult();
            if (result != null) {
                List<NewsOfJuhe.Data> dataList = result.getData();
                if (dataList != null) {
                    for (NewsOfJuhe.Data data : dataList) {
                        String url = data.getUrl();
                        String imageURL = data.getThumbnail_pic_s();
                        String largeImageURL = data.getThumbnail_pic_s03();
                        String title = data.getTitle();
                        String newsType;
                        if (type.equals(TYPE_TOP)){
                            newsType = data.getRealtype();
                        }else {
                            newsType = data.getCategory();
                        }

                        NewsInfo info = new NewsInfo(url, imageURL, largeImageURL, title, newsType);
                        newsInfoList.add(info);
                    }
                    Message msg = handler.obtainMessage();
                    msg.what = JSON_STATUS_SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = JSON_STATUS_FAIL;
                    msg.arg1 = -1;
                    handler.sendMessage(msg);
                }

            } else {
                Message msg = handler.obtainMessage();
                msg.what = JSON_STATUS_FAIL;
                msg.arg1 = -1;
                handler.sendMessage(msg);
            }


        }

        @Override
        public void jsonGetFail(int responseCode) {
            Message msg = handler.obtainMessage();
            msg.what = JSON_STATUS_FAIL;
            msg.arg1 = responseCode;
            handler.sendMessage(msg);
        }

        @Override
        public void jsonGetException(Exception e) {
            Message msg = handler.obtainMessage();
            msg.what = JSON_STATUS_EXCEPTION;
            msg.obj = e;
            handler.sendMessage(msg);
        }
    };
}
