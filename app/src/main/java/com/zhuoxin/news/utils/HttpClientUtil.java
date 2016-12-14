package com.zhuoxin.news.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/12/1.
 * 网络访问工具类
 */

public class HttpClientUtil {
    public interface OnJsonGetListener {
        public void jsonGetSuccess(String json);//成功回调，返回Json
        public void jsonGetFail(int responseCode);//失败回调，返回响应码
        public void jsonGetException(Exception e);//异常回调，返回异常
    }
    /**
     * 从网络获取Json数据
     * @param targetURL
     * @return
     */
    public static void getJson(URL targetURL,OnJsonGetListener listener){
        HttpURLConnection httpURLConnection = null;
        StringBuffer json = new StringBuffer();
        try {
            //从URL中打开连接
            httpURLConnection = (HttpURLConnection) targetURL.openConnection();
            //设置连接参数
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            //开启连接
            httpURLConnection.connect();
            //获取响应码，判断状态是否为200
            if (httpURLConnection.getResponseCode() == 200){
                //读取数据
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String msg = null;
                //循环取出数据，并放入Json中
                while ((msg = bufferedReader.readLine())!= null){
                    json.append(msg);

                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                listener.jsonGetSuccess(json.toString());

            }else {
                //不成功
                listener.jsonGetFail(httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            listener.jsonGetException(e);
        }
        finally {
            httpURLConnection.disconnect();
        }

    }

    /**
     * 从网络获取图片，设置到对应ImageView上
     * @param imageURL   图片网址
     * @param imageView  图片对应的ImageView
     */
    public static void setBitmapToImageView(final Context context, final URL imageURL, final ImageView imageView){
        final String fileMD5 = MD5Util.getMD5(imageURL.toString());
        AsyncTask<URL,Void,Bitmap> asyncTask = new AsyncTask<URL, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(URL... params) {
                Bitmap bitmap = null;
                URL targetURL = params[0];
                //进行http操作
                try {
                    HttpURLConnection connection = (HttpURLConnection) targetURL.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.connect();
                    if (connection.getResponseCode() == 200){
                        InputStream is = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                //取出tag
                String tag = (String) imageView.getTag();
                if (tag.equals(imageURL.toString())){
                    BitmapUtil.bitmapLruCache.put(fileMD5,bitmap);
                    BitmapUtil.setFileCache(context,imageURL.toString(),bitmap);
                    imageView.setImageBitmap(bitmap);
                }

            }
        };
        asyncTask.execute(imageURL);
    }
}
