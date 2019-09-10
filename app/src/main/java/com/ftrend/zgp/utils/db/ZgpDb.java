package com.ftrend.zgp.utils.db;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

/**
 * 数据库创建、升级工具
 *
 * @author liziqiang@ftrend.cn
 */
@Database(version = ZgpDb.DATABASE_VERSION, name = ZgpDb.DATABASE_NAME)
public class ZgpDb {
    private static String TAG = "ZgpDb";

    // 数据库名称
    public static final String DATABASE_NAME = "zgp";
    // 数据库版本号
    public static final int DATABASE_VERSION = 1;

    @Migration(version = 0, database = ZgpDb.class, priority = 0)
    public static class Migration0 extends BaseMigration {
        @Override
        public void migrate(DatabaseWrapper database) {
//            Log.e(TAG, "migrate: 0");
            // 注意：第一次运行，创建数据库时只会执行Migration0！！
            DbUpdateHelper.initDb(database);
//            DbUpdateHelper.update_2(database);
        }
    }

/*    @Migration(version = 2, database = ZgpDb.class, priority = 0)
    public static class Migration2 extends BaseMigration {
        @Override
        public void migrate(DatabaseWrapper database) {
//            Log.e(TAG, "migrate: 2");
            DbUpdateHelper.update_2(database);
        }
    }*/

}
