package com.ftrend.zgp.utils.db;

import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.HandoverPay;
import com.ftrend.zgp.model.TradeProd;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
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
    public static final int DATABASE_VERSION = 5;

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

    @Migration(version = 2, database = ZgpDb.class, priority = 1)
    public static class Migration2_TradeProd extends AlterTableMigration<TradeProd> {

        public Migration2_TradeProd(Class<TradeProd> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "prodForDsc");
            addColumn(SQLiteType.INTEGER, "prodPriceFlag");
            addColumn(SQLiteType.INTEGER, "prodIsLargess");
            addColumn(SQLiteType.REAL, "prodMinPrice");
        }
    }

    @Migration(version = 2, database = ZgpDb.class, priority = 2)
    public static class Migration2 extends BaseMigration {
        @Override
        public void migrate(DatabaseWrapper database) {
            DbUpdateHelper.update_2(database);
        }
    }

    @Migration(version = 3, database = ZgpDb.class, priority = 1)
    public static class Migration3 extends BaseMigration {
        @Override
        public void migrate(DatabaseWrapper database) {

        }
    }

    @Migration(version = 4, database = ZgpDb.class, priority = 1)
    public static class Migration4 extends BaseMigration {
        @Override
        public void migrate(DatabaseWrapper database) {

        }
    }

    @Migration(version = 4, database = ZgpDb.class, priority = 2)
    public static class Migration4_HandoverPay extends AlterTableMigration<HandoverPay> {

        public Migration4_HandoverPay(Class<HandoverPay> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "cashier");
            addColumn(SQLiteType.TEXT, "cashierName");
            addColumn(SQLiteType.TEXT, "payTypeName");
        }
    }

    @Migration(version = 5, database = ZgpDb.class, priority = 1)
    public static class Migration5_DepProduct extends AlterTableMigration<DepProduct> {

        public Migration5_DepProduct(Class<DepProduct> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "prodStatus");
            addColumn(SQLiteType.TEXT, "season");
        }
    }

}
