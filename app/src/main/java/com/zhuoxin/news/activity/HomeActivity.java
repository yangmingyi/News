package com.zhuoxin.news.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.zhuoxin.news.R;
import com.zhuoxin.news.entity.GirlsInfo;
import com.zhuoxin.news.fragment.ImageFragment;
import com.zhuoxin.news.fragment.NewsCollectFragment;
import com.zhuoxin.news.fragment.NewsFragment;
import com.zhuoxin.news.fragment.NewsPagerFragment;
import com.zhuoxin.news.interfaces.GirlsInterface;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //创建成员变量，用来保存当前的Fragment
    Fragment currentFragment;

    Context context;
    TextView tv_user_name;
    ImageView iv_user_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initSystemUI();
        showNewsFragment();
        //初始化ShareSDK
        ShareSDK.initSDK(this,"157fc72150700");


    }

    public void initSystemUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView =navigationView.getHeaderView(0);
        iv_user_photo = (ImageView) headerView.findViewById(R.id.iv_user_photo);
        tv_user_name = (TextView) headerView.findViewById(R.id.tv_user_name);
        iv_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置单击事件
                login();

                Toast.makeText(HomeActivity.this, "用户头像", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void login(){
        Platform qq = ShareSDK.getPlatform(context, QQ.NAME);
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                Logger.d("登录成功"+platform.getDb().exportData());
                final String user_photo_url = platform.getDb().getUserIcon();
                final String user_name = platform.getDb().getUserName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       //Picasso.with(context).load(user_photo_url).into(iv_user_photo);
                        ImageOptions imageOptions =new ImageOptions.Builder().setCircular(true).build();
                        x.image().bind(iv_user_photo,user_photo_url,imageOptions);

                        tv_user_name.setText(user_name);
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {


            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        qq.authorize();
        qq.showUser(null);

    }
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("极客导航");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://www.jikedaohang.com/");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("程序员自己的导航");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl("http://img3.imgtn.bdimg.com/it/u=2489051743,1646771720&fm=21&gp=0.jpg");
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("母猪排队掉进水沟视频");
        // site是分享此内容的网站名称，仅在QQ空间使用
       // oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://www.baidu.com");

        // 启动分享GUI
        oks.show(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getJson(){
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //获取接口对象
        GirlsInterface girlsInterface = retrofit.create(GirlsInterface.class);
        //假如队列进行数据解析
        Call<GirlsInfo> call = girlsInterface.getGirls();
        call.enqueue(new Callback<GirlsInfo>() {
            @Override
            public void onResponse(Call<GirlsInfo> call, Response<GirlsInfo> response) {
                GirlsInfo girlsInfo = response.body();
                List<GirlsInfo.ResultsBean>resultsBeanList = girlsInfo.getResults();
                for (int i = 0; i<resultsBeanList.size();i++){
                    Log.d(TAG,"getJson:获取结果："+resultsBeanList.get(i).getUrl());
                }

            }

            @Override
            public void onFailure(Call<GirlsInfo> call, Throwable t) {
                System.out.print(t.getMessage());
            }
        });
        Log.d(TAG,"getJson:jieguo");

    }
    private static final String TAG = "HomeActivity";
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            // Handle the camera action
            showNewsFragment();
        } else if (id == R.id.nav_gallery) {
            showImageFragment();

        } else if (id == R.id.nav_news_collect) {
            showNewsCollectFragment();

        } else if (id == R.id.nav_login) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            showShare();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "发送消息成功", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showNewsFragment() {
        if (currentFragment instanceof NewsPagerFragment){
            Toast.makeText(this, "数据未改变", Toast.LENGTH_SHORT).show();
        }else{
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            NewsPagerFragment newsPagerFragment = new NewsPagerFragment();
            ft.replace(R.id.fl_home, newsPagerFragment);
            ft.commit();
            currentFragment = newsPagerFragment;
        }
        
    }

    private void showImageFragment() {
        if (currentFragment instanceof  ImageFragment){
            Toast.makeText(this, "数据未改变", Toast.LENGTH_SHORT).show();
        }else{
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ImageFragment imageFragment = new ImageFragment();
            ft.replace(R.id.fl_home, imageFragment);
            ft.commit();
            currentFragment = imageFragment;
        }
    }
    private void showNewsCollectFragment() {
        if (currentFragment instanceof NewsCollectFragment){
            Toast.makeText(this, "数据未改变", Toast.LENGTH_SHORT).show();
        }else{
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            NewsCollectFragment newsCollectFragment = new NewsCollectFragment();
            ft.replace(R.id.fl_home, newsCollectFragment);
            ft.commit();
            currentFragment = newsCollectFragment;
        }

    }

}
