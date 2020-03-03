package com.ftrend.zgp.utils.db;

import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

/**
 * 数据库事务工具类
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/12
 */
public class TransHelper {

    /**
     * 同步执行事务，返回是否成功
     *
     * @param runner
     * @return
     */
    public static boolean transSync(final TransRunner runner) {
        final boolean[] result = {false};
        FlowManager.getDatabase(ZgpDb.class).executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                databaseWrapper.beginTransaction();
                try {
                    if (runner.execute(databaseWrapper)) {
                        databaseWrapper.setTransactionSuccessful();
                        result[0] = true;
                    }
                } catch (Exception e) {
                    LogUtil.e("数据库操作异常：" + e.getLocalizedMessage());
                } finally {
                    databaseWrapper.endTransaction();
                }
            }
        });
        return result[0];
    }

    /**
     * 事务执行器
     */
    public interface TransRunner {
        /**
         * 执行数据库操作，返回是否执行成功
         *
         * @param databaseWrapper
         * @return
         */
        boolean execute(DatabaseWrapper databaseWrapper);
    }
}
