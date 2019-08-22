package com.ftrend.zgp.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.ftrend.zgp.utils.log.LogUtil;

/**
 * 数据库调用工具
 *
 * @author liziqiang@ftrend.cn
 */
public class DatabaseManger {
    private DBHelper dbHelper;
    private static DatabaseManger INSTANCE = null;
    private SQLiteDatabase db;

    /**
     * 构造方法上下文
     *
     * @param context
     */
    private DatabaseManger(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取本类对象的实例
     *
     * @param context 上下文
     * @return 实例
     */
    public static final DatabaseManger getInstance(Context context) {
        if (INSTANCE == null) {
            if (context == null) {
                throw new RuntimeException("Context is null.");
            }
            INSTANCE = new DatabaseManger(context);
        }
        return INSTANCE;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (db.isOpen()) {
            db.close();
            db = null;
        }
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
        if (INSTANCE != null) {
            INSTANCE = null;
        }
    }

    /**
     * 执行一条sql语句
     */
    public void execSql(String sql) {
        try {
            if (db.isOpen()) {
                db.execSQL(sql);
            } else {
                LogUtil.e("execSql:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e(Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + e.getMessage());
        }
    }

    /**
     * sql执行查询操作的sql语句
     * selectionargs查询条件
     * 返回查询的游标，可对数据进行操作，但是需要自己关闭游标
     */
    public Cursor queryData2Cursor(String sql, String[] selectionArgs) throws Exception {
        Cursor cursor = null;
        try {
            if (db.isOpen()) {
                cursor = db.rawQuery(sql, selectionArgs);
            } else {
                LogUtil.e("queryData2Cursor:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e(Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + e.getMessage());
        }
        return cursor;
    }

    /**
     * 查询表中数据总条数
     * 返回表中数据条数
     */
    public int getDataCounts(String table) throws Exception {
        Cursor cursor = null;
        int counts = 0;
        try {
            if (db.isOpen()) {
                cursor = queryData2Cursor("select * from " + table, null);
                if (cursor != null && cursor.moveToFirst()) {
                    counts = cursor.getCount();
                }
            } else {
                LogUtil.e("getDataCounts:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e(Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + e.getMessage());
        }

        return counts;
    }

    /**
     * 消除表中所有数据
     *
     * @param table
     */
    public void clearAllData(String table) throws Exception {
        try {
            if (db.isOpen()) {
                execSql("delete from " + table);
            } else {
                LogUtil.e("clearAllData:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e(Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + e.getMessage());
        }

    }

    /**
     * 插入数据
     *
     * @param sql      执行操作的sql语句
     * @param bindArgs sql中的参数，参数的位置对于占位符的顺序
     * @return 返回插入对应的ID，返回0，则插入无效
     */

    public long insertDataBySql(String sql, String[] bindArgs) {
        long id = 0;
        try {
            if (db.isOpen()) {
                SQLiteStatement sqLiteStatement = db.compileStatement(sql);
                if (bindArgs != null) {
                    int size = bindArgs.length;
                    for (int i = 0; i < size; i++) {
                        sqLiteStatement.bindString(i + 1, bindArgs[i]);
                    }
                    id = sqLiteStatement.executeInsert();
                    sqLiteStatement.close();
                }
            } else {
                LogUtil.e("insertDataBySql:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e("insertDataBySql:" + e.getMessage());
        }
        return id;
    }

    /**
     * 插入数据
     *
     * @param table  表名
     * @param values 数据
     * @return 返回插入的ID，返回0，则插入失败
     */
    public long insertData(String table, ContentValues values) {
        long id = 0;
        try {
            if (db.isOpen()) {
                id = db.insertOrThrow(table, null, values);
            } else {
                LogUtil.e("insetData:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e("insertData:" + e.getMessage());
        }
        return id;
    }


    /**
     * 更新数据
     *
     * @param table        表名
     * @param values       需要更新的数据
     * @param whereClaause 表示sql语句中条件部分的语句
     * @param whereArgs    表示占位符的值
     * @return 受影响的行数
     */
    public int updateData(String table, ContentValues values, String whereClaause, String[] whereArgs) {
        int rowsNum = 0;
        try {
            if (db.isOpen()) {
                rowsNum = db.update(table, values, whereClaause, whereArgs);
            } else {
                LogUtil.e("updateData:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e("updateData:" + e.getMessage());
        }
        return rowsNum;
    }

    /**
     * 删除数据
     *
     * @param sql      待执行的sql语句
     * @param bindArgs sql语句中的参数，参数的顺序对应占位符的顺序
     */
    public void deleteDataBySql(String sql, String[] bindArgs) {
        try {
            if (db.isOpen()) {
                SQLiteStatement statement = db.compileStatement(sql);
                if (bindArgs != null) {
                    int size = bindArgs.length;
                    for (int i = 0; i < size; i++) {
                        statement.bindString(i + 1, bindArgs[i]);
                    }
                    statement.execute();
                    statement.close();
                }
            } else {
                LogUtil.e("deleteDataBySql:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e("deleteDataBySql:" + e.getMessage());
        }
    }

    /**
     * 删除数据
     *
     * @param table       表名
     * @param whereClause sql中的条件语句部分
     * @param whereArgs   占位符的值
     * @return
     */
    public long deleteData(String table, String whereClause, String[] whereArgs) {
        long rowsNum = 0;
        try {
            if (db.isOpen()) {
                rowsNum = db.delete(table, whereClause, whereArgs);
            } else {
                LogUtil.e("deleteData:The DataBase has already closed");
            }
        } catch (Exception e) {
            LogUtil.e("deleteData:" + e.getMessage());
        }

        return rowsNum;
    }

    /**
     * 数据查询
     *
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, Integer limit) {
        Cursor cursor = null;
        try {
            if (limit != null) {
                cursor = dbHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit + "");
            } else {
                cursor = dbHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            }
        } catch (RuntimeException e) {
            LogUtil.e("query:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cursor;
    }

}