package com.ftrend.zgp.utils.db;

import java.util.Collection;
import java.util.List;

public interface IDataBaseHelper {
    /**
     * @param obj
     */
    void save(Object obj); //保存数据

    /**
     * @param collection 需要保存的数据
     */
    void saveAll(Collection collection); //保存所有数据

    /**
     * 根据表名查询全部数据
     * @param table 表名
     * @param <T>   类
     * @return
     */
    <T> List<T> queryAll(Class<T> table); //根据类名（表名）查找所有的数据

    /**
     * @param table
     * @param order
     * @param <T>
     * @return
     */
    <T> List<T> queryAll(Class<T> table, String order); //通过排序的方式查找所有的数据

    /**
     * @param table
     * @param order
     * @param limit
     * @param <T>
     * @return
     */
    <T> List<T> queryAll(Class<T> table, String order, int limit); //通过排序和一夜显示多少条数据来查询所有的数据

    /**
     * @param table
     * @param id
     * @param <T>
     * @return
     */
    <T> T queryById(Class<T> table, Object id);//通过id查找对应的数据

    void clear(Class table); //清空对应表名中的所有数据

    /**
     * @param obj
     */
    void delete(Object obj); //删除对应的数据;

    /**
     * @param collection
     */
    void deleteAll(Collection collection); // 删除集合中所有的数据
}
