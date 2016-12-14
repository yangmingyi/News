package com.zhuoxin.news.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/12/6.
 */

public class MyOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_NEWS = "create table News(" +
            "url text," +
            "imageURL text," +
            "largeImageURL text," +
            "title text," +
            "type text)";
    /**
     *
     * @param context 上下文
     * @param name  数据库名称
     * @param factory
     * @param version  数据库版本
     */
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 创建数据库操作
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //在创建数据的时候，创建一张数据表
        //"create table news(url text,imageURL text,largeImageURL text,title text,type text";
        db.execSQL(CREATE_TABLE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
