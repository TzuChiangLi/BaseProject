package com.ftrend.zgp.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author LZQ
 * @content 数据库工具类
 */
public class DataBaseHelper extends SQLiteOpenHelper implements IDataBaseHelper {
    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 创建数据库
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * 更新数据库版本
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void save(Object obj) {

    }

    @Override
    public void saveAll(Collection collection) {

    }

    /**
     * 查询表内数据
     * @param table 表名
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> queryAll(Class<T> table) {
        return null;
    }

    @Override
    public <T> List<T> queryAll(Class<T> table, String order) {
        return null;
    }

    @Override
    public <T> List<T> queryAll(Class<T> table, String order, int limit) {
        return null;
    }

    @Override
    public <T> T queryById(Class<T> table, Object id) {
        return null;
    }

    /**
     * 清除表内数据
     * @param table
     */
    @Override
    public void clear(Class table) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table.getSimpleName(),null,null);
    }

    /**
     * 删除
     * @param obj
     */
    @Override
    public void delete(Object obj) {

    }

    @Override
    public void deleteAll(Collection collection) {

    }


}
